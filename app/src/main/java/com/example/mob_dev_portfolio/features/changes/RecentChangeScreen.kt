package com.example.mob_dev_portfolio.features.changes

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.data.entity.TimeFilter
import com.example.mob_dev_portfolio.ui.components.EmptyList
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.White
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.ui.components.DeviceRow

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RecentChangeScreen(
    recentChangeViewModel: RecentChangeViewModel,
    onDeviceClick: (DeviceEntity) -> Unit
) {

    val network by recentChangeViewModel.network.collectAsState()
    val newDevices by recentChangeViewModel.newDevices.collectAsState()
    val changedDevices by recentChangeViewModel.changedDevices.collectAsState()
    val timeFilter by recentChangeViewModel.timeFilter.collectAsState()
    val cardShape = RoundedCornerShape(12.dp)

    Scaffold { _ ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PageHeader(
                heading = "RECENT CHANGES"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                buildAnnotatedString {
                    append("Network: ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                        append("${network?.ssid}")
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            TimeFilterRow(
                selected = timeFilter,
                onFilterSelected = {
                    recentChangeViewModel.setFilter(it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "New devices", style = MaterialTheme.typography.titleSmall)

                Spacer(Modifier.weight(1f))

                Text(
                    text = "${newDevices.size} found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GreyLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .border(
                        width = 1.dp,
                        color = GreyMid,
                        shape = cardShape
                    )
                    .clip(cardShape),
                shape = cardShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            vertical = 8.dp,
                            horizontal = 12.dp
                        )
                ) {

                    if (newDevices.isEmpty()) {
                        EmptyList(
                            title = "NO NEW DEVICES",
                            image = R.drawable.no_services,
                            body = "No recently new devices on this network. Recently new devices will show here"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = newDevices,
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

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Changed devices", style = MaterialTheme.typography.titleSmall)

                Spacer(Modifier.weight(1f))

                Text(
                    text = "${changedDevices.size} found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GreyLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .border(
                        width = 1.dp,
                        color = GreyMid,
                        shape = cardShape
                    )
                    .clip(cardShape),
                shape = cardShape,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            vertical = 8.dp,
                            horizontal = 12.dp
                        )
                ) {

                    if (changedDevices.isEmpty()) {
                        EmptyList(
                            title = "NO CHANGED DEVICES",
                            image = R.drawable.no_services,
                            body = "No recently changed devices on this network. Recently changed devices will show here."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = changedDevices,
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
fun TimeFilterRow(
    selected: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit
) {
    val filters = listOf(
        TimeFilter.LAST_SCAN to "Last Scan",
        TimeFilter.LAST_24HRS to "24 Hours",
        TimeFilter.LAST_7DAYS to "7 Days"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                GreyLight,
                RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
    ) {
        filters.forEachIndexed { index, (filter, label) ->
            val isSelected = filter == selected

            Box(
                modifier = Modifier
                    .weight(1f)
                    .drawBehind {
                        if (index != 0) {
                            drawLine(
                                color = GreyLight,
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        White
                    } else {
                        White.copy(0.5f)
                    }
                )
            }
        }
    }
}