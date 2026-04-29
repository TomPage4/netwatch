package com.example.mob_dev_portfolio.features.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.data.entity.ScanState
import com.example.mob_dev_portfolio.features.dashboard.DashboardViewModel
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.theme.GreyDark
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.Pink
import com.example.mob_dev_portfolio.ui.theme.Red
import com.example.mob_dev_portfolio.ui.theme.White

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    dashboardViewModel: DashboardViewModel,
    onScannedServiceTypes: () -> Unit,
    onRiskRatingRules: () -> Unit,
    onDataRetention: () -> Unit
) {
    Scaffold { _ ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            PageHeader(
                heading = "SETTINGS"
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsMenu(
                clearAllHistory = {
                    settingsViewModel.clearAllHistory()
                },
                refreshNetwork = {
                    dashboardViewModel.refreshNetwork()
                },
                onScannedServiceTypes = onScannedServiceTypes,
                onRiskRatingRules = onRiskRatingRules,
                onDataRetention = onDataRetention
            )
        }
    }
}

@Composable
fun SettingsMenu(
    clearAllHistory: () -> Unit,
    refreshNetwork: () -> Unit,
    onScannedServiceTypes: () -> Unit,
    onRiskRatingRules: () -> Unit,
    onDataRetention: () -> Unit
) {
    val cardShape = RoundedCornerShape(12.dp)
    var showClearHistoryDialog by remember {
        mutableStateOf(false)
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = GreyMid,
                    shape = RoundedCornerShape(12.dp)
                ),
            containerColor = GreyDark,
            shape = RoundedCornerShape(12.dp),
            onDismissRequest = {
                showClearHistoryDialog = false
            },
            title = {
                Text(text = "Clear All History", style = MaterialTheme.typography.labelLarge)
            },
            text = {
                Text(text = "Are you sure you want to clear all history? This action cannot be undone.", color = GreyLight, style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                Button(
                    onClick = {
                        clearAllHistory()
                        showClearHistoryDialog = false
                        refreshNetwork()
                    },
                    colors = ButtonDefaults.buttonColors(Red),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text("Clear", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Cancel", color = White, style = MaterialTheme.typography.labelLarge)
                }
            }
        )
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
        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            SettingsMenuRow(
                icon = Icons.Default.ChevronRight,
                onClick = {
                    onScannedServiceTypes()
                }
            ) {
                Text("Discovery Methods", style = MaterialTheme.typography.bodyLarge)
            }

            SettingsMenuRow(
                icon = Icons.Default.ChevronRight,
                onClick = {
                    onRiskRatingRules()
                }
            ) {
                Text("Risk Rating Rules", style = MaterialTheme.typography.bodyLarge)
            }

            SettingsMenuRow(
                icon = Icons.Default.ChevronRight,
                onClick = {
                    onDataRetention()
                }
            ) {
                Text("Data Retention", style = MaterialTheme.typography.bodyLarge)
            }

            SettingsMenuRow(
                icon = Icons.Default.Delete,
                onClick = {
                    showClearHistoryDialog = true
                }
            ) {
                Text("Clear All History", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun SettingsMenuRow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = GreyMid,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height)
                )
            }
            .padding(vertical = 6.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}