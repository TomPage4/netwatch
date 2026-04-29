package com.example.mob_dev_portfolio.features.network

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.entity.NetworkEntity
import java.time.LocalDateTime
import androidx.core.content.ContextCompat
import android.Manifest
import android.net.Network
import android.net.NetworkRequest
import com.example.mob_dev_portfolio.data.dto.NetworkWithDeviceCountDTO
import com.example.mob_dev_portfolio.data.dto.WiFiIdentityDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

class NetworkRepository(
    private val appContext: Context,
    private val db: AppDatabase
) {

    suspend fun getNetworkById(id: Long): NetworkEntity? {
        return db.networkDao().getNetworkById(id)
    }

    fun observeNetworks(): Flow<List<NetworkWithDeviceCountDTO>> {
        return db.networkDao().observeNetworksWithDeviceCount()
    }

    private suspend fun refreshNetworkAndGet(increment: Boolean): NetworkEntity? {
        val wifiIdentity = readCurrentWiFiIdentity() ?: return null
        val ssid = wifiIdentity.ssid ?: return null
        val bssid = wifiIdentity.bssid

        if (ssid == "<unknown ssid>") {
            return null
        }

        if (bssid == "02:00:00:00:00:00") {
            return null
        }

        val now = LocalDateTime.now()

        val existing = db.networkDao().getNetworkBySsid(ssid)

        return if (existing == null) {
            val newEntity = NetworkEntity(
                ssid = ssid,
                bssid = bssid,
                securityType = wifiIdentity.securityType ?: WifiInfo.SECURITY_TYPE_UNKNOWN,
                firstSeen = now,
                lastSeen = now,
                totalScans = if (increment) {
                    1
                } else {
                    0
                }
            )
            val newId = db.networkDao().insert(newEntity)
            newEntity.copy(id = newId)
        } else {
            val updated = existing.copy(
                bssid = bssid ?: existing.bssid,
                securityType = wifiIdentity.securityType ?: existing.securityType,
                lastSeen = now,
                totalScans = if (increment) {
                    existing.totalScans + 1
                } else {
                    existing.totalScans
                }
            )
            db.networkDao().update(updated)
            updated
        }
    }

    suspend fun refreshCurrentNetworkAndGet() = refreshNetworkAndGet(increment = false)
    suspend fun incrementScanCountAndGet() = refreshNetworkAndGet(increment = true)

    private suspend fun readCurrentWiFiIdentity(): WiFiIdentityDTO? {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val active = connectivityManager.activeNetwork ?: return null

        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        val request = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()

        val result = withTimeoutOrNull(1500L) {
            suspendCancellableCoroutine { context ->
                val callback = object : ConnectivityManager.NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {
                    override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                        if (network != active) {
                            return
                        }

                        val wifiInfo = capabilities.transportInfo as? WifiInfo

                        val readSsid =  wifiInfo?.ssid?.removePrefix("\"")?.removeSuffix("\"")
                        val readBssid = wifiInfo?.bssid

                        val ssid = if (readSsid == "<unknown ssid>") {
                            "<unknown ssid>"
                        } else {
                            readSsid
                        }

                        val bssid = if (readBssid == "02:00:00:00:00:00") {
                            "02:00:00:00:00:00"
                        } else {
                            readBssid
                        }

                        if (context.isActive) {
//                            context.resume(WiFiIdentityDTO(ssid, bssid))
                            val securityType = wifiInfo?.currentSecurityType
                            context.resume(WiFiIdentityDTO(ssid, bssid, securityType))
                        }

                        runCatching {
                            connectivityManager.unregisterNetworkCallback(this)
                        }
                    }

                    override fun onUnavailable() {
                        if (context.isActive) {
                            context.resume(null)
                        }
                        runCatching {
                            connectivityManager.unregisterNetworkCallback(this)
                        }
                    }

                    override fun onLost(network: Network) {
                        if (network != active) {
                            return
                        }
                        if (context.isActive) {
                            context.resume(null)
                        }
                        runCatching {
                            connectivityManager.unregisterNetworkCallback(this)
                        }
                    }
                }

                context.invokeOnCancellation {
                    runCatching {
                        connectivityManager.unregisterNetworkCallback(callback)
                    }
                }

                try {
                    connectivityManager.registerNetworkCallback(request, callback)
                } catch (se: SecurityException) {
                    if (context.isActive) {
                        context.resume(null)
                    }
                }
            }
        }

        if (result != null) {
            return result
        }

        val capabilities = connectivityManager.getNetworkCapabilities(active) ?: return null

        if (!capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return null
        }

        val info = capabilities.transportInfo as? WifiInfo ?: return null

        val readSsid = info.ssid?.removePrefix("\"")?.removeSuffix("\"")
        val readBssid = info.bssid

        val ssid = if (readSsid == "<unknown ssid>") {
            "<unknown ssid>"
        } else {
            readSsid
        }

        val bssid = if (readBssid == "02:00:00:00:00:00") {
            "02:00:00:00:00:00"
        } else {
            readBssid
        }

//        return WiFiIdentityDTO(ssid, bssid)
        return WiFiIdentityDTO(ssid, bssid, info.currentSecurityType)
    }
}