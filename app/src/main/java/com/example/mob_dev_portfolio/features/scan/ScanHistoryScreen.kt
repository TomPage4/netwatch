package com.example.mob_dev_portfolio.features.scan

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.R
import com.example.mob_dev_portfolio.data.entity.ScanEntity
import com.example.mob_dev_portfolio.ui.components.EmptyList
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import java.time.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScanHistoryScreen(
    scanHistoryViewModel: ScanHistoryViewModel,
    onScanClick: (scanId: Long) -> Unit
) {
    val scans by scanHistoryViewModel.scans.collectAsState()
    val cardShape = RoundedCornerShape(12.dp)

    Scaffold { _ ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            PageHeader(
                heading = "SCAN HISTORY"
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
                    if (scans.isEmpty()) {
                        EmptyList(
                            title = "NO SCAN HISTORY",
                            image = R.drawable.no_services,
                            body = "No previous scans on this network. Scans from this network will show here."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = scans,
                                key = {
                                    it.id
                                }
                            ) { scan ->
                                ScanRow(
                                    scan = scan,
                                    onScanClick = onScanClick
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
private fun ScanRow(
    scan: ScanEntity,
    onScanClick: (scanId: Long) -> Unit
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
        Column(
            modifier = Modifier
                .clickable {
                    onScanClick(scan.id)
                }
                .fillMaxWidth()
        ) {

            Text("${scan.endedAt?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}", style = MaterialTheme.typography.labelMedium)

            Spacer(modifier = Modifier.height(2.dp))

            Text("${scan.endedAt?.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}", style = MaterialTheme.typography.bodyMedium, color = GreyLight)

        }
    }
}