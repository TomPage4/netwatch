package com.example.mob_dev_portfolio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mob_dev_portfolio.data.dto.NetworkWithDeviceCountDTO
import com.example.mob_dev_portfolio.data.entity.NetworkEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface NetworkDAO {

    @Query("SELECT * FROM networks ORDER BY last_seen")
    suspend fun getAllNetworks(): List<NetworkEntity>

    @Query("""
        SELECT 
            network.id,
            network.ssid,
            network.last_seen AS lastSeen,
            COUNT(device.id) AS deviceCount
        FROM networks network
        LEFT JOIN devices device ON device.network_id = network.id
        GROUP BY network.id
        ORDER BY network.last_seen DESC
    """)
    fun observeNetworksWithDeviceCount(): Flow<List<NetworkWithDeviceCountDTO>>

    @Query("SELECT * FROM networks ORDER BY last_seen DESC LIMIT 1")
    fun observeLatest(): Flow<NetworkEntity?>

    @Query("SELECT * FROM networks WHERE ssid = :ssid LIMIT 1")
    suspend fun getNetworkBySsid(ssid: String): NetworkEntity?

    @Query("SELECT * FROM networks WHERE id = :id LIMIT 1")
    suspend fun getNetworkById(id: Long): NetworkEntity?

    @Query("UPDATE networks SET last_seen = :time WHERE id = :id")
    suspend fun updateLastSeen(id: Long, time: LocalDateTime)

    @Query("SELECT total_scans FROM networks WHERE id = :id")
    suspend fun getCountById(id: Long): Int

    @Insert
    suspend fun insert(entity: NetworkEntity): Long

    @Update
    suspend fun update(entity: NetworkEntity)
}