package com.example.mob_dev_portfolio.features.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.entity.ScanEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ScanHistoryViewModel(
    private val scanRepository: ScanRepository
): ViewModel() {

    private val _scans = MutableStateFlow<List<ScanEntity>>(emptyList())
    val scans: StateFlow<List<ScanEntity>> = _scans.asStateFlow()

    fun scanHistoryRedirect(networkId: Long) {
        viewModelScope.launch {
            scanRepository.observeScansByNetworkId(networkId)
                .collectLatest { scans ->
                    _scans.value = scans
                }
        }
    }
}