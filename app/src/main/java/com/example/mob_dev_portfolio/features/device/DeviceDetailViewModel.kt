package com.example.mob_dev_portfolio.features.device

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.mob_dev_portfolio.data.constants.ServiceType
import com.example.mob_dev_portfolio.data.converter.RiskFindingConverter
import com.example.mob_dev_portfolio.data.converter.toServiceEntity
import com.example.mob_dev_portfolio.data.dto.ServiceDetailDTO
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.DeviceEventEntity
import com.example.mob_dev_portfolio.data.entity.RiskRating
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.features.scan.ScanServiceHistoryRepository
import com.example.mob_dev_portfolio.features.service.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class DeviceDetailViewModel(
    private val serviceRepository: ServiceRepository,
    private val deviceEventRepository: DeviceEventRepository,
    private val scanServiceHistoryRepository: ScanServiceHistoryRepository,
    private val deviceRepository: DeviceRepository
): ViewModel() {

    private val _device = MutableStateFlow<DeviceEntity?>(null)
    val device: StateFlow<DeviceEntity?> = _device.asStateFlow()

    private val _services = MutableStateFlow<List<ServiceEntity>>(emptyList())
    val services: StateFlow<List<ServiceEntity>> = _services.asStateFlow()

    fun deviceRedirect(device: DeviceEntity, isPreviousScan: Boolean = false, scanId: Long = 0L) {
        viewModelScope.launch {
            _device.value = device

        }

        viewModelScope.launch {
            if (!isPreviousScan) {
                serviceRepository.observeServicesByDeviceId(device.id)
                    .collectLatest { services ->
                        _services.value = services
                    }
            } else {
                scanServiceHistoryRepository.observeServicesByDeviceIdAndScanId(device.id, scanId)
                    .collectLatest { services ->
                        _services.value = services.map {
                            it.toServiceEntity()
                        }
                    }
            }
        }
    }

    fun onMarkTrustedClick(device: DeviceEntity) {
        viewModelScope.launch {
            deviceRepository.markTrusted(device)
            _device.value = device.copy(isTrusted = true)
        }
    }

    fun onRemoveTrustedClick(device: DeviceEntity) {
        viewModelScope.launch {
            deviceRepository.removeTrusted(device)
            _device.value = device.copy(isTrusted = false)
        }
    }

    fun onCopyDetailsClick(device: DeviceEntity, services: List<ServiceEntity>): String {

        return buildString {
            appendLine("=== DEVICE DETAILS ===")
            appendLine("Name: ${device.displayName}")
            appendLine("IP Address: ${device.ipAddress}")
            appendLine("First Seen: ${device.firstSeen.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
            appendLine("Last Seen: ${device.lastSeen.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
            appendLine()
            appendLine("=== RELATED SERVICES (${_services.value.size}) ===")

            for (service in services) {
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

                appendLine()
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
                    service.riskFinding.forEach { finding ->
                        val severityText = when (finding.severity) {
                            RiskRating.LOW -> "Low"
                            RiskRating.MED -> "Medium"
                            RiskRating.HIGH -> "High"
                            RiskRating.UNRATED -> "Unrated"
                        }

                        appendLine(" • ${finding.title} ($severityText)")
                    }
                }
            }
        }
    }
}