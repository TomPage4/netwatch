package com.example.mob_dev_portfolio.features.navigation

import com.example.mob_dev_portfolio.features.device.DeviceEventRepository
import com.example.mob_dev_portfolio.features.device.DeviceRepository
import com.example.mob_dev_portfolio.features.network.NetworkRepository
import com.example.mob_dev_portfolio.features.scan.ScanDeviceHistoryRepository
import com.example.mob_dev_portfolio.features.scan.ScanRepository
import com.example.mob_dev_portfolio.features.scan.ScanServiceHistoryRepository
import com.example.mob_dev_portfolio.features.service.ServiceEventRepository
import com.example.mob_dev_portfolio.features.service.ServiceRepository
import com.example.mob_dev_portfolio.features.settings.ServiceTypesViewModel
import com.example.mob_dev_portfolio.features.settings.SettingsRepository
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.features.changes.RecentChangeViewModel
import com.example.mob_dev_portfolio.features.dashboard.DashboardViewModel
import com.example.mob_dev_portfolio.features.device.DeviceDetailViewModel
import com.example.mob_dev_portfolio.features.device.DeviceLogViewModel
import com.example.mob_dev_portfolio.features.discovery.DiscoveryRepository
import com.example.mob_dev_portfolio.features.network.NetworkDetailViewModel
import com.example.mob_dev_portfolio.features.network.NetworkHistoryViewModel
import com.example.mob_dev_portfolio.features.risk.RiskAssessor
import com.example.mob_dev_portfolio.features.scan.ScanDetailViewModel
import com.example.mob_dev_portfolio.features.scan.ScanHistoryViewModel
import com.example.mob_dev_portfolio.features.service.ServiceDetailViewModel
import com.example.mob_dev_portfolio.features.service.ServiceLogViewModel
import com.example.mob_dev_portfolio.features.settings.SettingsViewModel

class ViewModelAndRepositoryCreation(
    private val appContext: Context,
    private val db: AppDatabase
) : ViewModelProvider.Factory {

    private val networkRepository by lazy {
        NetworkRepository(appContext = appContext, db = db)
    }
    private val networkIdProvider: suspend () -> Long = {
        networkRepository.refreshCurrentNetworkAndGet()?.id
            ?: error("Could not resolve current network")
    }
    private val deviceRepository by lazy {
        DeviceRepository(db = db)
    }
    private val serviceRepository by lazy {
        ServiceRepository(db = db)
    }
    private val settingsRepository by lazy {
        SettingsRepository(db = db)
    }
    private val serviceEventRepository by lazy {
        ServiceEventRepository(db = db) }
    private val deviceEventRepository by lazy {
        DeviceEventRepository(db = db)
    }
    private val scanRepository by lazy {
        ScanRepository(db = db)
    }
    private val scanServiceHistoryRepository by lazy {
        ScanServiceHistoryRepository(db = db)
    }
    private val scanDeviceHistoryRepository by lazy {
        ScanDeviceHistoryRepository(db = db)
    }
    private val riskAssessor by lazy {
        RiskAssessor()
    }

    private val discoveryRepository by lazy {
        DiscoveryRepository(
            appContext = appContext,
            db = db,
            networkIdProvider = networkIdProvider,
            settingsRepository = settingsRepository,
            riskAssessor = riskAssessor
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        DashboardViewModel::class.java -> DashboardViewModel(
            discoveryRepository = discoveryRepository,
            networkRepository = networkRepository,
            serviceRepository = serviceRepository,
            scanDeviceHistoryRepository = scanDeviceHistoryRepository
        )
        ServiceDetailViewModel::class.java -> ServiceDetailViewModel(
            deviceRepository = deviceRepository
        )
        NetworkHistoryViewModel::class.java -> NetworkHistoryViewModel(
            networkRepository = networkRepository
        )
        NetworkDetailViewModel::class.java -> NetworkDetailViewModel(
            networkRepository = networkRepository,
            deviceRepository = deviceRepository,
            serviceRepository = serviceRepository
        )
        DeviceDetailViewModel::class.java -> DeviceDetailViewModel(
            serviceRepository = serviceRepository,
            deviceEventRepository = deviceEventRepository,
            scanServiceHistoryRepository = scanServiceHistoryRepository,
            deviceRepository = deviceRepository
        )
        SettingsViewModel::class.java -> SettingsViewModel(
            settingsRepository = settingsRepository,
            discoveryRepository = discoveryRepository
        )
        RecentChangeViewModel::class.java -> RecentChangeViewModel(
//            serviceRepository = serviceRepository
            deviceRepository = deviceRepository
        )
        ServiceTypesViewModel::class.java -> ServiceTypesViewModel(
            settingsRepository = settingsRepository
        )
        ScanHistoryViewModel::class.java -> ScanHistoryViewModel(
            scanRepository = scanRepository
        )
        ScanDetailViewModel::class.java -> ScanDetailViewModel(
            scanRepository = scanRepository,
            scanServiceHistoryRepository = scanServiceHistoryRepository,
            scanDeviceHistoryRepository = scanDeviceHistoryRepository,
            networkRepository = networkRepository
        )
        NavigationViewModel::class.java -> NavigationViewModel()
        ServiceLogViewModel::class.java -> ServiceLogViewModel(
            serviceEventRepository = serviceEventRepository
        )
        DeviceLogViewModel::class.java -> DeviceLogViewModel(
            deviceEventRepository = deviceEventRepository
        )
        else -> throw IllegalArgumentException("Unknown ViewModel")
    } as T
}