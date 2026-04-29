package com.example.mob_dev_portfolio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mob_dev_portfolio.data.entity.DeviceEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceEventDAO {

    @Query("""
        SELECT * FROM device_events
        WHERE device_id = :deviceId
        ORDER BY timestamp DESC
    """)
    fun observeDeviceLogsByDeviceId(deviceId: Long): Flow<List<DeviceEventEntity>>

    @Insert
    suspend fun insert(event: DeviceEventEntity): Long
}