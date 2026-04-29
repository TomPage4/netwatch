package com.example.mob_dev_portfolio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDAO {

    @Query("""
    SELECT *
    FROM devices
    WHERE network_id = :networkId
    ORDER BY last_seen DESC
    """)
    fun observeDevicesWithServiceCountByNetworkId(networkId: Long): Flow<List<DeviceEntity>>

    @Query("""
    SELECT * FROM devices 
    WHERE network_id = :networkId 
    AND ip_address = :ipAddress
    LIMIT 1
    """)
    suspend fun findByNetworkIdAndIpAddress(
        networkId: Long,
        ipAddress: String
    ): DeviceEntity?

    @Query("SELECT * FROM devices WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): DeviceEntity?

    @Insert
    suspend fun insert(entity: DeviceEntity): Long

    @Update
    suspend fun update(entity: DeviceEntity)
}