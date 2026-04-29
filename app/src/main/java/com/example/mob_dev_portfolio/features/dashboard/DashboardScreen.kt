package com.example.mob_dev_portfolio.features.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.data.entity.NetworkEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.data.entity.ScanState
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.Pink
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.ui.components.DeviceRow
import com.example.mob_dev_portfolio.ui.theme.Green
import com.example.mob_dev_portfolio.ui.theme.GreyDark
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.Red
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.mob_dev_portfolio.data.converter.toDeviceEntity
import com.example.mob_dev_portfolio.data.dto.ScanDeviceHistoryWithDeviceDTO
import com.example.mob_dev_portfolio.data.entity.RiskRating
import com.example.mob_dev_portfolio.ui.components.EmptyList
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.theme.Orange

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel,
    onDeviceClick: (DeviceEntity, Long) -> Unit
) {
    val scanState by dashboardViewModel.scanState.collectAsState()
    val network by dashboardViewModel.network.collectAsState()
    val devices by dashboardViewModel.devices.collectAsState()
    val scanId by dashboardViewModel.scanId.collectAsState()

    val cardShape = RoundedCornerShape(12.dp)

    Scaffold { _ ->
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PageHeader(
                heading = "DASHBOARD"
            )

            Spacer(modifier = Modifier.height(12.dp))

            NetworkInfo(
                network = network,
                scanState = scanState,
                onStartStop = dashboardViewModel::onStartStopClicked,
                devices = devices
            )

            if (network != null && network!!.ssid != "<unknown ssid>" && network!!.bssid != "02:00:00:00:00:00") {

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = "Devices", style = MaterialTheme.typography.titleSmall)

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = "${devices.size} found",
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

                        if (devices.isEmpty()) {
                            EmptyList(
                                title = "NO DEVICES FOUND",
                                image = R.drawable.no_services,
                                body = "No devices or services detected. This network may block discovery, or all devices are currently silent."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = devices,
                                    key = {
                                        it.history.id
                                    }
                                ) { deviceWithHistory ->
                                    DeviceRow(
                                        device = deviceWithHistory.toDeviceEntity(),
                                        deviceRedirect = { deviceEntity ->
                                            scanId?.let { onDeviceClick(deviceEntity, it) }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NetworkInfo(
    network: NetworkEntity?,
    scanState: ScanState,
    onStartStop: () -> Unit,
    devices: List<ScanDeviceHistoryWithDeviceDTO>
) {
    val cardShape = RoundedCornerShape(12.dp)
    val isScanning = scanState == ScanState.Scanning
    val isStopping = scanState == ScanState.Stopping

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Pink,
                shape = cardShape
            )
            .clip(cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = Pink.copy(alpha = 0.02f),
        ),
    ) {
        Column {
            if (network != null) {
                if (network.ssid == "<unknown ssid>" && network.bssid == "02:00:00:00:00:00") {
                        Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                vertical = 26.dp,
                                horizontal = 20.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.security_block),
                            contentDescription = "Security Block",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "NETWORK SECURITY PREVENTING SCAN",
                            style = MaterialTheme.typography.labelSmall,
                            color = GreyLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Connect to a different Wi-Fi network to start scanning for services and devices",
                            style = MaterialTheme.typography.bodySmall,
                            color = GreyMid,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                modifier = Modifier
                                    .border(
                                        width = 2.dp,
                                        color = GreyMid,
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                onClick = {},
                                enabled = false,
                                colors = ButtonDefaults.buttonColors(
                                    disabledContainerColor = Color.Transparent,
                                    disabledContentColor = GreyMid
                                ),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "START SCAN",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                } else {

                    fun securityLevel(type: Int): Int = when (type) {
                        0, 1 -> 0
                        2, 3, -1 -> 1
                        4, 5, 6, 7, 8, 9, 10, 11, 12, 13 -> 2
                        else -> 1
                    }

                    val securityColor = when (network.securityType?.let { securityLevel(it) }) {
                        0 -> Red
                        1 -> Orange
                        else -> Green
                    }

                    val highRiskDeviceCount = devices.count { it.device?.riskRating == RiskRating.HIGH }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            )
                        ) {
                            Text(
                                text = "CONNECTED TO",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GreyLight
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${network.ssid}",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Column(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            ),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "SECURITY",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GreyLight
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = when (network.securityType) {
                                    -1 -> "UNKNOWN"
                                    0 -> "OPEN"
                                    1 -> "WEP"
                                    2 -> "WPA2"
                                    3 -> "EAP"
                                    4 -> "WPA3"
                                    5 -> "WPA3-Ent 192-bit"
                                    6 -> "OWE"
                                    7 -> "WAPI-PSK"
                                    8 -> "WAPI-Cert"
                                    9 -> "WPA3-Ent"
                                    10 -> "OSEN"
                                    11 -> "Passpoint R1/R2"
                                    12 -> "Passpoint R3"
                                    13 -> "DPP"
                                    else -> "UNKNOWN"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = securityColor
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            )
                            .height(70.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(cardShape),
                            shape = cardShape
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 4.dp
                                    )
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "${highRiskDeviceCount}", style = MaterialTheme.typography.labelLarge, color = Red)
                                Text(text = "HIGH RISK", style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(cardShape),
                            shape = cardShape
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 4.dp
                                    )
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "${devices.size}", style = MaterialTheme.typography.labelLarge)
                                Text(text = "DEVICES", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (scanState == ScanState.Scanning || scanState == ScanState.Stopping) {
                        LinearProgressIndicator(modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .padding(
                                horizontal = 12.dp
                            ),
                            trackColor = GreyMid
                        )
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            ),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp,
                                    color = GreyMid,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            onClick = onStartStop,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreyDark)
                        ) {
                            Text(
                                text = when {
                                    isStopping -> "STOPPING..."
                                    isScanning -> "STOP SCAN"
                                    else -> "START SCAN"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            vertical = 26.dp,
                            horizontal = 20.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.no_network),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "NOT CONNECTED",
                        style = MaterialTheme.typography.labelMedium,
                        color = GreyLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Connect to a Wi-Fi network to start scanning for services and devices.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GreyMid,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = GreyMid,
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            onClick = {},
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = GreyMid
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "START SCAN",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}