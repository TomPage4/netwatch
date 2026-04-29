package com.example.mob_dev_portfolio.features.discovery

import android.content.Context
import androidx.room.withTransaction
import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.constants.EventType
import com.example.mob_dev_portfolio.data.constants.ScanType
import com.example.mob_dev_portfolio.data.dto.ServiceUpdateDTO
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.DeviceEventEntity
import com.example.mob_dev_portfolio.data.entity.ResolveStatus
import com.example.mob_dev_portfolio.data.entity.ScanDeviceHistoryEntity
import com.example.mob_dev_portfolio.data.entity.ScanEntity
import com.example.mob_dev_portfolio.data.entity.ScanServiceHistoryEntity
import com.example.mob_dev_portfolio.data.entity.ScanState
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEventEntity
import com.example.mob_dev_portfolio.features.risk.RiskAssessor
import com.example.mob_dev_portfolio.features.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import com.example.mob_dev_portfolio.data.constants.ServiceType
import com.example.mob_dev_portfolio.data.entity.RiskRating

class DiscoveryRepository(
    appContext: Context,
    private val db: AppDatabase,
    private val networkIdProvider: suspend () -> Long,
    private val settingsRepository: SettingsRepository,
    private val riskAssessor: RiskAssessor
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val discovery = Discovery(
        appContext = appContext,
        scope = scope
    )

    val scanState: StateFlow<ScanState> = discovery.scanState

    private var collectorStarted: Boolean = false

    private var currentNetworkId: Long? = null

    private val ipAddressToDeviceIdCache = ConcurrentHashMap<String, Long>()

    private data class ActiveScan(val scanId: Long, val networkId: Long)

    private var activeScan: ActiveScan? = null

    private val newServiceIdsProcessedInScan = ConcurrentHashMap.newKeySet<Long>()

    suspend fun startScan(): Long = withContext(Dispatchers.IO) {
        ensureCollectorStarted()
        val networkId = networkIdProvider()
        val scanId = db.scanDao().insert(
            ScanEntity(
                networkId = networkId,
                startedAt = LocalDateTime.now(),
                serviceFoundCount = 0,
                deviceFoundCount = 0,
                riskRuleAtScan = settingsRepository.getRiskRule()
            )
        )
        activeScan = ActiveScan(scanId, networkId)
        currentNetworkId = networkId
        discovery.startScan()
        scanId
    }

    fun stopScan() {
        discovery.stopScan()
        currentNetworkId = null
        ipAddressToDeviceIdCache.clear()
        newServiceIdsProcessedInScan.clear()

        val scan = activeScan ?: return
        activeScan = null

        scope.launch {
            db.scanDao().update(
                db.scanDao().findById(scan.scanId).copy(
                    endedAt = LocalDateTime.now()
                )
            )
        }
    }

    private fun ensureCollectorStarted() {
        if (collectorStarted) return
        collectorStarted = true

        scope.launch {
            discovery.updates.collect { update ->
                when (update) {
                    is ServiceUpdateDTO.Found -> handleFound(update)
                    is ServiceUpdateDTO.Updated -> handleUpdated(update)
                    is ServiceUpdateDTO.ResolveFailed -> handleResolveFailed(update)
                    is ServiceUpdateDTO.Lost -> handleLost(update)
                    is ServiceUpdateDTO.HostDiscovered -> handleHostDiscovered(update)
                    is ServiceUpdateDTO.PortScan -> handlePortScan(update)
                }
            }
        }

        scope.launch {
            settingsRepository.observeSelectedServiceTypes().collect { types ->
                discovery.updateServiceTypes(types)
            }
        }
        scope.launch {
            settingsRepository.observeScanTypes().collect { types ->
                discovery.updateScanTypes(types)
            }
        }
    }

    private suspend fun handleFound(found: ServiceUpdateDTO.Found) {
        val networkId = currentNetworkId ?: return
        val now = found.updatedAt
        val scan = activeScan ?: return

        db.withTransaction {
            val riskRule = settingsRepository.getRiskRule()

            var existingDevice = db.deviceDao().findByNetworkIdAndIpAddress(networkId, "00.00.00.00")


            if (existingDevice == null) {
                db.deviceDao().insert(
                    DeviceEntity(
                        networkId = networkId,
                        displayName = "Unresolved Services",
                        ipAddress = "00.00.00.00",
                        serviceCount = 0,
                        isTrusted = false,
                        isNew = false,
                        isChanged = false,
                        scanType = ScanType.SERVICE_DISCOVERY,
                        riskRating = RiskRating.UNRATED,
                        riskFinding = emptyList(),
                        riskRuleAtRating = riskRule,
                        firstSeen = now,
                        lastSeen = now
                    )
                )
                existingDevice = db.deviceDao().findByNetworkIdAndIpAddress(networkId, "00.00.00.00")
                db.scanDeviceHistoryDao().insert(
                    ScanDeviceHistoryEntity(
                        scanId = scan.scanId,
                        deviceId = existingDevice!!.id,
                        networkId = networkId,
                        displayName = "Unresolved Services",
                        ipAddress = "00.00.00.00",
                        serviceCount = 0,
                        isNew = false,
                        isChanged = false,
                        scanType = ScanType.SERVICE_DISCOVERY,
                        riskRating = RiskRating.UNRATED,
                        riskFinding = emptyList(),
                        riskRuleAtRating = riskRule,
                        firstSeen = now,
                        lastSeen = now
                    )
                )
            } else {

                db.deviceDao().update(
                    existingDevice.copy(
                        lastSeen = now
                    )
                )

                val existingDeviceHistory = db.scanDeviceHistoryDao().getDeviceByIdAndScanId(existingDevice.id, scan.scanId)

                if (existingDeviceHistory == null) {
                    db.scanDeviceHistoryDao().insert(
                        ScanDeviceHistoryEntity(
                            scanId = scan.scanId,
                            deviceId = existingDevice.id,
                            networkId = networkId,
                            displayName = "Unresolved Services",
                            ipAddress = "00.00.00.00",
                            serviceCount = 0,
                            isNew = false,
                            isChanged = false,
                            scanType = ScanType.SERVICE_DISCOVERY,
                            riskRating = RiskRating.UNRATED,
                            riskFinding = emptyList(),
                            riskRuleAtRating = riskRule,
                            firstSeen = now,
                            lastSeen = now
                        )
                    )
                } else {
                    db.scanDeviceHistoryDao().update(
                        existingDeviceHistory.copy(
                            lastSeen = now
                        )
                    )
                }
            }

            val existing = db.serviceDao().findByNaturalKey(
                networkId = networkId,
                name = found.name,
                type = found.type
            )

            val serviceId: Long

            val (riskRating, riskFindings) = riskAssessor.serviceAssess(
                serviceType = discovery.normaliseType(found.type),
                ipAddress = null,
                port = null,
                resolveStatus = ResolveStatus.UNRESOLVED,
                serviceName = existing?.name,
                isNewOnKnownNetwork = existing == null && db.networkDao().getCountById(networkId) != 1,
                riskRule = riskRule
            )

            if (existing == null) {
                serviceId = db.serviceDao().insert(
                    ServiceEntity(
                        networkId = networkId,
                        deviceId = existingDevice.id,
                        name = found.name,
                        type = found.type,
                        ipAddress = null,
                        port = null,
                        resolveStatus = ResolveStatus.UNRESOLVED,
                        firstSeen = now,
                        lastSeen = now,
                        lastChanged = null,
                        isNew = true,
                        isChanged = false,
                        riskRating = riskRating,
                        riskFinding = riskFindings,
                        riskRuleAtRating = riskRule
                    )
                )
                db.serviceEventDao().insert(
                    ServiceEventEntity(
                        serviceId = serviceId,
                        timestamp = now,
                        eventType = EventType.FIRST_SEEN
                    )
                )
                db.scanServiceHistoryDao().insert(
                    ScanServiceHistoryEntity(
                        scanId = scan.scanId,
                        networkId = networkId,
                        serviceId = serviceId,
                        deviceId = existingDevice.id,
                        name = found.name,
                        type = found.type,
                        ipAddress = null,
                        port = null,
                        resolveStatus = ResolveStatus.UNRESOLVED,
                        firstSeen = now,
                        lastSeen = now,
                        lastChanged = null,
                        isNew = true,
                        isChanged = false,
                        riskRating = riskRating,
                        riskFinding = riskFindings,
                        riskRuleAtRating = riskRule
                    )
                )
            } else {
                serviceId = existing.id
                db.serviceDao().update(
                    existing.copy(
                        isNew = false,
                        isChanged = false,
                        lastSeen = now
                    )
                )
                db.scanServiceHistoryDao().insert(
                    ScanServiceHistoryEntity(
                        scanId = scan.scanId,
                        networkId = networkId,
                        serviceId = serviceId,
                        deviceId = existingDevice.id,
                        name = found.name,
                        type = found.type,
                        ipAddress = null,
                        port = null,
                        resolveStatus = ResolveStatus.UNRESOLVED,
                        firstSeen = existing.firstSeen,
                        lastSeen = now,
                        lastChanged = null,
                        isNew = false,
                        isChanged = false,
                        riskRating = riskRating,
                        riskFinding = riskFindings,
                        riskRuleAtRating = riskRule
                    )
                )
            }

            val newServiceCount = db.serviceDao().countByDeviceId(existingDevice.id)
            db.deviceDao().update(
                existingDevice.copy(
                    serviceCount = newServiceCount
                )
            )
            val currentDeviceHistory = db.scanDeviceHistoryDao().getDeviceByIdAndScanId(existingDevice.id, scan.scanId)
            if (currentDeviceHistory != null) {
                val newScanServiceCount = db.scanServiceHistoryDao().countDistinctByDeviceAndScan(existingDevice.id, scan.scanId)

                db.scanDeviceHistoryDao().update(
                    currentDeviceHistory.copy(
                        serviceCount = newScanServiceCount
                    )
                )
            }

            val existingScan = db.scanDao().findById(scan.scanId)
            db.scanDao().update(
                existingScan.copy(serviceFoundCount = existingScan.serviceFoundCount + 1)
            )
        }
    }

    private suspend fun handleUpdated(updated: ServiceUpdateDTO.Updated) {
        val networkId = currentNetworkId ?: return
        val now = updated.updatedAt
        val scan = activeScan ?: return

        val service = updated.service

        val name = service.name
        val type = service.type
        val ipAddress = service.ipAddress
        val hostname = service.server
        val port = if (service.port > 0) {
            service.port
        } else {
            null
        }

        val newStatus = when {
            ipAddress != null && port != null -> ResolveStatus.RESOLVED
            ipAddress != null || port != null || hostname != null -> ResolveStatus.PARTIAL
            else -> ResolveStatus.UNRESOLVED
        }

        db.withTransaction {
            val existing = db.serviceDao().findByNaturalKey(
                networkId = networkId,
                name = name,
                type = discovery.normaliseType(type)
            ) ?: return@withTransaction

            var existingScanDevice: ScanDeviceHistoryEntity?

            val deviceId: Long

            if (ipAddress != null) {
                deviceId = resolveDeviceId(networkId, ipAddress, hostname, now, scan.scanId)

                existingScanDevice = db.scanDeviceHistoryDao().getDeviceByIdAndScanId(deviceId, scan.scanId)

                if (hostname != null) {
                    val existingDevice = db.deviceDao().findById(deviceId)
                    if (existingDevice != null && existingDevice.displayName != hostname) {
                        db.deviceDao().update(
                            existingDevice.copy(
                                displayName = hostname
                            )
                        )
                        db.scanDeviceHistoryDao().update(
                            existingScanDevice!!.copy(
                                displayName = hostname
                            )
                        )
                        existingScanDevice = db.scanDeviceHistoryDao().getDeviceByIdAndScanId(deviceId, scan.scanId)
                    }
                }
            } else {
                deviceId = existing.deviceId
                existingScanDevice = db.scanDeviceHistoryDao().getDeviceByIdAndScanId(deviceId, scan.scanId)
            }
            val resolvedIp = ipAddress ?: existing.ipAddress ?: return@withTransaction
            val resolvedPort = port ?: existing.port

            if (ipAddress != existing.ipAddress) {
                db.serviceEventDao().insert(
                    ServiceEventEntity(
                        serviceId = existing.id,
                        timestamp = now,
                        eventType = EventType.IP_UPDATED,
                        change = "${existing.ipAddress} -> $ipAddress"
                    )
                )
            }
            if (port != existing.port) {
                db.serviceEventDao().insert(
                    ServiceEventEntity(
                        serviceId = existing.id,
                        timestamp = now,
                        eventType = EventType.PORT_UPDATED,
                        change = "${existing.port} -> $port"
                    )
                )
            }

            val hasChanged = resolvedIp != existing.ipAddress || resolvedPort != existing.port || newStatus != existing.resolveStatus

            val riskRule = settingsRepository.getRiskRule()
            val (riskRating, riskFinding) = riskAssessor.serviceAssess(
                serviceType = discovery.normaliseType(type),
                ipAddress = resolvedIp,
                port = resolvedPort,
                resolveStatus = newStatus,
                serviceName = existing.name,
                isNewOnKnownNetwork = existing.isNew && db.networkDao().getCountById(networkId) != 1,
                riskRule = riskRule
            )

            val lastChanged = if (!existing.isNew && hasChanged) now else existing.lastChanged
            val isChanged = !existing.isNew && (existing.isChanged || hasChanged)
            val oldDeviceId = existing.deviceId

            db.serviceDao().update(
                existing.copy(
                    deviceId = deviceId,
                    ipAddress = resolvedIp,
                    port = resolvedPort,
                    resolveStatus = newStatus,
                    lastSeen = now,
                    lastChanged = lastChanged,
                    isChanged = isChanged,
                    riskRating = riskRating,
                    riskFinding = riskFinding,
                    riskRuleAtRating = riskRule
                )
            )

            val scanService = db.scanServiceHistoryDao().findByServiceIdAndScanId(existing.id, scan.scanId)
            if (scanService != null) {
                db.scanServiceHistoryDao().update(
                    scanService.copy(
                            deviceId = deviceId,
                            ipAddress = resolvedIp,
                            port = resolvedPort,
                            resolveStatus = newStatus,
                            lastSeen = now,
                            lastChanged = lastChanged,
                            isChanged = isChanged,
                            riskRating = riskRating,
                            riskFinding = riskFinding,
                            riskRuleAtRating = riskRule
                        )
                )
            }

            val unresolvedDeviceId = db.deviceDao()
                .findByNetworkIdAndIpAddress(networkId, "00.00.00.00")
                ?.id

            val affectedDeviceIds = mutableSetOf<Long>()

            affectedDeviceIds.add(oldDeviceId)
            affectedDeviceIds.add(deviceId)

            if (unresolvedDeviceId != null) {
                affectedDeviceIds.add(unresolvedDeviceId)
            }

            affectedDeviceIds.forEach { id ->
                db.deviceDao().findById(id)?.let { device ->
                    val count = db.serviceDao().countByDeviceId(id)
                    db.deviceDao().update(
                        device.copy(
                            serviceCount = count
                        )
                    )
                }

                db.scanDeviceHistoryDao().getDeviceByIdAndScanId(id, scan.scanId)
                    ?.let { scanDevice ->
                        val count = db.scanServiceHistoryDao().countDistinctByDeviceAndScan(id, scan.scanId)
                        db.scanDeviceHistoryDao().update(
                            scanDevice.copy(
                                serviceCount = count
                            )
                        )
                    }
            }

            if (existing.isNew && newServiceIdsProcessedInScan.add(existing.id)) {
                db.deviceEventDao().insert(
                    DeviceEventEntity(
                        deviceId = deviceId,
                        timestamp = now,
                        eventType = EventType.NEW_SERVICE,
                        eventInfo = existing.name
                    )
                )
            }

            val services = db.scanServiceHistoryDao().findByDeviceIdAndScanId(deviceId, scan.scanId)
            val (deviceRiskRating, deviceRiskFindings) = riskAssessor.deviceAssess(
                ipAddress = resolvedIp,
                services = services,
                isNewOnKnownNetwork = existing.isNew && db.networkDao().getCountById(networkId) != 1,
                riskRule = riskRule
            )

            val existingDevice = db.deviceDao().findById(deviceId)

            if (existingDevice != null) {

                val deviceHasChanged = services?.any { it.isChanged } == true
                val deviceIsChanged = !existingDevice.isNew && deviceHasChanged

                db.deviceDao().update(
                    existingDevice.copy(
                        isChanged = deviceIsChanged,
                        riskRating = deviceRiskRating,
                        riskFinding = deviceRiskFindings,
                        riskRuleAtRating = riskRule,
                        lastChanged = if (deviceIsChanged) now else existingDevice.lastChanged
                    )
                )
            }

            if (existingScanDevice != null) {
                val scanDeviceHasChanged = services?.any { it.isChanged } == true
                val scanDeviceIsChanged = !existingScanDevice.isNew && scanDeviceHasChanged
                val serviceCount = db.scanServiceHistoryDao().countDistinctByDeviceAndScan(deviceId, scan.scanId)
                db.scanDeviceHistoryDao().update(
                    existingScanDevice.copy(
                        isChanged = scanDeviceIsChanged,
                        riskRating = deviceRiskRating,
                        riskFinding = deviceRiskFindings,
                        riskRuleAtRating = riskRule,
                        serviceCount = serviceCount,
                        lastChanged = if (scanDeviceIsChanged) now else existingScanDevice.lastChanged
                    )
                )
            }

            if (newStatus != existing.resolveStatus) {
                val resolveEventType = when (newStatus) {
                    ResolveStatus.RESOLVED -> EventType.FULLY_RESOLVED
                    ResolveStatus.PARTIAL -> EventType.PARTIAL_RESOLVE
                    ResolveStatus.UNRESOLVED -> EventType.UNRESOLVED
                }
                db.serviceEventDao().insert(
                    ServiceEventEntity(
                        serviceId = existing.id,
                        timestamp = now,
                        eventType = resolveEventType
                    )
                )
            }
        }
    }

    private suspend fun handleResolveFailed(failed: ServiceUpdateDTO.ResolveFailed) {
        val networkId = currentNetworkId ?: return
        val now = failed.failedAt

        db.withTransaction {
            val existing = db.serviceDao().findByNaturalKey(
                networkId = networkId,
                name = failed.serviceName,
                type = failed.serviceType
            ) ?: return@withTransaction

            db.serviceDao().update(
                existing.copy(
                    resolveStatus = ResolveStatus.UNRESOLVED,
                    lastSeen = now
                )
            )

            db.serviceEventDao().insert(
                ServiceEventEntity(
                    serviceId = existing.id,
                    timestamp = now,
                    eventType = EventType.RESOLVE_FAILED
                )
            )
        }
    }

    private suspend fun handleLost(lost: ServiceUpdateDTO.Lost) {
        val networkId = currentNetworkId ?: return
        val now = lost.updatedAt

        db.withTransaction {
            val existing = db.serviceDao().findByNaturalKey(
                networkId = networkId,
                name = lost.name,
                type = lost.type
            ) ?: return@withTransaction

            db.serviceDao().update(
                existing.copy(
                    lastSeen = now
                )
            )
        }
    }

    private suspend fun resolveDeviceId(
        networkId: Long,
        ipAddress: String,
        hostname: String?,
        now: LocalDateTime,
        scanId: Long
    ): Long {

        ipAddressToDeviceIdCache[ipAddress]?.let { return it }

        val existingDevice = db.deviceDao().findByNetworkIdAndIpAddress(networkId, ipAddress)

        var services: List<ScanServiceHistoryEntity>? = null
        if (existingDevice != null) {
            services = db.scanServiceHistoryDao().findByDeviceIdAndScanId(existingDevice.id, scanId)
        }

        val riskRule = settingsRepository.getRiskRule()
        val (riskRating, riskFindings) = riskAssessor.deviceAssess(
            ipAddress = ipAddress,
            services = services,
            isNewOnKnownNetwork = existingDevice == null && db.networkDao().getCountById(networkId) != 1,
            riskRule = riskRule
        )

        val deviceId = if (existingDevice != null) {
            db.deviceDao().update(
                existingDevice.copy(
                    displayName = hostname ?: existingDevice.displayName,
                    isNew = false,
                    lastSeen = now
                )
            )
            existingDevice.id
        } else {
            db.deviceDao().insert(
                DeviceEntity(
                    networkId = networkId,
                    displayName = hostname ?: ipAddress,
                    ipAddress = ipAddress,
                    serviceCount = 0,
                    isTrusted = false,
                    isNew = true,
                    isChanged = false,
                    scanType = ScanType.SERVICE_DISCOVERY,
                    riskRating = riskRating,
                    riskFinding = riskFindings,
                    riskRuleAtRating = riskRule,
                    firstSeen = now,
                    lastSeen = now
                )
            )
        }

        if (existingDevice == null) {
            db.deviceEventDao().insert(
                DeviceEventEntity(
                    deviceId = deviceId,
                    timestamp = now,
                    eventType = EventType.FIRST_SEEN
                )
            )

            val existingScan = db.scanDao().findById(scanId)
            db.scanDao().update(
                existingScan.copy(
                    deviceFoundCount = existingScan.deviceFoundCount + 1
                )
            )
        }

        if (db.scanDeviceHistoryDao().getDeviceByIdAndScanId(deviceId, scanId) == null) {

            val firstSeen = existingDevice?.firstSeen ?: now
            val isNew = existingDevice == null
            val isChanged = existingDevice?.isChanged ?: false

            db.scanDeviceHistoryDao().insert(
                ScanDeviceHistoryEntity(
                    scanId = scanId,
                    deviceId = deviceId,
                    networkId = networkId,
                    displayName = hostname ?: ipAddress,
                    ipAddress = ipAddress,
                    serviceCount = 0,
                    isNew = isNew,
                    isChanged = isChanged,
                    scanType = ScanType.SERVICE_DISCOVERY,
                    riskRating = riskRating,
                    riskFinding = riskFindings,
                    riskRuleAtRating = riskRule,
                    firstSeen = firstSeen,
                    lastSeen = now
                )
            )
        }

        ipAddressToDeviceIdCache[ipAddress] = deviceId
        return deviceId
    }

    private suspend fun handleHostDiscovered(hostDiscovered: ServiceUpdateDTO.HostDiscovered) {

        val networkId = currentNetworkId ?: return
        val scan = activeScan ?: return
        val existingDevice = db.deviceDao().findByNetworkIdAndIpAddress(networkId, hostDiscovered.ipAddress)

        val riskRule = settingsRepository.getRiskRule()
        val (riskRating, riskFindings) = riskAssessor.deviceAssess(
            ipAddress = hostDiscovered.ipAddress,
            isNewOnKnownNetwork = existingDevice == null && db.networkDao().getCountById(networkId) != 1,
            riskRule = riskRule
        )

        val deviceId = if (existingDevice != null) {
            db.deviceDao().update(
                existingDevice.copy(
                    displayName = hostDiscovered.hostname ?: existingDevice.displayName,
                    isNew = false,
                    lastSeen = hostDiscovered.updatedAt
                )
            )
            existingDevice.id
        } else {
            db.deviceDao().insert(
                DeviceEntity(
                    networkId = networkId,
                    displayName = hostDiscovered.hostname ?: hostDiscovered.ipAddress,
                    ipAddress = hostDiscovered.ipAddress,
                    serviceCount = 0,
                    isTrusted = false,
                    isNew = true,
                    isChanged = false,
                    scanType = ScanType.HOST_DISCOVERY,
                    riskRating = riskRating,
                    riskFinding = riskFindings,
                    riskRuleAtRating = riskRule,
                    firstSeen = hostDiscovered.updatedAt,
                    lastSeen = hostDiscovered.updatedAt
                )
            )
        }

        if (existingDevice == null) {
            db.deviceEventDao().insert(
                DeviceEventEntity(
                    deviceId = deviceId,
                    timestamp = hostDiscovered.updatedAt,
                    eventType = EventType.FIRST_SEEN
                )
            )

            val existingScan = db.scanDao().findById(scan.scanId)
            db.scanDao().update(
                existingScan.copy(
                    deviceFoundCount = existingScan.deviceFoundCount + 1
                )
            )
        }


        if (db.scanDeviceHistoryDao().getDeviceByIdAndScanId(deviceId, scan.scanId) == null) {

            val firstSeen = existingDevice?.firstSeen ?: hostDiscovered.updatedAt
            val isNew = existingDevice == null
            val isChanged = existingDevice?.isChanged ?: false

            db.scanDeviceHistoryDao().insert(
                ScanDeviceHistoryEntity(
                    scanId = scan.scanId,
                    deviceId = deviceId,
                    networkId = networkId,
                    displayName = hostDiscovered.hostname ?: hostDiscovered.ipAddress,
                    ipAddress = hostDiscovered.ipAddress,
                    serviceCount = 0,
                    isNew = isNew,
                    isChanged = isChanged,
                    scanType = ScanType.HOST_DISCOVERY,
                    riskRating = riskRating,
                    riskFinding = riskFindings,
                    riskRuleAtRating = riskRule,
                    firstSeen = firstSeen,
                    lastSeen = hostDiscovered.updatedAt
                )
            )
        }
    }

    private suspend fun handlePortScan(portScan: ServiceUpdateDTO.PortScan) {

        val networkId = currentNetworkId ?: return
        val scan = activeScan ?: return

        val existingDevice = db.deviceDao().findByNetworkIdAndIpAddress(networkId, portScan.ipAddress)

        val deviceId: Long

        if (existingDevice != null) {

            deviceId = existingDevice.id

            val serviceId: Long
            val existingService = db.serviceDao().findByDeviceIdAndPort(existingDevice.id, portScan.portResult.port)
            val riskRule = settingsRepository.getRiskRule()

            val type = when (portScan.portResult.port) {
                23 -> ServiceType.TELNET
                21 -> ServiceType.FTP
                22 -> ServiceType.SSH
                554 -> ServiceType.RTSP
                80 -> ServiceType.HTTP
                8080 -> ServiceType.HTTP
                443 -> ServiceType.HTTPS
                else -> null
            }

            val (serviceRiskRating, serviceRiskFindings) = riskAssessor.serviceAssess(
                ipAddress = portScan.ipAddress,
                serviceType = type,
                port = portScan.portResult.port,
                resolveStatus = ResolveStatus.PARTIAL,
                riskRule = riskRule
            )

            val isNew: Boolean
            val firstSeen: LocalDateTime

            val isChanged: Boolean

            if (existingService == null) {
                isNew = true
                isChanged = false
                firstSeen = portScan.updatedAt
                serviceId = db.serviceDao().insert(
                    ServiceEntity(
                        networkId = networkId,
                        deviceId = deviceId,
                        name = "${portScan.portResult.port}",
                        type = type,
                        ipAddress = portScan.ipAddress,
                        port = portScan.portResult.port,
                        resolveStatus = ResolveStatus.PARTIAL,
                        firstSeen = portScan.updatedAt,
                        lastSeen = portScan.updatedAt,
                        lastChanged = null,
                        isNew = true,
                        isChanged = isChanged,
                        riskRating = serviceRiskRating,
                        riskFinding = serviceRiskFindings,
                        riskRuleAtRating = riskRule,
                        portResponse = portScan.portResult.response
                    )
                )
                db.serviceEventDao().insert(
                    ServiceEventEntity(
                        serviceId = serviceId,
                        timestamp = portScan.updatedAt,
                        eventType = EventType.FIRST_SEEN
                    )
                )
            } else {
                isNew = false
                firstSeen = existingService.firstSeen
                serviceId = existingService.id

                val hasServiceChanged = portScan.ipAddress != existingService.ipAddress || portScan.portResult.port != existingService.port

                isChanged = !existingService.isNew && (existingService.isChanged || hasServiceChanged)

                db.serviceDao().update(
                    existingService.copy(
                        isNew = false,
                        isChanged = isChanged,
                        lastSeen = portScan.updatedAt
                    )
                )
            }

            db.scanServiceHistoryDao().insert(
                ScanServiceHistoryEntity(
                    scanId = scan.scanId,
                    networkId = networkId,
                    serviceId = serviceId,
                    deviceId = deviceId,
                    name = "${portScan.portResult.port}",
                    type = type,
                    ipAddress = portScan.ipAddress,
                    port = portScan.portResult.port,
                    resolveStatus = ResolveStatus.PARTIAL,
                    firstSeen = firstSeen,
                    lastSeen = portScan.updatedAt,
                    lastChanged = null,
                    isNew = isNew,
                    isChanged = isChanged,
                    riskRating = serviceRiskRating,
                    riskFinding = serviceRiskFindings,
                    riskRuleAtRating = riskRule,
                    portResponse = portScan.portResult.response
                )
            )

            val services = db.scanServiceHistoryDao().findByDeviceIdAndScanId(existingDevice.id, scan.scanId)

            val (deviceRiskRating, deviceRiskFindings) = riskAssessor.deviceAssess(
                ipAddress = portScan.ipAddress,
                services = services,
                isNewOnKnownNetwork = false,
                riskRule = riskRule
            )

            db.deviceDao().update(
                existingDevice.copy(
                    riskRating = deviceRiskRating,
                    riskFinding = deviceRiskFindings,
                    riskRuleAtRating = riskRule,
                    serviceCount = db.serviceDao().countByDeviceId(deviceId),
                    lastSeen = portScan.updatedAt
                )
            )

            val existingHistory = db.scanDeviceHistoryDao().getDeviceByIdAndScanId(deviceId, scan.scanId)

            if (existingHistory != null) {
                val historyHasChanged = services?.any { it.isChanged } == true
                val historyIsChanged = !existingHistory.isNew && historyHasChanged

                db.scanDeviceHistoryDao().update(
                    existingHistory.copy(
                        riskRating = deviceRiskRating,
                        riskFinding = deviceRiskFindings,
                        riskRuleAtRating = riskRule,
                        isChanged = historyIsChanged,
                        serviceCount = db.scanServiceHistoryDao().countDistinctByDeviceAndScan(deviceId, scan.scanId),
                        lastSeen = portScan.updatedAt,
                        lastChanged = if (historyIsChanged) portScan.updatedAt else existingHistory.lastChanged
                    )
                )
            }

            db.deviceEventDao().insert(
                DeviceEventEntity(
                    deviceId = deviceId,
                    timestamp = portScan.updatedAt,
                    eventType = EventType.PORT_SCANNED,
                    eventInfo = "${portScan.portResult.port}"
                )
            )
        }
    }
}