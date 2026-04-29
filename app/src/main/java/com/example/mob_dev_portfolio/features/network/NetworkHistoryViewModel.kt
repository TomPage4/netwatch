package com.example.mob_dev_portfolio.features.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.dto.NetworkWithDeviceCountDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NetworkHistoryViewModel(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _networks = MutableStateFlow<List<NetworkWithDeviceCountDTO>>(emptyList())
    val networks: StateFlow<List<NetworkWithDeviceCountDTO>> = _networks.asStateFlow()

    init {
        viewModelScope.launch {
            networkRepository.observeNetworks()
                .collectLatest { networks ->
                    _networks.value = networks
                }
        }
    }
}