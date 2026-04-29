package com.example.mob_dev_portfolio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mob_dev_portfolio.data.entity.ScanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDAO {

    @Query("SELECT * FROM scans WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long?): ScanEntity

    @Query("""
        SELECT * FROM scans
        WHERE network_id = :networkId
        AND ended_at IS NOT null
        ORDER BY ended_at DESC
    """)
    fun observeScansByNetworkId(networkId: Long): Flow<List<ScanEntity>>

    @Insert
    suspend fun insert(entity: ScanEntity): Long

    @Update
    suspend fun update(service: ScanEntity)
}