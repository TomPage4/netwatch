package com.example.mob_dev_portfolio.features.scan

import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.dto.ScanServiceWithTrustedDTO
import com.example.mob_dev_portfolio.data.entity.ScanServiceHistoryEntity
import kotlinx.coroutines.flow.Flow

class ScanServiceHistoryRepository(
    private val db: AppDatabase
) {

    fun observeServicesByScanId(scanId: Long): Flow<List<ScanServiceHistoryEntity>> {
        return db.scanServiceHistoryDao().observeServicesByScanId(scanId)
    }

    fun observeServicesByDeviceIdAndScanId(deviceId: Long, scanId: Long): Flow<List<ScanServiceHistoryEntity>> {
        return db.scanServiceHistoryDao().observeServicesByDeviceIdAndScanId(deviceId, scanId)
    }
}