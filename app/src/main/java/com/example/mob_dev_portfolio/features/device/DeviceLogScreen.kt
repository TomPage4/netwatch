package com.example.mob_dev_portfolio.features.device

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.data.entity.DeviceEventEntity
import com.example.mob_dev_portfolio.ui.components.EmptyList
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DeviceLogScreen(
    deviceLogViewModel: DeviceLogViewModel
) {
    val logs by deviceLogViewModel.logs.collectAsState()

    Scaffold { _ ->
        Column (
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            PageHeader(
                heading = "DEVICE LOGS"
            )

            Spacer(modifier = Modifier.height(8.dp))

            DeviceLog(
                logs = logs
            )
        }
    }
}

@Composable
private fun DeviceLog(
    logs: List<DeviceEventEntity>
) {
    val cardShape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier
            .fillMaxSize()
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
            if (logs.isEmpty()) {
                EmptyList(
                    title = "NO LOGS FOUND",
                    image = R.drawable.no_services,
                    body = "No logs for this device to display. Try scanning again on this network."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = logs,
                        key = {
                            it.id
                        }
                    ) { log ->
                        DeviceLogRow(
                            log = log
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceLogRow(
    log: DeviceEventEntity
) {
    Card(
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
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${log.eventType}", style = MaterialTheme.typography.labelMedium)

                if (log.eventInfo != null) {
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(text = log.eventInfo, color = GreyLight, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                buildAnnotatedString {
                    append("${log.timestamp.format((DateTimeFormatter.ofPattern("dd/MM/yy")))}")
                    withStyle(style = SpanStyle(color = GreyLight)) {
                        append(" ${log.timestamp.format((DateTimeFormatter.ofPattern("HH:mm")))}")
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}