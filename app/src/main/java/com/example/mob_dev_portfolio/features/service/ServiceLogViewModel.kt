package com.example.mob_dev_portfolio.features.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.dto.ServiceDetailDTO
import com.example.mob_dev_portfolio.data.entity.ServiceEventEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ServiceLogViewModel(
    private val serviceEventRepository: ServiceEventRepository
): ViewModel() {

    private val _logs = MutableStateFlow<List<ServiceEventEntity>>(emptyList())
    val logs: StateFlow<List<ServiceEventEntity>> = _logs.asStateFlow()

    fun serviceLogRedirect(service: ServiceDetailDTO) {

        viewModelScope.launch {
            serviceEventRepository.observeServiceLogsByServiceId(service.id)
                .collectLatest {
                    _logs.value = it
                }
        }
    }
}