package com.example.mob_dev_portfolio.features.device

import android.annotation.SuppressLint
import android.content.ClipData
import androidx.compose.foundation.border
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.data.constants.ScanType
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.RiskRating
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.ui.components.EmptyList
import com.example.mob_dev_portfolio.ui.components.InfoRow
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.components.RiskBadge
import com.example.mob_dev_portfolio.ui.components.ServiceBadge
import com.example.mob_dev_portfolio.ui.components.ServiceRow
import com.example.mob_dev_portfolio.ui.theme.Blue
import com.example.mob_dev_portfolio.ui.theme.Green
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.Orange
import com.example.mob_dev_portfolio.ui.theme.Pink
import com.example.mob_dev_portfolio.ui.theme.Red
import com.example.mob_dev_portfolio.ui.theme.White
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DeviceDetailScreen(
    deviceDetailViewModel: DeviceDetailViewModel,
    onMarkTrusted: (DeviceEntity) -> Unit,
    onRemoveTrusted: (DeviceEntity) -> Unit,
    onCopyDetails: (DeviceEntity, List<ServiceEntity>) -> String,
    onServiceClick: (ServiceEntity) -> Unit,
    onLogClick: (DeviceEntity) -> Unit
) {
    val device by deviceDetailViewModel.device.collectAsState()
    val services by deviceDetailViewModel.services.collectAsState()

    val cardShape = RoundedCornerShape(12.dp)

    Scaffold { _ ->
        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PageHeader(
                heading = "DEVICE DETAILS"
            )

            Spacer(modifier = Modifier.height(8.dp))

            device?.let {
                ActionButtonRow(
                    device = it,
                    onMarkTrusted = onMarkTrusted,
                    onRemoveTrusted = onRemoveTrusted,
                    services = services,
                    onCopyDetails = onCopyDetails,
                    onLogClick = onLogClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            device?.let {
                DeviceInfo(it)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Services", style = MaterialTheme.typography.titleSmall)

                Spacer(Modifier.weight(1f))

                Text(
                    text = "${services.size} found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GreyLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
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
                Column(
                    modifier = Modifier
                        .padding(
                            vertical = 8.dp,
                            horizontal = 12.dp
                        )
                ) {

                    if (services.isEmpty()) {
                        EmptyList(
                            title = "NO SERVICES FOUND",
                            image = R.drawable.no_services,
                            body = "No services detected on this device. It may not be advertising any services, or they could not be resolved."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = services,
                                key = {
                                    it.id
                                }
                            ) { service ->
                                ServiceRow(
                                    service = service,
                                    serviceRedirect = onServiceClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtonRow(
    device: DeviceEntity,
    onMarkTrusted: (DeviceEntity) -> Unit,
    onRemoveTrusted: (DeviceEntity) -> Unit,
    services: List<ServiceEntity>,
    onCopyDetails: (DeviceEntity, List<ServiceEntity>) -> String,
    onLogClick: (DeviceEntity) -> Unit
) {
    val isTrusted = device.isTrusted
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp
            )
    ) {
        if (device.riskRating != RiskRating.UNRATED) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (!isTrusted) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = GreyMid,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        onClick = {
                            onMarkTrusted(device)
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
                            text = "Mark as Trusted",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Green
                        )
                    }
                } else {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                width = 2.dp,
                                color = GreyMid,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        onClick = {
                            onRemoveTrusted(device)
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
                            text = "Remove Trusted",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
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
                        onLogClick(device)
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
                                ClipEntry(
                                    ClipData.newPlainText(
                                        "device_details",
                                        onCopyDetails(device, services)
                                    )
                                )
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
    }
}

@Composable
fun DeviceInfo(device: DeviceEntity) {
    val cardShape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                GreyMid,
                cardShape
            )
            .clip(cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {

        val riskColor = when (device.riskRating) {
            RiskRating.LOW -> Green
            RiskRating.MED -> Orange
            RiskRating.HIGH -> Red
            RiskRating.UNRATED -> GreyLight
        }
        val riskConvert = when (device.riskRating) {
            RiskRating.LOW -> "Low risk"
            RiskRating.MED -> "Medium risk"
            RiskRating.HIGH -> "High risk"
            RiskRating.UNRATED -> "Unrated"
        }

        val highRiskFinding = device.riskFinding.count { it.severity == RiskRating.HIGH}

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = device.displayName,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
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
                        if (device.isTrusted) {
                            RiskBadge(
                                text = "Trusted",
                                color = Blue,
                                big = true
                            )
                        } else {
                            RiskBadge(
                                text = riskConvert,
                                color = riskColor,
                                big = true
                            )
                        }

                        if (device.riskRating != RiskRating.UNRATED) {

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
                    }

                    if (device.riskFinding.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        device.riskFinding.forEach { finding ->
                            val findingRiskColor = when (finding.severity) {
                                RiskRating.LOW -> Green
                                RiskRating.MED -> Orange
                                RiskRating.HIGH -> Red
                                RiskRating.UNRATED -> GreyLight
                            }
                            Text(
                                "${finding.title}",
                                style = MaterialTheme.typography.labelMedium,
                                color = findingRiskColor
                            )

                            Text("${finding.detail}", style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            InfoRow(
                title = "Observed via",
                data = when (device.scanType) {
                    ScanType.SERVICE_DISCOVERY -> "Service Discovery"
                    ScanType.HOST_DISCOVERY -> "Host Discovery"
                    else -> "Unknown"
                }
            )

            InfoRow(
                title = "IP Address",
                data = "${device.ipAddress}"
            )

            InfoRow(
                title = "First seen",
                data = "${device.firstSeen}",
                isDate = true
            )

            InfoRow(
                title = "Last seen",
                data = "${device.lastSeen}",
                isDate = true
            )
        }
    }
}