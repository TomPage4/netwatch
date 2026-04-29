package com.example.mob_dev_portfolio.features.scan

import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.entity.ScanEntity
import kotlinx.coroutines.flow.Flow

class ScanRepository(
    private val db: AppDatabase
) {

    fun observeScansByNetworkId(networkId: Long): Flow<List<ScanEntity>> {
        return db.scanDao().observeScansByNetworkId(networkId)
    }

    suspend fun getScanById(id: Long): ScanEntity {
        return db.scanDao().findById(id)
    }
}