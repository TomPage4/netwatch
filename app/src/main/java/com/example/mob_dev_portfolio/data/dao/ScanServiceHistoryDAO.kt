package com.example.mob_dev_portfolio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mob_dev_portfolio.data.dto.ScanServiceWithTrustedDTO
import com.example.mob_dev_portfolio.data.entity.ScanServiceHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanServiceHistoryDAO {

    @Query("""
        SELECT * FROM scan_service_history
        WHERE service_id = :serviceId
        ORDER BY last_seen
        DESC LIMIT 1
    """)
    suspend fun getRecentServiceByServiceId(serviceId: Long): ScanServiceHistoryEntity

    @Query("""
        SELECT * FROM scan_service_history
        WHERE scan_id = :scanId
        ORDER BY last_seen DESC
    """)
    fun observeServicesByScanId(scanId: Long): Flow<List<ScanServiceHistoryEntity>>

    @Query("""
        SELECT * FROM scan_service_history
        WHERE device_id = :deviceId
        AND scan_id = :scanId
        ORDER BY last_seen DESC
    """)
    fun observeServicesByDeviceIdAndScanId(deviceId: Long, scanId: Long): Flow<List<ScanServiceHistoryEntity>>

    @Query("SELECT COUNT(*) FROM scan_service_history WHERE device_id = :deviceId AND scan_id = :scanId")
    suspend fun countDistinctByDeviceAndScan(deviceId: Long, scanId: Long): Int

    @Query("""
        SELECT * FROM scan_service_history
        WHERE device_id = :deviceId
        AND scan_id = :scanId
    """)
    fun findByDeviceIdAndScanId(deviceId: Long, scanId: Long): List<ScanServiceHistoryEntity>?

    @Query("""
        SELECT * FROM scan_service_history
        WHERE service_id = :serviceId
        AND scan_id = :scanId
        LIMIT 1
    """)
    fun findByServiceIdAndScanId(serviceId: Long, scanId: Long): ScanServiceHistoryEntity?

    @Insert
    suspend fun insert(service: ScanServiceHistoryEntity): Long

    @Update
    suspend fun update(service: ScanServiceHistoryEntity)
}