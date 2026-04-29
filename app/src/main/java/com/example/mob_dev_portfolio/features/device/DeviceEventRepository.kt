package com.example.mob_dev_portfolio.features.device

import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.entity.DeviceEventEntity
import kotlinx.coroutines.flow.Flow

class DeviceEventRepository(
    private val db: AppDatabase
) {

    fun observeDeviceLogsByDeviceId(deviceId: Long): Flow<List<DeviceEventEntity>> {
        return db.deviceEventDao().observeDeviceLogsByDeviceId(deviceId)
    }
}