package com.example.mob_dev_portfolio.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.mob_dev_portfolio.data.entity.AppSettingEntity
import com.example.mob_dev_portfolio.data.entity.RetentionPeriod
import com.example.mob_dev_portfolio.data.entity.RiskRule
import com.example.mob_dev_portfolio.data.entity.ScanTypeEntity
import com.example.mob_dev_portfolio.data.entity.ServiceTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDAO {

    @Query("DELETE FROM device_events")
    suspend fun clearDeviceEvents()

    @Query("DELETE FROM devices")
    suspend fun clearDevices()

    @Query("DELETE FROM networks")
    suspend fun clearNetworks()

    @Query("DELETE FROM scan_service_history")
    suspend fun clearScanServiceHistory()

    @Query("DELETE FROM scan_device_history")
    suspend fun clearScanDeviceHistory()

    @Query("DELETE FROM scans")
    suspend fun clearScans()

    @Query("DELETE FROM service_events")
    suspend fun clearServiceEvents()

    @Query("DELETE FROM services")
    suspend fun clearServices()

    @Transaction
    suspend fun clearAllHistory() {
        clearDeviceEvents()
        clearDevices()
        clearNetworks()
        clearScanServiceHistory()
        clearScanDeviceHistory()
        clearScans()
        clearServiceEvents()
        clearServices()
    }

    @Query("""
        UPDATE service_types
        SET selected = true
        WHERE service_type = :serviceType
    """)
    suspend fun typeSelect(serviceType: String)

    @Query("""
        UPDATE service_types
        SET selected = false
        WHERE service_type = :serviceType
    """)
    suspend fun typeRemove(serviceType: String)

    @Query("SELECT * FROM service_types")
    fun observeServiceTypes(): Flow<List<ServiceTypeEntity>>

    @Query("""
        UPDATE scan_types
        SET selected = true
        WHERE scan_type = :scanType
    """)
    suspend fun scanTypeSelect(scanType: String)

    @Query("""
        UPDATE scan_types
        SET selected = false
        WHERE scan_type = :scanType
    """)
    suspend fun scanTypeRemove(scanType: String)

    @Query("SELECT * FROM scan_types")
    fun observeScanTypes(): Flow<List<ScanTypeEntity>>

    @Query("SELECT * FROM service_types WHERE selected = true")
    fun observeSelectedServiceTypes(): Flow<List<ServiceTypeEntity>>

    @Query("SELECT * FROM app_settings")
    suspend fun getSettings(): AppSettingEntity

    @Query("UPDATE app_settings SET risk_rule = :rule")
    suspend fun updateRiskRule(rule: RiskRule)

    @Query("SELECT risk_rule FROM app_settings")
    suspend fun getRiskRule(): RiskRule

    @Query("UPDATE app_settings SET retention_period = :period")
    suspend fun updateRetentionPeriod(period: RetentionPeriod)
}