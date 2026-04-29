package com.example.mob_dev_portfolio.features.device

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.DeviceEventEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeviceLogViewModel(
    private val deviceEventRepository: DeviceEventRepository
): ViewModel() {

    private val _logs = MutableStateFlow<List<DeviceEventEntity>>(emptyList())
    val logs: StateFlow<List<DeviceEventEntity>> = _logs.asStateFlow()

    fun deviceLogRedirect(device: DeviceEntity) {

        viewModelScope.launch {
            deviceEventRepository.observeDeviceLogsByDeviceId(device.id)
                .collectLatest {
                    _logs.value = it
                }
        }
    }
}