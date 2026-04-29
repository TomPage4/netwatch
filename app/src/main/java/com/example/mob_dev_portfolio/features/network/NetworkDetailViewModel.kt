package com.example.mob_dev_portfolio.features.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.NetworkEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.features.device.DeviceRepository
import com.example.mob_dev_portfolio.features.service.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NetworkDetailViewModel (
    private val networkRepository: NetworkRepository,
    private val deviceRepository: DeviceRepository,
    private val serviceRepository: ServiceRepository
): ViewModel() {

    private val _network = MutableStateFlow<NetworkEntity?>(null)
    val network: StateFlow<NetworkEntity?> = _network.asStateFlow()

    private val _services = MutableStateFlow<List<ServiceEntity>>(emptyList())
    val services: StateFlow<List<ServiceEntity>> = _services.asStateFlow()

    private val _devices = MutableStateFlow<List<DeviceEntity>>(emptyList())
    val devices: StateFlow<List<DeviceEntity>> = _devices.asStateFlow()

    fun networkRedirect(networkId: Long) {
        viewModelScope.launch {
            _network.value = networkRepository.getNetworkById(networkId)
        }

        viewModelScope.launch {
            serviceRepository.observeServicesByNetworkId(networkId)
                .collectLatest { services ->
                    _services.value = services
                }
        }

        viewModelScope.launch {
            deviceRepository.observeDevicesByNetworkId(networkId)
                .collectLatest { devices ->
//                    _devices.value = devices
                    _devices.value = devices.sortedByDescending {
                        it.ipAddress == "00.00.00.00"
                    }
                }
        }
    }
}