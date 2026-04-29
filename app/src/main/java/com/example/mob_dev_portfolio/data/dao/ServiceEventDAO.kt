package com.example.mob_dev_portfolio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mob_dev_portfolio.data.entity.ServiceEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceEventDAO {

    @Query("""
        SELECT * FROM service_events
        WHERE service_id = :serviceId
        ORDER BY timestamp DESC
    """)
    fun observeServiceLogsByServiceId(serviceId: Long): Flow<List<ServiceEventEntity>>

    @Insert
    suspend fun insert(event: ServiceEventEntity): Long
}