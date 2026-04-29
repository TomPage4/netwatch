package com.example.mob_dev_portfolio.features.discovery

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log
import com.example.mob_dev_portfolio.data.constants.ScanType
import com.example.mob_dev_portfolio.data.dto.InetAddressSubnetMaskDTO
import com.example.mob_dev_portfolio.data.dto.PortResultDTO
import com.example.mob_dev_portfolio.data.dto.ResolvedServiceDTO
import com.example.mob_dev_portfolio.data.dto.ServiceUpdateDTO
import com.example.mob_dev_portfolio.data.entity.ScanState
import com.example.mob_dev_portfolio.data.entity.ScanTypeEntity
import com.example.mob_dev_portfolio.data.entity.ServiceTypeEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.xbill.DNS.Lookup
import org.xbill.DNS.PTRRecord
import org.xbill.DNS.ReverseMap
import org.xbill.DNS.SimpleResolver
import org.xbill.DNS.Type
import java.io.IOException
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.time.Duration
import java.time.LocalDateTime
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceInfo
import javax.jmdns.ServiceListener


class Discovery(
    appContext: Context,
    private val scope: CoroutineScope
) {
    private val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _scanState = MutableStateFlow(ScanState.Idle)
    val scanState = _scanState.asStateFlow()

    private val _updates = MutableSharedFlow<ServiceUpdateDTO>(extraBufferCapacity = 256)
    val updates: SharedFlow<ServiceUpdateDTO> = _updates

    private val _serviceTypes = MutableStateFlow<List<ServiceTypeEntity>>(emptyList())
    private val _scanTypes = MutableStateFlow<List<ScanTypeEntity>>(emptyList())

    private var jmDNS: JmDNS? = null
    private var multicastLock: WifiManager.MulticastLock? = null

    private val listeners = mutableMapOf<String, ServiceListener>()

    fun updateServiceTypes(types: List<ServiceTypeEntity>) {
        _serviceTypes.value = types
    }

    fun updateScanTypes(types: List<ScanTypeEntity>) {
        _scanTypes.value = types
    }

    private fun isScanTypeEnabled(scanType: String): Boolean {
        return _scanTypes.value.any { it.scanType == scanType && it.selected }
    }

    fun normaliseType(raw: String): String {
        val cleaned = raw.removePrefix(".").removeSuffix(".").removeSuffix(".local")
        return "$cleaned.local."
    }

    private fun acquireMulticastLock() {
        if (multicastLock?.isHeld == true) return

        multicastLock = wifiManager
            .createMulticastLock("mob_dev_nsd")
            .apply {
                setReferenceCounted(false)
                acquire()
            }
    }

    private fun releaseMulticastLock() {
        try {
            if (multicastLock?.isHeld == true) multicastLock?.release()
        } catch (_: Exception) {
        } finally {
            multicastLock = null
        }
    }

    fun startScan() {
        if (_scanState.value != ScanState.Idle) return

        _scanState.value = ScanState.Scanning
        acquireMulticastLock()

        listeners.clear()

        jmDNS = createJmDns() ?: return

        if (isScanTypeEnabled(ScanType.SERVICE_DISCOVERY)) {
            for (type in _serviceTypes.value) {
                val typeString = normaliseType(type.serviceType)

                val listener = createServiceListener()
                listeners[typeString] = listener

                try {
                    jmDNS?.addServiceListener(typeString, listener)
                } catch (e: Exception) {
                    _scanState.value = ScanState.Error
                }
            }
        }
        if (isScanTypeEnabled(ScanType.HOST_SCAN)) {
            subnetScan()
        }
    }

    private fun getInetAddressAndSubnetMask(): InetAddressSubnetMaskDTO? {
        try {
            val linkProperties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork) ?: return null
            val subnet = linkProperties.linkAddresses.firstOrNull { it.address is Inet4Address } ?: return null
            return InetAddressSubnetMaskDTO(subnet.address, subnet.prefixLength)
        } catch (e: Exception) {
            _scanState.value = ScanState.Error
            releaseMulticastLock()
            return null
        }
    }

    private fun createJmDns(): JmDNS? {
        return try {
            val inetAddress = getInetAddressAndSubnetMask()?.inetAddress ?: throw IOException()
            JmDNS.create(inetAddress)
        } catch (e: IOException) {
            _scanState.value = ScanState.Error
            releaseMulticastLock()
            null
        }
    }

    fun stopScan() {
        if (_scanState.value != ScanState.Scanning) return

        _scanState.value = ScanState.Stopping

        scope.launch {
            listeners.forEach { (type, listener) ->
                try {
                    jmDNS?.removeServiceListener(type, listener)
                } catch (_: Exception) { }
            }
            listeners.clear()

            try {
                jmDNS?.close()
            } catch (_: Exception) { }

            jmDNS = null
            releaseMulticastLock()
            _scanState.value = ScanState.Idle
        }
    }

    private fun createServiceListener() = object : ServiceListener {

        override fun serviceAdded(event: ServiceEvent) {
            val name = event.name ?: return
            val type = event.type ?: return

            _updates.tryEmit(
                ServiceUpdateDTO.Found(name, type, LocalDateTime.now())
            )
        }

        override fun serviceRemoved(event: ServiceEvent) {
            val name = event.name ?: return
            val type = event.type ?: return

            _updates.tryEmit(
                ServiceUpdateDTO.Lost(name, type, LocalDateTime.now())
            )
        }

        override fun serviceResolved(event: ServiceEvent) {
            val info = event.info ?: return

            _updates.tryEmit(
                ServiceUpdateDTO.Updated(
                    service = toResolvedService(info),
                    updatedAt = LocalDateTime.now()
                )
            )
        }
    }

    fun toResolvedService(info: ServiceInfo): ResolvedServiceDTO {
        val address = info.inetAddresses.firstOrNull()
        return ResolvedServiceDTO(
            name = info.name,
            type = info.type,
            port = info.port,
            ipAddress = address?.hostAddress,
            server = info.server?.removeSuffix(".")?.removeSuffix(".local")
        )
    }

    private fun subnetScan() {
        val subnet = getInetAddressAndSubnetMask() ?: return
//        if (subnet.subnetMask != 24) return

        val hostAddress = subnet.inetAddress.hostAddress ?: return
        val baseIp = hostAddress.split(".").dropLast(1).joinToString(".")

        val semaphore = Semaphore(permits = 50)

        val ports = arrayOf(80, 22, 23, 21, 8080, 443, 5000, 554)

        for (i in 1..255) {
            val ip = "$baseIp.$i"

            scope.launch {
                semaphore.withPermit {
                    try {
                        val inetAddress = InetAddress.getByName(ip)
                        val ipAddress = inetAddress.hostAddress

                        if (inetAddress.isReachable(600) && ipAddress != null) {
                            reverseLookup(ipAddress, "${baseIp}.1")

                            for (port in ports) {
                                val portResult = portScan(ipAddress, port)

                                if (portResult.isOpen) {
                                    _updates.tryEmit(
                                        ServiceUpdateDTO.PortScan(portResult, ipAddress, LocalDateTime.now())
                                    )
                                }
                            }
                        }
                    } catch (_: Exception) {}
                }
            }
        }
    }

    private fun reverseLookup(ipAddress: String, dnsServer: String) {
        try {
            val resolver = SimpleResolver(dnsServer).apply {
                timeout = Duration.ofSeconds(3)
            }

            val reverseName = ReverseMap.fromAddress(ipAddress)

            val lookup = Lookup(reverseName, Type.PTR)
            lookup.setResolver(resolver)
            lookup.run()

            val hostNameString = lookup.answers?.mapNotNull {
                    (it as? PTRRecord)?.target?.toString()
                }?.takeIf {
                    it.isNotEmpty()
                }?.joinToString(", ")

            _updates.tryEmit(
                ServiceUpdateDTO.HostDiscovered(hostNameString, ipAddress, LocalDateTime.now())
            )

        } catch (_: Exception) {
            _updates.tryEmit(
                ServiceUpdateDTO.HostDiscovered(null, ipAddress, LocalDateTime.now())
            )
        }
    }

    private fun portScan(ipAddress: String, port: Int): PortResultDTO {
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(ipAddress, port), 200)
            socket.soTimeout = 300

            var response: String? = null

            try {
                if (port == 80 || port == 8080) {
                    val output = socket.getOutputStream().bufferedWriter()
                    output.write("GET / HTTP/1.0\r\nHost: $ipAddress\r\n\r\n")
                    output.flush()

                    val inputStream = socket.getInputStream()
                    val reader = inputStream.bufferedReader()
                    val lines = mutableListOf<String>()

                    for (i in 1..15) {
                        val line = reader.readLine() ?: break
                        lines.add(line)
                    }

                    response = lines.joinToString("\n")
                } else {
                    response = "OPEN (non-HTTP)"
                }

            } catch (_: Exception) {}

            socket.close()
            return PortResultDTO(port, isOpen = true, response = response)

        } catch (_: Exception) {
            return PortResultDTO(port, isOpen = false)
        }
    }
}