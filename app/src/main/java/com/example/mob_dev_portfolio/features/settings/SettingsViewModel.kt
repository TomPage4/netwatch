package com.example.mob_dev_portfolio.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mob_dev_portfolio.data.entity.RetentionPeriod
import com.example.mob_dev_portfolio.data.entity.RiskRule
import com.example.mob_dev_portfolio.data.entity.ScanState
import com.example.mob_dev_portfolio.features.discovery.DiscoveryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val discoveryRepository: DiscoveryRepository
) : ViewModel() {

    private val _riskRules = MutableStateFlow<List<RiskRule>>(emptyList())
    val riskRules: StateFlow<List<RiskRule>> = _riskRules.asStateFlow()

    private val _selectedRiskRule = MutableStateFlow<RiskRule?>(null)
    val selectedRiskRule: StateFlow<RiskRule?> = _selectedRiskRule.asStateFlow()

    private val _retentionPeriod = MutableStateFlow<List<RetentionPeriod>>(emptyList())
    val retentionPeriod: StateFlow<List<RetentionPeriod>> = _retentionPeriod.asStateFlow()

    private val _selectedRetentionPeriod = MutableStateFlow<RetentionPeriod?>(null)
    val selectedRetentionPeriod: StateFlow<RetentionPeriod?> = _selectedRetentionPeriod.asStateFlow()

    val scanState: StateFlow<ScanState> = discoveryRepository.scanState

    init {
        viewModelScope.launch {
            val settings = settingsRepository.getSettings()
            _selectedRiskRule.value = settings.riskRule
            _riskRules.value = enumValues<RiskRule>().toList()
            _selectedRetentionPeriod.value = settings.retentionPeriod
            _retentionPeriod.value = enumValues<RetentionPeriod>().toList()
        }
    }

    fun clearAllHistory() {
        if (scanState.value == ScanState.Idle) {
            viewModelScope.launch {
                settingsRepository.clearAllHistory()
            }
        }
    }

    fun onRiskRuleSelected(rule: RiskRule) {
        _selectedRiskRule.value = rule
        viewModelScope.launch {
            settingsRepository.updateRiskRule(rule)
        }
    }

    fun onRetentionPeriodSelected(period: RetentionPeriod) {
        _selectedRetentionPeriod.value = period
        viewModelScope.launch {
            settingsRepository.updateRetentionPeriod(period)
        }
    }
}