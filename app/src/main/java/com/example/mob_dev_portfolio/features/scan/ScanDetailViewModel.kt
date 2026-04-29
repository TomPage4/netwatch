package com.example.mob_dev_portfolio.features.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.constants.ServiceType
import com.example.mob_dev_portfolio.data.dto.ScanDeviceHistoryWithDeviceDTO
import com.example.mob_dev_portfolio.data.dto.ScanServiceWithTrustedDTO
import com.example.mob_dev_portfolio.data.entity.NetworkEntity
import com.example.mob_dev_portfolio.data.entity.RiskRating
import com.example.mob_dev_portfolio.data.entity.ScanDeviceHistoryEntity
import com.example.mob_dev_portfolio.data.entity.ScanEntity
import com.example.mob_dev_portfolio.data.entity.ScanServiceHistoryEntity
import com.example.mob_dev_portfolio.features.network.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class ScanDetailViewModel(
    private val scanRepository: ScanRepository,
    private val scanServiceHistoryRepository: ScanServiceHistoryRepository,
    private val scanDeviceHistoryRepository: ScanDeviceHistoryRepository,
    private val networkRepository: NetworkRepository
): ViewModel() {

    private val _scan = MutableStateFlow<ScanEntity?>(null)
    val scan: StateFlow<ScanEntity?> = _scan.asStateFlow()

    private val _services = MutableStateFlow<List<ScanServiceHistoryEntity>>(emptyList())
    val services: StateFlow<List<ScanServiceHistoryEntity>> = _services.asStateFlow()

    private val _devices = MutableStateFlow<List<ScanDeviceHistoryWithDeviceDTO>>(emptyList())
    val devices: StateFlow<List<ScanDeviceHistoryWithDeviceDTO>> = _devices.asStateFlow()

    private val _network = MutableStateFlow<NetworkEntity?>(null)
    val network: StateFlow<NetworkEntity?> = _network.asStateFlow()

    fun scanRedirect(scanId: Long) {
        viewModelScope.launch {
            val scanEntity = scanRepository.getScanById(scanId)
            _scan.value = scanEntity

            scanEntity.let {
                _network.value = networkRepository.getNetworkById(it.networkId)
            }
        }

        viewModelScope.launch {
            scanServiceHistoryRepository.observeServicesByScanId(scanId)
                .collectLatest { services ->
                    _services.value = services
                }
        }

        viewModelScope.launch {
            scanDeviceHistoryRepository.observeDevicesByScanId(scanId)
                .collectLatest { devices ->
//                    _devices.value = devices
                    _devices.value = devices.sortedByDescending {
                        it.history.ipAddress == "00.00.00.00"
                    }
                }
        }
    }

    fun onCopyDetailsClick(
        scan: ScanEntity,
        services: List<ScanServiceHistoryEntity>,
        devices: List<ScanDeviceHistoryWithDeviceDTO>
    ): String {

        return buildString {
            appendLine("=== SCAN DETAILS ===")
            appendLine("Scan ID: ${scan.id}")
            appendLine("Risk Rule At Scan: ${scan.riskRuleAtScan}")
            appendLine("Started At: ${scan.startedAt.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
            appendLine("Ended At: ${scan.endedAt?.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
            appendLine()
            appendLine("=== RELATED DEVICES AND SERVICES ===")
            appendLine()

            for (device in devices) {
                appendLine("== DEVICE DETAILS ==")
                appendLine("Name: ${device.device?.displayName}")
                appendLine("IP Address: ${device.device?.ipAddress}")
                appendLine("First Seen: ${device.device?.firstSeen?.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
                appendLine("Last Seen: ${device.device?.lastSeen?.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
                appendLine()
                appendLine("= RELATED SERVICES =")

                for (service in services) {
                    if (service.deviceId == device.device?.id) {
                        val riskText = when (service.riskRating) {
                            RiskRating.LOW -> "Low"
                            RiskRating.MED -> "Medium"
                            RiskRating.HIGH -> "High"
                            RiskRating.UNRATED -> "Unrated"
                        }

                        val typeConvert = when (service.type) {
                            ServiceType.HTTP -> "HTTP"
                            ServiceType.HTTPS -> "HTTPS"
                            ServiceType.DNS_SD -> "DNS SD"
                            ServiceType.GOOGLE_CAST -> "Google Cast"
                            ServiceType.AIRPLAY -> "Airplay"
                            ServiceType.SPOTIFY_CONNECT -> "Spotify Connect"
                            ServiceType.RAOP -> "RAOP"
                            ServiceType.IPP -> "IPP"
                            else -> "Unknown"
                        }

                        appendLine("Name: ${service.name}")
                        appendLine("Type: ${typeConvert}")
                        appendLine("IP Address: ${service.ipAddress}")
                        appendLine("Port: ${service.port}")
                        appendLine("Status: ${service.resolveStatus}")
                        appendLine("New: ${service.isNew}")
                        appendLine("Changed: ${service.isChanged}")
                        appendLine("First Seen: ${service.firstSeen.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
                        appendLine("Last Seen: ${service.lastSeen.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
                        appendLine("Last Changed: ${service.lastChanged?.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
                        appendLine("Risk Rating: $riskText")
                        appendLine("Risk Rule: ${service.riskRuleAtRating}")
                        appendLine("Risk Findings:")
                        if (service.riskFinding.isEmpty()) {
                            appendLine("None")
                        } else {
                            for (finding in service.riskFinding) {
                                val severityText = when (finding.severity) {
                                    RiskRating.LOW -> "Low"
                                    RiskRating.MED -> "Medium"
                                    RiskRating.HIGH -> "High"
                                    RiskRating.UNRATED -> "Unrated"
                                }

                                appendLine(" • ${finding.title} ($severityText)")
                            }
                        }
                        appendLine()
                    }
                }
            }
        }
    }
}