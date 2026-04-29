package com.example.mob_dev_portfolio.features.scan

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.data.converter.toDeviceEntity
import com.example.mob_dev_portfolio.data.dto.ScanDeviceHistoryWithDeviceDTO
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.NetworkEntity
import com.example.mob_dev_portfolio.data.entity.RiskRule
import com.example.mob_dev_portfolio.data.entity.ScanEntity
import com.example.mob_dev_portfolio.data.entity.ScanServiceHistoryEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.ui.components.DeviceRow
import com.example.mob_dev_portfolio.ui.components.EmptyList
import com.example.mob_dev_portfolio.ui.components.InfoRow
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.theme.Green
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.Orange
import com.example.mob_dev_portfolio.ui.theme.Red
import com.example.mob_dev_portfolio.ui.theme.White
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScanDetailScreen(
    scanDetailViewModel: ScanDetailViewModel,
    onServiceClick: (ServiceEntity) -> Unit,
    onDeviceClick: (DeviceEntity, Long) -> Unit,
    onCopyDetails: (ScanEntity, List<ScanServiceHistoryEntity>, List<ScanDeviceHistoryWithDeviceDTO>) -> String
) {
    val scan by scanDetailViewModel.scan.collectAsState()
    val services by scanDetailViewModel.services.collectAsState()
    val devices by scanDetailViewModel.devices.collectAsState()
    val network by scanDetailViewModel.network.collectAsState()

    val cardShape = RoundedCornerShape(12.dp)

    Scaffold { _ ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PageHeader(
                heading = "SCAN DETAILS"
            )

            Spacer(modifier = Modifier.height(8.dp))

            scan?.let {
                ActionButtonRow(
                    scan = it,
                    services = services,
                    devices = devices,
                    onCopyDetails = onCopyDetails
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            scan?.let { scan ->
                network?.let { network ->
                    ScanDetailInfo(
                        scan,
                        network
                    )
                }
            }

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
                                        scan?.id?.let {
                                            onDeviceClick(deviceEntity, it)
                                        }
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

@Composable
private fun ActionButtonRow(
    scan: ScanEntity,
    services: List<ScanServiceHistoryEntity>,
    devices: List<ScanDeviceHistoryWithDeviceDTO>,
    onCopyDetails: (ScanEntity, List<ScanServiceHistoryEntity>, List<ScanDeviceHistoryWithDeviceDTO>) -> String
) {
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.dp
            )
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
                scope.launch {
                    clipboard.setClipEntry(
                        ClipEntry(ClipData.newPlainText("scan_details", onCopyDetails(scan, services, devices)))
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
private fun ScanDetailInfo(
    scan: ScanEntity,
    network: NetworkEntity
) {

    val cardShape = RoundedCornerShape(12.dp)

    val riskRule = when (scan.riskRuleAtScan) {
        RiskRule.PERMISSIVE -> "Permissive"
        RiskRule.STANDARD -> "Standard"
        RiskRule.CONSERVATIVE -> "Conservative"
    }

    val riskRuleColor = when (scan.riskRuleAtScan) {
        RiskRule.PERMISSIVE -> Red
        RiskRule.STANDARD -> Orange
        RiskRule.CONSERVATIVE -> Green
    }

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
                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text("${network.ssid}", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp
            )
        ) {

            InfoRow(
                title = "Risk rule",
                data = "${riskRule}",
                dataColor = riskRuleColor
            )

            InfoRow(
                title = "Started at",
                data = "${scan.startedAt}",
                isDate = true
            )

            InfoRow(
                title = "Ended at",
                data = "${scan.endedAt}",
                isDate = true
            )
        }
    }
}