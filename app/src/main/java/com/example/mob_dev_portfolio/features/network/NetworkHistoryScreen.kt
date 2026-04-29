package com.example.mob_dev_portfolio.features.network

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.data.dto.NetworkWithDeviceCountDTO
import com.example.mob_dev_portfolio.ui.components.EmptyList
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.components.ServiceBadge
import com.example.mob_dev_portfolio.ui.theme.Green
import com.example.mob_dev_portfolio.ui.theme.GreyDark
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.Orange
import com.example.mob_dev_portfolio.ui.theme.Red

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NetworkHistoryScreen(
    networkHistoryViewModel: NetworkHistoryViewModel,
    onNetworkClick: (networkId: Long) -> Unit
) {
    val networks by networkHistoryViewModel.networks.collectAsState()
    val cardShape = RoundedCornerShape(12.dp)

    Scaffold { _ ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            PageHeader(
                heading = "NETWORK HISTORY"
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                    containerColor = GreyDark,
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            vertical = 8.dp,
                            horizontal = 12.dp
                        )
                ) {
                    if (networks.isEmpty()) {
                        EmptyList(
                            title = "NO NETWORK HISTORY",
                            image = R.drawable.no_network,
                            body = "No previously connected to networks. Previously connected to networks will show here."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = networks,
                                key = {
                                    it.id
                                }
                            ) { network ->
                                NetworkRow(
                                    network = network,
                                    networkRedirect = onNetworkClick
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
private fun NetworkRow(
    network: NetworkWithDeviceCountDTO,
    networkRedirect: (networkId: Long) -> Unit
) {
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
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

        Row(
            modifier = Modifier
                .clickable {
                    networkRedirect(network.id)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                val riskColor = when (network.deviceCount) {
                    in 0..8 -> Green
                    in 9..20 -> Orange
                    else -> Red
                }

                Text(text = network.ssid, style = MaterialTheme.typography.labelMedium)

                Spacer(modifier = Modifier.height(2.dp))

                if (network.deviceCount == 1) {
                    ServiceBadge(
                        text = "1 Device",
                        cardShape = RoundedCornerShape(4.dp),
                        color = riskColor
                    )
                } else {
                    ServiceBadge(
                        text = "${network.deviceCount} Devices",
                        cardShape = RoundedCornerShape(4.dp),
                        color = riskColor
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = GreyMid,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}