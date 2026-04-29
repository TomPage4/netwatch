package com.example.mob_dev_portfolio.features.service

import android.annotation.SuppressLint
import android.content.ClipData
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.data.entity.ResolveStatus
import com.example.mob_dev_portfolio.data.entity.RiskRating
import com.example.mob_dev_portfolio.ui.theme.Green
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.Pink
import com.example.mob_dev_portfolio.ui.theme.Red
import com.example.mob_dev_portfolio.ui.theme.White
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.data.constants.ServiceType
import com.example.mob_dev_portfolio.data.dto.ServiceDetailDTO
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEventEntity
import com.example.mob_dev_portfolio.ui.components.EmptyList
import com.example.mob_dev_portfolio.ui.components.InfoRow
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.components.RiskBadge
import com.example.mob_dev_portfolio.ui.theme.Blue
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.Orange
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ServiceDetailScreen(
    serviceDetailViewModel: ServiceDetailViewModel,
    onCopyDetails: (ServiceDetailDTO) -> String,
    onDeviceClick: (DeviceEntity) -> Unit,
    onLogClick: (ServiceDetailDTO) -> Unit
) {
    val service by serviceDetailViewModel.service.collectAsState()

    Scaffold { _ ->
        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PageHeader(
                heading = "SERVICE DETAILS"
            )

            Spacer(modifier = Modifier.height(8.dp))

            service?.let {
                ActionButtonRow(
                    service = it,
                    onCopyDetails = onCopyDetails,
                    onLogClick = onLogClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            service?.let {
                ServiceInfo(
                    it,
                    onDeviceClick = onDeviceClick
                )
            }
        }
    }
}

@Composable
private fun ActionButtonRow(
    service: ServiceDetailDTO,
    onCopyDetails: (ServiceDetailDTO) -> String,
    onLogClick: (ServiceDetailDTO) -> Unit
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row (
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Button(
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = GreyMid,
                    shape = RoundedCornerShape(8.dp)
                ),
            onClick = {
                onLogClick(service)
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(
                horizontal = 4.dp,
                vertical = 6.dp
            )
        ) {
            Text(
                text = "Logs",
                style = MaterialTheme.typography.bodyMedium,
                color = White
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = GreyMid,
                    shape = RoundedCornerShape(8.dp)
                ),
            onClick = {
                scope.launch {
                    clipboard.setClipEntry(
                        ClipEntry(ClipData.newPlainText("service_details", onCopyDetails(service)))
                    )

                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(
                horizontal = 4.dp,
                vertical = 6.dp
            )
        ) {
            Text(
                text = "Copy Details",
                style = MaterialTheme.typography.bodyMedium,
                color = White
            )
        }
    }
}

@Composable
fun ServiceInfo(
    service: ServiceDetailDTO,
    onDeviceClick: (DeviceEntity) -> Unit
) {
    val cardShape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = GreyMid,
                shape = cardShape
            )
            .clip(cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        val riskColor = when (service.riskRating) {
            RiskRating.LOW -> Green
            RiskRating.MED -> Orange
            RiskRating.HIGH -> Red
            RiskRating.UNRATED -> GreyLight
        }
        val resolveStatusText = when (service.resolveStatus) {
            ResolveStatus.UNRESOLVED -> "Unresolved"
            ResolveStatus.PARTIAL -> "Partial"
            ResolveStatus.RESOLVED -> "Resolved"
        }
        val resolveStatusColor = when (service.resolveStatus) {
            ResolveStatus.RESOLVED -> Green
            ResolveStatus.PARTIAL -> Orange
            ResolveStatus.UNRESOLVED -> Red
        }

        val portColor = when (service.port) {
            21, 23, 22, 25, 80, 3389 -> Red
            53, 8080 -> Orange
            443, 631 -> Green
            else -> Orange
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
            ServiceType.PRINTER -> "Printer"
            ServiceType.SMB -> "SMB"
            ServiceType.WORKSTATION -> "Workstation"
            ServiceType.SSH -> "SSH"
            ServiceType.RDP -> "RDP"
            ServiceType.RFB -> "RFB (VNC)"
            ServiceType.HAP -> "HomeKit (HAP)"
            ServiceType.HUE -> "Philips Hue"
            ServiceType.MATTER -> "Matter"
            ServiceType.TELNET -> "Telnet"
            ServiceType.FTP -> "FTP"
            ServiceType.RTSP -> "RTSP"
            else -> "Unknown"
        }

        val riskConvert = when (service.riskRating) {
            RiskRating.LOW -> "Low risk"
            RiskRating.MED -> "Medium risk"
            RiskRating.HIGH -> "High risk"
            RiskRating.UNRATED -> "Unrated"
        }

        val highRiskFinding = service.riskFinding.count { it.severity == RiskRating.HIGH}

        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            strokeWidth = 3f,
                            color = GreyMid,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height)
                        )
                    }
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent,
                )
            ) {
                Column (
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text("${service.name}", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = GreyMid,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height))
                    }
                    .padding(vertical = 8.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        RiskBadge(
                            text = riskConvert,
                            color = riskColor,
                            big = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            modifier = Modifier.weight(1f),
                            text = if (highRiskFinding == 1) {
                                "1 critical finding"
                            } else {
                                "${highRiskFinding} critical findings"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Red,
                            textAlign = TextAlign.End
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    service.riskFinding.forEach { finding ->
                        val findingRiskColor = when (finding.severity) {
                            RiskRating.LOW -> Green
                            RiskRating.MED -> Orange
                            RiskRating.HIGH -> Red
                            RiskRating.UNRATED -> GreyLight
                        }
                        Text("${finding.title}", style = MaterialTheme.typography.labelMedium, color = findingRiskColor)

                        Text("${finding.detail}", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            InfoRow(
                title = "Status",
                data = resolveStatusText,
                dataColor = resolveStatusColor
            )

            InfoRow(
                title = "Type",
                data = typeConvert
            )

            InfoRow(
                title = "IP Address",
                data = service.ipAddress.toString()
            )

            InfoRow(
                title = "Port",
                data = service.port.toString(),
                dataColor = portColor
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(color = GreyMid,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height)
                        )
                    }
                    .padding(vertical = 8.dp)
                    .clickable {
                        service.device?.let {
                            onDeviceClick(it)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Device",
                    color = GreyLight,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(8.dp))

                service.device?.let {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = it.displayName,
                        color = Pink,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.End
                    )
                }
            }

            InfoRow(
                title = "First seen",
                data = "${service.firstSeen}",
                isDate = true
            )

            InfoRow(
                title = "Last seen",
                data = "${service.lastSeen}",
                isDate = true
            )
        }
    }
}
