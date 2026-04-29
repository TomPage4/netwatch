package com.example.mob_dev_portfolio.features.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.data.entity.RetentionPeriod
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.White

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DataRetentionScreen(
    settingsViewModel: SettingsViewModel
) {

    val retentionPeriod by settingsViewModel.retentionPeriod.collectAsState()
    val selectedRetentionPeriod by settingsViewModel.selectedRetentionPeriod.collectAsState()

    Scaffold { _ ->
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize()
        ) {
            PageHeader(
                heading = "Data Retention"
            )

            Spacer(modifier = Modifier.height(16.dp))

            DataRetentionMenu(
                retentionPeriod = retentionPeriod,
                selectedRetentionPeriod = selectedRetentionPeriod,
                onPeriodSelect = {
                    settingsViewModel.onRetentionPeriodSelected(it)
                }
            )
        }
    }
}

@Composable
fun DataRetentionMenu(
    retentionPeriod: List<RetentionPeriod>,
    selectedRetentionPeriod: RetentionPeriod?,
    onPeriodSelect: (RetentionPeriod) -> Unit
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
            containerColor = Color.Transparent
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            items(
                items = retentionPeriod,
                key = {
                    it
                }
            ) { period ->
                DataRetentionMenuRow(
                    period = period,
                    isSelected = period == selectedRetentionPeriod,
                    onPeriodSelect = onPeriodSelect
                )
            }
        }
    }
}

@Composable
fun DataRetentionMenuRow(
    period: RetentionPeriod,
    isSelected: Boolean,
    onPeriodSelect: (RetentionPeriod) -> Unit
) {

    val periodText = when (period) {
        RetentionPeriod.WEEK -> "Week"
        RetentionPeriod.MONTH -> "Month"
        RetentionPeriod.THREE_MONTHS -> "3 Months"
        RetentionPeriod.SIX_MONTHS -> "6 Months"
        RetentionPeriod.YEAR -> "Year"
    }

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
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onPeriodSelect(period)
                }
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${periodText}", style = MaterialTheme.typography.bodyLarge)
            RadioButton(
                selected = isSelected,
                onClick = {
                    onPeriodSelect(period)
                },
                colors = RadioButtonDefaults.colors(
                    selectedColor = White,
                    unselectedColor = GreyLight
                )
            )
        }
    }
}