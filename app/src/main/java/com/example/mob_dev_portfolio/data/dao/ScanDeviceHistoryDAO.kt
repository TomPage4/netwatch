package com.example.mob_dev_portfolio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.mob_dev_portfolio.data.dto.ScanDeviceHistoryWithDeviceDTO
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.ScanDeviceHistoryEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ScanDeviceHistoryDAO {

    @Query("""
        SELECT * FROM scan_device_history
        WHERE device_id = :deviceId
        AND scan_id = :scanId
        LIMIT 1
    """)
    suspend fun getDeviceByIdAndScanId(deviceId: Long, scanId: Long): ScanDeviceHistoryEntity?

    @Transaction
    @Query("""
        SELECT * FROM scan_device_history
        WHERE scan_id = :scanId
        ORDER BY last_seen DESC
    """)
    fun observeDevicesByScanId(scanId: Long): Flow<List<ScanDeviceHistoryWithDeviceDTO>>

    @Query("""
    SELECT devices.*
    FROM devices
    INNER JOIN scan_device_history history ON devices.id = history.device_id
    WHERE history.network_id = :networkId
    AND history.is_changed = 1
    AND (:lastScanOnly = 0 OR history.scan_id = (
        SELECT MAX(scan_id) FROM scan_device_history WHERE network_id = :networkId
    ))
    GROUP BY devices.id
""")
    fun observeChangedByNetworkId(networkId: Long, lastScanOnly: Boolean): Flow<List<DeviceEntity>>

    @Query("""
    SELECT devices.*
    FROM devices
    INNER JOIN scan_device_history history ON devices.id = history.device_id
    WHERE history.network_id = :networkId
    AND history.is_new = 1
    AND (:lastScanOnly = 0 OR history.scan_id = (
        SELECT MAX(scan_id) FROM scan_device_history WHERE network_id = :networkId
    ))
    GROUP BY devices.id
""")
    fun observeNewByNetworkId(networkId: Long, lastScanOnly: Boolean): Flow<List<DeviceEntity>>

    @Insert
    suspend fun insert(device: ScanDeviceHistoryEntity): Long

    @Update
    suspend fun update(device: ScanDeviceHistoryEntity)
}