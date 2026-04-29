package com.example.mob_dev_portfolio.features.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.constants.ScanType
import com.example.mob_dev_portfolio.data.entity.ScanTypeEntity
import com.example.mob_dev_portfolio.data.entity.ServiceTypeEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServiceTypesViewModel(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _serviceTypes = MutableStateFlow<List<ServiceTypeEntity>>(emptyList())
    val serviceTypes: StateFlow<List<ServiceTypeEntity>> = _serviceTypes.asStateFlow()

    private val _serviceDiscoveryEnabled = MutableStateFlow(true)
    val serviceDiscoveryEnabled: StateFlow<Boolean> = _serviceDiscoveryEnabled

    private val _scanTypes = MutableStateFlow<List<ScanTypeEntity>>(emptyList())
    val scanTypes: StateFlow<List<ScanTypeEntity>> = _scanTypes.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.observeServiceTypes().collect { types ->
                _serviceTypes.value = types
            }
        }

        viewModelScope.launch {
            settingsRepository.observeScanTypes().collect { types ->
                _scanTypes.value = types
            }
        }
    }

    fun onTypeSelect(serviceType: ServiceTypeEntity) {
        viewModelScope.launch {
            settingsRepository.typeSelect(serviceType)
            _serviceTypes.update { currentList ->
                currentList.map {
                    if (it.serviceType == serviceType.serviceType) {
                        it.copy(selected = true)
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun onTypeRemove(serviceType: ServiceTypeEntity) {
        viewModelScope.launch {
            settingsRepository.typeRemove(serviceType)
            _serviceTypes.update { currentList ->
                currentList.map {
                    if (it.serviceType == serviceType.serviceType) {
                        it.copy(selected = false)
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun onScanTypeSelect(scanType: ScanTypeEntity) {
        viewModelScope.launch {
            settingsRepository.scanTypeSelect(scanType)
            if (scanType.scanType == ScanType.SERVICE_DISCOVERY) {
                _serviceDiscoveryEnabled.update { true }
            }
            _scanTypes.update { currentList ->
                currentList.map {
                    if (it.scanType == scanType.scanType) {
                        it.copy(selected = true)
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun onScanTypeRemove(scanType: ScanTypeEntity) {
        viewModelScope.launch {
            settingsRepository.scanTypeRemove(scanType)
            if (scanType.scanType == ScanType.SERVICE_DISCOVERY) {
                _serviceDiscoveryEnabled.update { false }
            }
            _scanTypes.update { currentList ->
                currentList.map {
                    if (it.scanType == scanType.scanType) {
                        it.copy(selected = false)
                    } else {
                        it
                    }
                }
            }
        }
    }
}