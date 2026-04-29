package com.example.mob_dev_portfolio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDAO {

    @Query("SELECT * FROM services WHERE id = :id")
    fun observeById(id: Long): Flow<ServiceEntity?>

    @Query("""
        SELECT * FROM services
        WHERE network_id = :networkId
        ORDER BY last_seen DESC
    """)
    fun observeServicesByNetworkId(networkId: Long): Flow<List<ServiceEntity>>

    @Query("""
        SELECT * FROM services
        WHERE device_id = :deviceId
        ORDER BY last_seen DESC
    """)
    fun observeServicesByDeviceId(deviceId: Long): Flow<List<ServiceEntity>>

    @Query("""
        SELECT * FROM services
        WHERE network_id = :networkId AND name = :name AND type = :type
        LIMIT 1
    """)
    suspend fun findByNaturalKey(networkId: Long, name: String, type: String): ServiceEntity?

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun findById(id: Long): ServiceEntity?

    @Query("SELECT * FROM services WHERE network_id = :networkId")
    suspend fun findByNetworkId(networkId: Long): List<ServiceEntity>

    @Query("SELECT * FROM services")
    suspend fun getAll(): List<ServiceEntity?>

    @Query("""
        SELECT * FROM services
        WHERE device_id = :deviceId
        AND port = :port
        LIMIT 1
    """)
    fun findByDeviceIdAndPort(deviceId: Long, port: Int): ServiceEntity?

    @Query("""
        SELECT COUNT(*)
        FROM services
        WHERE device_id = :deviceId
    """)
    suspend fun countByDeviceId(deviceId: Long): Int

    @Insert
    suspend fun insert(service: ServiceEntity): Long

    @Update
    suspend fun update(service: ServiceEntity)
}