package com.example.mob_dev_portfolio

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.entity.ScanStatus
import com.example.mob_dev_portfolio.features.dashboard.DashboardScreen
import com.example.mob_dev_portfolio.features.dashboard.DashboardViewModel
import com.example.mob_dev_portfolio.ui.components.AppMenu
import com.example.mob_dev_portfolio.ui.theme.GreyLight
import com.example.mob_dev_portfolio.ui.theme.MobdevportfolioTheme
import com.example.mob_dev_portfolio.data.entity.ScanState
import com.example.mob_dev_portfolio.ui.theme.Green
import com.example.mob_dev_portfolio.ui.theme.Red
import androidx.lifecycle.ViewModelProvider
import com.example.mob_dev_portfolio.data.entity.AppScreen
import com.example.mob_dev_portfolio.features.network.NetworkDetailScreen
import com.example.mob_dev_portfolio.features.network.NetworkDetailViewModel
import com.example.mob_dev_portfolio.features.network.NetworkHistoryScreen
import com.example.mob_dev_portfolio.features.network.NetworkHistoryViewModel
import com.example.mob_dev_portfolio.features.service.ServiceDetailScreen
import com.example.mob_dev_portfolio.features.service.ServiceDetailViewModel
import com.example.mob_dev_portfolio.features.device.DeviceDetailViewModel
import com.example.mob_dev_portfolio.features.changes.RecentChangeScreen
import com.example.mob_dev_portfolio.features.changes.RecentChangeViewModel
import com.example.mob_dev_portfolio.features.device.DeviceDetailScreen
import com.example.mob_dev_portfolio.features.device.DeviceLogScreen
import com.example.mob_dev_portfolio.features.device.DeviceLogViewModel
import com.example.mob_dev_portfolio.features.navigation.ViewModelAndRepositoryCreation
import com.example.mob_dev_portfolio.features.navigation.NavigationViewModel
import com.example.mob_dev_portfolio.features.scan.ScanDetailScreen
import com.example.mob_dev_portfolio.features.scan.ScanDetailViewModel
import com.example.mob_dev_portfolio.features.scan.ScanHistoryScreen
import com.example.mob_dev_portfolio.features.scan.ScanHistoryViewModel
import com.example.mob_dev_portfolio.features.service.ServiceLogScreen
import com.example.mob_dev_portfolio.features.service.ServiceLogViewModel
import com.example.mob_dev_portfolio.features.settings.DataRetentionScreen
import com.example.mob_dev_portfolio.features.settings.RiskRatingRulesScreen
import com.example.mob_dev_portfolio.features.settings.ServiceTypesScreen
import com.example.mob_dev_portfolio.features.settings.ServiceTypesViewModel
import com.example.mob_dev_portfolio.features.settings.SettingsScreen
import com.example.mob_dev_portfolio.features.settings.SettingsViewModel
import com.example.mob_dev_portfolio.ui.theme.Orange

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
            .launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))

        val viewModelAndRepositoryCreation = ViewModelAndRepositoryCreation(
            appContext = applicationContext,
            db = AppDatabase.build(applicationContext)
        )

        val dashboardViewModel: DashboardViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[DashboardViewModel::class.java]
        val serviceDetailViewModel: ServiceDetailViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[ServiceDetailViewModel::class.java]
        val networkHistoryViewModel: NetworkHistoryViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[NetworkHistoryViewModel::class.java]
        val networkDetailViewModel: NetworkDetailViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[NetworkDetailViewModel::class.java]
        val deviceDetailViewModel: DeviceDetailViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[DeviceDetailViewModel::class.java]
        val settingsViewModel: SettingsViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[SettingsViewModel::class.java]
        val recentChangeViewModel: RecentChangeViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[RecentChangeViewModel::class.java]
        val serviceTypesViewModel: ServiceTypesViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[ServiceTypesViewModel::class.java]
        val scanHistoryViewModel: ScanHistoryViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[ScanHistoryViewModel::class.java]
        val scanDetailViewModel: ScanDetailViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[ScanDetailViewModel::class.java]
        val navigationViewModel: NavigationViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[NavigationViewModel::class.java]
        val serviceLogViewModel: ServiceLogViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[ServiceLogViewModel::class.java]
        val deviceLogViewModel: DeviceLogViewModel = ViewModelProvider(this, viewModelAndRepositoryCreation)[DeviceLogViewModel::class.java]

        setContent {
            val scope = rememberCoroutineScope()
            val scanState by dashboardViewModel.scanState.collectAsState()
            val currentScreen by navigationViewModel.currentScreen.collectAsState()
            val canGoBack by navigationViewModel.canGoBack.collectAsState()

            val (status, statusLabel, dotColor) = when (scanState) {
                ScanState.Idle -> Triple(ScanStatus.IDLE, "idle", GreyLight)
                ScanState.Scanning -> Triple(ScanStatus.SCANNING, "scanning", Green)
                ScanState.Stopping -> Triple(ScanStatus.STOPPING, "stopping", Orange)
                ScanState.Error -> Triple(ScanStatus.FAILED, "failed", Red)
            }

            BackHandler(enabled = canGoBack) {
                navigationViewModel.navigateBack()
            }

            MobdevportfolioTheme {
                AppMenu(
                    scope = scope,
                    status = status,
                    statusLabel = statusLabel,
                    statusDotColor = dotColor,
                    onNavigateDashboard = {
                        navigationViewModel.navigateTo(AppScreen.Dashboard, clearStack = true)
                    },
                    onNavigateHistory = {
                        navigationViewModel.navigateTo(AppScreen.NetworkHistory, clearStack = true)
                    },
                    onNavigateSettings = {
                        navigationViewModel.navigateTo(AppScreen.Settings, clearStack = true)
                    },
                    onRefreshNetwork = dashboardViewModel::refreshNetwork
                ) {
                    when (currentScreen) {
                        AppScreen.Dashboard -> DashboardScreen(
                            dashboardViewModel = dashboardViewModel,
                            onDeviceClick = { device, scanId ->
                                deviceDetailViewModel.deviceRedirect(
                                    device,
                                    isPreviousScan = true,
                                    scanId = scanId
                                )
                                navigationViewModel.navigateTo(AppScreen.DeviceDetail)
                            }
                        )
                        AppScreen.NetworkHistory -> NetworkHistoryScreen(
                            networkHistoryViewModel = networkHistoryViewModel,
                            onNetworkClick = { networkId ->
                                networkDetailViewModel.networkRedirect(networkId)
                                navigationViewModel.navigateTo(AppScreen.NetworkDetail)
                            }
                        )
                        AppScreen.RecentChange -> RecentChangeScreen(
                            recentChangeViewModel = recentChangeViewModel,
//                            onServiceClick = { serviceId ->
//                                serviceDetailViewModel.serviceRedirect(serviceId)
//                                navigationViewModel.navigateTo(AppScreen.ServiceDetail)
//                            }
                            onDeviceClick = { deviceId ->
                                deviceDetailViewModel.deviceRedirect(deviceId)
                                navigationViewModel.navigateTo(AppScreen.DeviceDetail)
                            }
                        )
                        AppScreen.ServiceDetail -> ServiceDetailScreen(
                            serviceDetailViewModel = serviceDetailViewModel,
                            onCopyDetails = { service ->
                                serviceDetailViewModel.onCopyDetailsClick(service)
                            },
                            onDeviceClick = { deviceId ->
                                deviceDetailViewModel.deviceRedirect(deviceId)
                                navigationViewModel.navigateTo(AppScreen.DeviceDetail)
                            },
                            onLogClick = { service ->
                                serviceLogViewModel.serviceLogRedirect(service)
                                navigationViewModel.navigateTo(AppScreen.ServiceLog)
                            }
                        )
                        AppScreen.NetworkDetail -> NetworkDetailScreen(
                            networkDetailViewModel = networkDetailViewModel,
                            onServiceClick = { service ->
                                serviceDetailViewModel.serviceRedirect(service)
                                navigationViewModel.navigateTo(AppScreen.ServiceDetail)
                            },
                            onDeviceClick = { deviceId ->
                                deviceDetailViewModel.deviceRedirect(deviceId)
                                navigationViewModel.navigateTo(AppScreen.DeviceDetail)
                            },
                            onChangeClick = { network ->
                                recentChangeViewModel.changeRedirect(network)
                                navigationViewModel.navigateTo(AppScreen.RecentChange)
                            },
                            onScanHistoryClick = { networkId ->
                                scanHistoryViewModel.scanHistoryRedirect(networkId)
                                navigationViewModel.navigateTo(AppScreen.ScanHistory)
                            }
                        )
                        AppScreen.DeviceDetail -> DeviceDetailScreen(
                            deviceDetailViewModel = deviceDetailViewModel,
                            onMarkTrusted = { device ->
                                deviceDetailViewModel.onMarkTrustedClick(device)
                            },
                            onRemoveTrusted = { device ->
                                deviceDetailViewModel.onRemoveTrustedClick(device)
                            },
                            onCopyDetails = { device, services ->
                                deviceDetailViewModel.onCopyDetailsClick(device, services)
                            },
                            onServiceClick = { service ->
                                serviceDetailViewModel.serviceRedirect(service)
                                navigationViewModel.navigateTo(AppScreen.ServiceDetail)
                            },
                            onLogClick = { device ->
                                deviceLogViewModel.deviceLogRedirect(device)
                                navigationViewModel.navigateTo(AppScreen.DeviceLog)
                            }
                        )
                        AppScreen.Settings -> SettingsScreen(
                            settingsViewModel = settingsViewModel,
                            dashboardViewModel = dashboardViewModel,
                            onScannedServiceTypes = {
                                navigationViewModel.navigateTo(AppScreen.ScannedServiceTypes)
                            },
                            onRiskRatingRules = {
                                navigationViewModel.navigateTo(AppScreen.RiskRatingRules)
                            },
                            onDataRetention = {
                                navigationViewModel.navigateTo(AppScreen.DataRetention)
                            }
                        )
                        AppScreen.ScannedServiceTypes -> ServiceTypesScreen(
                            serviceTypesViewModel = serviceTypesViewModel,
                            onTypeSelect = { serviceType ->
                                serviceTypesViewModel.onTypeSelect(serviceType)
                            },
                            onTypeRemove = { serviceType ->
                                serviceTypesViewModel.onTypeRemove(serviceType)
                            },
                            onScanTypeSelect = { scanType ->
                                serviceTypesViewModel.onScanTypeSelect(scanType)
                            },
                            onScanTypeRemove = { scanType ->
                                serviceTypesViewModel.onScanTypeRemove(scanType)
                            }
                        )
                        AppScreen.RiskRatingRules -> RiskRatingRulesScreen(
                            settingsViewModel = settingsViewModel
                        )
                        AppScreen.DataRetention -> DataRetentionScreen(
                            settingsViewModel = settingsViewModel
                        )
                        AppScreen.ScanHistory -> ScanHistoryScreen(
                            scanHistoryViewModel = scanHistoryViewModel,
                            onScanClick = { scanId ->
                                scanDetailViewModel.scanRedirect(scanId)
                                navigationViewModel.navigateTo(AppScreen.ScanDetail)
                            }
                        )
                        AppScreen.ScanDetail -> ScanDetailScreen(
                            scanDetailViewModel = scanDetailViewModel,
                            onServiceClick = { service ->
                                serviceDetailViewModel.serviceRedirect(
                                    service
                                )
                                navigationViewModel.navigateTo(AppScreen.ServiceDetail)
                            },
                            onDeviceClick = { device, scanId ->
                                deviceDetailViewModel.deviceRedirect(
                                    device,
                                    isPreviousScan = true,
                                    scanId = scanId
                                )
                                navigationViewModel.navigateTo(AppScreen.DeviceDetail)
                            },
                            onCopyDetails = { scan, services, devices ->
                                scanDetailViewModel.onCopyDetailsClick(scan, services, devices)
                            }
                        )
                        AppScreen.ServiceLog -> ServiceLogScreen(
                            serviceLogViewModel = serviceLogViewModel
                        )
                        AppScreen.DeviceLog -> DeviceLogScreen(
                            deviceLogViewModel = deviceLogViewModel
                        )
                    }
                }
            }
        }
    }
}