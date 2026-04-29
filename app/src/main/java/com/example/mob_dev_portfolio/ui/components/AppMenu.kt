package com.example.mob_dev_portfolio.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.mob_dev_portfolio.data.entity.ScanStatus
import com.example.mob_dev_portfolio.ui.theme.Black
import com.example.mob_dev_portfolio.ui.theme.GreyMid
import com.example.mob_dev_portfolio.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mob_dev_portfolio.ui.theme.GreyDark

@Composable
fun AppMenu(
    scope: CoroutineScope,
    status: ScanStatus,
    statusLabel: String,
    statusDotColor: Color,
    onNavigateDashboard: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateSettings: () -> Unit,
    onRefreshNetwork: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        modifier = Modifier
                            .widthIn(
                                min = 220.dp,
                                max = 300.dp
                            ),
                        drawerContainerColor = GreyDark
                    ) {
                        Row {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                                    .background(GreyMid)
                            )

                            Column {
                                SideMenuRow(
                                    text = "Dashboard",
                                    icon = Icons.Default.ChevronRight,
                                    onClick = {
                                        onNavigateDashboard()
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                )
                                SideMenuRow(
                                    text = "Network History",
                                    icon = Icons.Default.ChevronRight,
                                    onClick = {
                                        onNavigateHistory()
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                )
                                SideMenuRow(
                                    text = "Settings",
                                    icon = Icons.Default.Tune,
                                    onClick = {
                                        onNavigateSettings()
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    topBar = {
                        TopBar(
                            status = status,
                            statusLabel = statusLabel,
                            statusDotColor = statusDotColor,
                            onMenuClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            },
                            onRefreshNetwork = onRefreshNetwork
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { padding ->
                    Box(modifier = Modifier
                        .padding(padding)
                    ) {
                        content(padding)
                    }
                }
            }
        }
    }
}

@Composable
private fun SideMenuRow(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                )
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.weight(1f))
            Icon(icon, contentDescription = null, tint = White)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBar(
    status: ScanStatus,
    statusLabel: String,
    statusDotColor: Color,
    onMenuClick: () -> Unit,
    onRefreshNetwork: () -> Unit
) {
    val statusCardShape = RoundedCornerShape(99.dp)

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = GreyDark,
            titleContentColor = White,
            actionIconContentColor = White
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "NETWATCH",
                    style = MaterialTheme.typography.headlineLarge,
                    color = White
                )

                Spacer(Modifier.width(16.dp))

                Card(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = White,
                            shape = statusCardShape
                        )
                        .clip(statusCardShape),
                    colors = CardDefaults.cardColors(containerColor = Black),
                    shape = statusCardShape
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(statusDotColor)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = White
                        )
                    }
                }
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh network",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            onRefreshNetwork()
                        }
                )

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    modifier = Modifier
                        .size(38.dp)
                        .clickable {
                            onMenuClick()
                        }
                        .padding(horizontal = 6.dp)
                )
            }
        }
    )
}