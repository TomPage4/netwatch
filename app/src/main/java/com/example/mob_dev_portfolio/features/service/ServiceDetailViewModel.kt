package com.example.mob_dev_portfolio.features.service

import androidx.lifecycle.ViewModel
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.constants.ServiceType
import com.example.mob_dev_portfolio.data.converter.toServiceDetailDTO
import com.example.mob_dev_portfolio.data.dto.ServiceDetailDTO
import com.example.mob_dev_portfolio.data.entity.RiskRating
import com.example.mob_dev_portfolio.features.device.DeviceRepository
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

class ServiceDetailViewModel (
    private val deviceRepository: DeviceRepository
): ViewModel() {

    private val _service = MutableStateFlow<ServiceDetailDTO?>(null)
    val service: StateFlow<ServiceDetailDTO?> = _service.asStateFlow()

    fun serviceRedirect(service: ServiceEntity) {

        viewModelScope.launch {
            _service.value = service.toServiceDetailDTO(
                deviceRepository.getDeviceById(service.deviceId)
            )
        }
    }

    fun onCopyDetailsClick(service: ServiceDetailDTO): String {
        return buildString {

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

            appendLine("=== SERVICE DETAILS ===")
            appendLine("Name: ${service.name}")
            appendLine("Type: ${typeConvert}")
            appendLine("Hostname: ${service.hostname}")
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

            appendLine()
            appendLine("=== DEVICE DETAILS ===")
            appendLine("Name: ${service.device?.displayName}")
            appendLine("IP Address: ${service.device?.ipAddress}")
            appendLine("First Seen: ${service.device?.firstSeen?.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
            appendLine("Last Seen: ${service.device?.lastSeen?.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"))}")
        }
    }
}