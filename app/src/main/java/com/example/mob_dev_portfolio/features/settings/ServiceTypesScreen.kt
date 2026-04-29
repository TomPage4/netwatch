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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.data.constants.ScanType
import com.example.mob_dev_portfolio.data.constants.ServiceType
import com.example.mob_dev_portfolio.data.entity.ScanTypeEntity
import com.example.mob_dev_portfolio.data.entity.ServiceTypeEntity
import com.example.mob_dev_portfolio.ui.components.PageHeader
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.White

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ServiceTypesScreen(
    serviceTypesViewModel: ServiceTypesViewModel,
    onTypeSelect: (ServiceTypeEntity) -> Unit,
    onTypeRemove: (ServiceTypeEntity) -> Unit,
    onScanTypeSelect: (ScanTypeEntity) -> Unit,
    onScanTypeRemove: (ScanTypeEntity) -> Unit
) {

    val serviceTypes by serviceTypesViewModel.serviceTypes.collectAsState()
    val serviceDiscoveryEnabled by serviceTypesViewModel.serviceDiscoveryEnabled.collectAsState()
    val scanTypes by serviceTypesViewModel.scanTypes.collectAsState()

    Scaffold { _ ->
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            PageHeader(
                heading = "Discovery Methods"
            )

            Spacer(modifier = Modifier.height(16.dp))

            ServiceTypesMenu(
                scanTypes = scanTypes,
                serviceTypes = serviceTypes,
                serviceDiscoveryEnabled = serviceDiscoveryEnabled,
                onScanTypeSelect = onScanTypeSelect,
                onScanTypeRemove = onScanTypeRemove,
                onTypeSelect = onTypeSelect,
                onTypeRemove = onTypeRemove
            )
        }
    }
}

@Composable
fun ServiceTypesMenu(
    scanTypes: List<ScanTypeEntity>,
    serviceTypes: List<ServiceTypeEntity>,
    serviceDiscoveryEnabled: Boolean,
    onScanTypeSelect: (ScanTypeEntity) -> Unit,
    onScanTypeRemove: (ScanTypeEntity) -> Unit,
    onTypeSelect: (ServiceTypeEntity) -> Unit,
    onTypeRemove: (ServiceTypeEntity) -> Unit
) {
    val cardShape = RoundedCornerShape(12.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GreyMid, cardShape)
            .clip(cardShape),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(scanTypes) { scanType ->
                ParentRow(
                    scanType = scanType,
                    isSelected = scanType.selected,
                    onScanTypeSelect = onScanTypeSelect,
                    onScanTypeRemove = onScanTypeRemove,
                    showDivider = scanType.scanType != ScanType.SERVICE_DISCOVERY
                )
            }

            itemsIndexed(serviceTypes) { index, serviceType ->
                ServiceTypesMenuRow(
                    serviceType = serviceType,
                    enabled = serviceDiscoveryEnabled,
                    isLast = index == serviceTypes.lastIndex,
                    onTypeSelect = onTypeSelect,
                    onTypeRemove = onTypeRemove
                )
            }
        }
    }
}

@Composable
fun ParentRow(
    scanType: ScanTypeEntity,
    isSelected: Boolean,
    onScanTypeSelect: (ScanTypeEntity) -> Unit,
    onScanTypeRemove: (ScanTypeEntity) -> Unit,
    showDivider: Boolean = true
) {

    val title = when (scanType.scanType) {
        ScanType.HOST_DISCOVERY -> "Host Discovery"
        ScanType.SERVICE_DISCOVERY -> "Service Discovery"
        else -> "Unknown"
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (isSelected) {
                        onScanTypeRemove(scanType)
                    } else {
                        onScanTypeSelect(scanType)
                    }
                }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge
            )
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = White,
                    unselectedColor = GreyLight
                )
            )
        }
        if (showDivider) {
            HorizontalDivider(color = GreyMid, thickness = 1.dp)
        }
    }
}

@Composable
fun ServiceTypesMenuRow(
    serviceType: ServiceTypeEntity,
    enabled: Boolean,
    isLast: Boolean,
    onTypeSelect: (ServiceTypeEntity) -> Unit,
    onTypeRemove: (ServiceTypeEntity) -> Unit
) {
    val isSelected = serviceType.selected
    val alpha = if (enabled) 1f else 0.4f

    val typeConvert = when (serviceType.serviceType) {
        ServiceType.HTTP -> "HTTP"
        ServiceType.HTTPS -> "HTTPS"
        ServiceType.DNS_SD -> "DNS SD"
        ServiceType.GOOGLE_CAST -> "Google Cast"
        ServiceType.AIRPLAY -> "Airplay"
        ServiceType.SPOTIFY_CONNECT -> "Spotify Connect"
        ServiceType.RAOP -> "RAOP"
        ServiceType.IPP -> "IPP"
        ServiceType.PRINTER -> "Printer"
        ServiceType.SMB -> "SMB"
        ServiceType.WORKSTATION -> "Workstation"
        ServiceType.SSH -> "SSH"
        ServiceType.RDP -> "RDP"
        ServiceType.RFB -> "RFB (VNC)"
        ServiceType.HAP -> "HomeKit (HAP)"
        ServiceType.HUE -> "Philips Hue"
        ServiceType.MATTER -> "Matter"
        ServiceType.TELNET -> "Telnet"
        ServiceType.FTP -> "FTP"
        ServiceType.RTSP -> "RTSP"
        else -> "Unknown"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .drawBehind {
                drawLine(
                    color = GreyMid,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 2.dp.toPx()
                )
                if (!isLast) {
                    drawLine(
                        color = GreyMid,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
            .clickable(enabled = enabled) {
                if (isSelected) {
                    onTypeRemove(serviceType)
                } else {
                    onTypeSelect(serviceType)
                }
            }
            .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = typeConvert,
            style = MaterialTheme.typography.bodyMedium,
            color = if (enabled) Color.Unspecified else GreyLight,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        RadioButton(
            selected = isSelected,
            onClick = null,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(
                selectedColor = White,
                unselectedColor = GreyLight,
                disabledSelectedColor = GreyLight,
                disabledUnselectedColor = GreyLight
            )
        )
    }
}