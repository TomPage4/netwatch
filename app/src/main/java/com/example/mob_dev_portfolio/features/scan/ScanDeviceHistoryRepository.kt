package com.example.mob_dev_portfolio.features.scan

import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.dto.ScanDeviceHistoryWithDeviceDTO
import com.example.mob_dev_portfolio.data.entity.ScanDeviceHistoryEntity
import kotlinx.coroutines.flow.Flow

class ScanDeviceHistoryRepository(
    private val db: AppDatabase
) {

    fun observeDevicesByScanId(scanId: Long): Flow<List<ScanDeviceHistoryWithDeviceDTO>> {
        return db.scanDeviceHistoryDao().observeDevicesByScanId(scanId)
    }
}