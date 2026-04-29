package com.example.mob_dev_portfolio.features.settings

import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.entity.AppSettingEntity
import com.example.mob_dev_portfolio.data.entity.RetentionPeriod
import com.example.mob_dev_portfolio.data.entity.RiskRule
import com.example.mob_dev_portfolio.data.entity.ScanTypeEntity
import com.example.mob_dev_portfolio.data.entity.ServiceTypeEntity
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val db: AppDatabase
) {

    suspend fun clearAllHistory() {
        db.settingsDao().clearAllHistory()
    }

    suspend fun typeSelect(serviceType: ServiceTypeEntity) {
        db.settingsDao().typeSelect(serviceType.serviceType)
    }

    suspend fun typeRemove(serviceType: ServiceTypeEntity) {
        db.settingsDao().typeRemove(serviceType.serviceType)
    }

    fun observeServiceTypes(): Flow<List<ServiceTypeEntity>> {
        return db.settingsDao().observeServiceTypes()
    }

    fun observeSelectedServiceTypes(): Flow<List<ServiceTypeEntity>> {
        return db.settingsDao().observeSelectedServiceTypes()
    }

    suspend fun scanTypeSelect(scanType: ScanTypeEntity) {
        db.settingsDao().scanTypeSelect(scanType.scanType)
    }

    suspend fun scanTypeRemove(scanType: ScanTypeEntity) {
        db.settingsDao().scanTypeRemove(scanType.scanType)
    }

    fun observeScanTypes(): Flow<List<ScanTypeEntity>> {
        return db.settingsDao().observeScanTypes()
    }

    suspend fun getSettings(): AppSettingEntity {
        return db.settingsDao().getSettings()
    }

    suspend fun updateRiskRule(rule: RiskRule) {
        db.settingsDao().updateRiskRule(rule)
    }

    suspend fun getRiskRule(): RiskRule {
        return db.settingsDao().getRiskRule()
    }

    suspend fun updateRetentionPeriod(period: RetentionPeriod) {
        db.settingsDao().updateRetentionPeriod(period)
    }
}