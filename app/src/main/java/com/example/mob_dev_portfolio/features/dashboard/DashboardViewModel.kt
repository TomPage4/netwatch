package com.example.mob_dev_portfolio.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.dto.ScanDeviceHistoryWithDeviceDTO
import com.example.mob_dev_portfolio.data.entity.NetworkEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.features.discovery.DiscoveryRepository
import com.example.mob_dev_portfolio.features.network.NetworkRepository
import com.example.mob_dev_portfolio.data.entity.ScanState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import com.example.mob_dev_portfolio.data.entity.ScanDeviceHistoryEntity
import com.example.mob_dev_portfolio.features.scan.ScanDeviceHistoryRepository
import com.example.mob_dev_portfolio.features.service.ServiceRepository

class DashboardViewModel(
    private val discoveryRepository: DiscoveryRepository,
    private val networkRepository: NetworkRepository,
    private val serviceRepository: ServiceRepository,
    private val scanDeviceHistoryRepository: ScanDeviceHistoryRepository
) : ViewModel() {

    private val _scanId = MutableStateFlow<Long?>(null)
    val scanId: StateFlow<Long?> = _scanId.asStateFlow()

    val scanState: StateFlow<ScanState> = discoveryRepository.scanState

    private val _network = MutableStateFlow<NetworkEntity?>(null)
    val network: StateFlow<NetworkEntity?> = _network.asStateFlow()

    private val _devices = MutableStateFlow<List<ScanDeviceHistoryWithDeviceDTO>>(emptyList())
    val devices: StateFlow<List<ScanDeviceHistoryWithDeviceDTO>> = _devices.asStateFlow()

    private var scanStartedAt: LocalDateTime? = null

    init {
        viewModelScope.launch {
            _network.value = networkRepository.refreshCurrentNetworkAndGet()
        }

        viewModelScope.launch {
            _network
                .filterNotNull()
                .flatMapLatest { network ->
                    serviceRepository.observeServicesByNetworkId(network.id)
                }
        }

        viewModelScope.launch {
            _scanId
                .filterNotNull()
                .flatMapLatest { scanId ->
                    scanDeviceHistoryRepository.observeDevicesByScanId(scanId)
                }
                .collectLatest { devices ->
                    _devices.value = devices.sortedByDescending {
                        it.history.ipAddress == "00.00.00.00"
                    }
                }
        }
    }

    fun onStartStopClicked() {
        if (scanState.value == ScanState.Scanning) {
            discoveryRepository.stopScan()
        } else {
            viewModelScope.launch {
                scanStartedAt = LocalDateTime.now()
                _devices.value = emptyList()

                val network = networkRepository.incrementScanCountAndGet()
                _network.value = network

                if (network != null) {
                    val scanId = discoveryRepository.startScan()
                    _scanId.value = scanId
                }
            }
        }
    }

    fun refreshNetwork() {
        viewModelScope.launch {
            if (scanState.value != ScanState.Scanning) {
                _devices.value = emptyList()
                scanStartedAt = null
                _network.value = networkRepository.refreshCurrentNetworkAndGet()
            }
        }
    }
}