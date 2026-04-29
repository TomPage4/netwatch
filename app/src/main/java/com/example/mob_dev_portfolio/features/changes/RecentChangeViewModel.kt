package com.example.mob_dev_portfolio.features.changes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.dto.ScanDeviceHistoryWithDeviceDTO
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.NetworkEntity
import com.example.mob_dev_portfolio.data.entity.ScanDeviceHistoryEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.data.entity.TimeFilter
import com.example.mob_dev_portfolio.features.device.DeviceRepository
import com.example.mob_dev_portfolio.features.service.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class RecentChangeViewModel(
    private val deviceRepository: DeviceRepository
) : ViewModel() {

    private val _network = MutableStateFlow<NetworkEntity?>(null)
    val network: StateFlow<NetworkEntity?> = _network

    private val _timeFilter = MutableStateFlow(TimeFilter.LAST_SCAN)
    val timeFilter: StateFlow<TimeFilter> = _timeFilter

    val changedDevices = combine(_network.filterNotNull(), _timeFilter) { network, filter ->
        deviceRepository.observeChangedByNetworkId(network.id, filter == TimeFilter.LAST_SCAN)
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val newDevices = combine(_network.filterNotNull(), _timeFilter) { network, filter ->
        deviceRepository.observeNewByNetworkId(network.id, filter == TimeFilter.LAST_SCAN)
    }.flatMapLatest { it }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setFilter(filter: TimeFilter) {
        _timeFilter.value = filter
        Log.d("Debug", "Changed: ${changedDevices.value.size}, New: ${newDevices.value.size}")
    }

    fun changeRedirect(network: NetworkEntity) {
        _network.value = network
    }
}