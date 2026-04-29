package com.example.mob_dev_portfolio.features.network

import android.annotation.SuppressLint
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.NetworkEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.ui.components.DeviceRow
import com.example.mob_dev_portfolio.ui.components.EmptyList
import com.example.mob_dev_portfolio.ui.components.InfoRow
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.components.ServiceRow
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.White

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NetworkDetailScreen(
    networkDetailViewModel: NetworkDetailViewModel,
    onServiceClick: (ServiceEntity) -> Unit,
    onDeviceClick: (DeviceEntity) -> Unit,
    onChangeClick: (NetworkEntity) -> Unit,
    onScanHistoryClick: (networkId: Long) -> Unit
) {
    val network by networkDetailViewModel.network.collectAsState()
    val services by networkDetailViewModel.services.collectAsState()
    val devices by networkDetailViewModel.devices.collectAsState()
    val cardShape = RoundedCornerShape(12.dp)

    Scaffold { _ ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PageHeader(
                heading = "NETWORK DETAILS",
            )

            Spacer(modifier = Modifier.height(8.dp))

            network?.let {
                ActionButtonRow(
                    network = it,
                    onChangeClick = onChangeClick,
                    onScanHistoryClick = onScanHistoryClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            network?.let {
                NetworkInfo(
                    network = it
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "All devices", style = MaterialTheme.typography.titleSmall)

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
                                    it.id
                                }
                            ) { device ->
                                DeviceRow(
                                    device = device,
                                    deviceRedirect = onDeviceClick
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
    network: NetworkEntity,
    onChangeClick: (NetworkEntity) -> Unit,
    onScanHistoryClick: (networkId: Long) -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
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
                onChangeClick(network)
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
                text = "Recent Changes",
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
                onScanHistoryClick(network.id)
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
                text = "Scan History",
                style = MaterialTheme.typography.bodyMedium,
                color = White
            )
        }
    }
}

@Composable
private fun NetworkInfo(
    network: NetworkEntity
) {
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
                title = "BSSID",
                data = "${network.bssid}"
            )

            InfoRow(
                title = "Total scans",
                data = "${network.totalScans}"
            )

            InfoRow(
                title = "First seen",
                data = "${network.firstSeen}",
                isDate = true
            )

            InfoRow(
                title = "Last seen",
                data = "${network.lastSeen}",
                isDate = true
            )
        }
    }
}