package com.example.mob_dev_portfolio.features.device

import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.constants.EventType
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.DeviceEventEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class DeviceRepository(
    private val db: AppDatabase
) {

    suspend fun getDeviceById(id: Long): DeviceEntity? {
        return db.deviceDao().findById(id)
    }

    fun observeDevicesByNetworkId(networkId: Long): Flow<List<DeviceEntity>> {
        return db.deviceDao().observeDevicesWithServiceCountByNetworkId(networkId)
    }

    fun observeChangedByNetworkId(networkId: Long, lastScanOnly: Boolean): Flow<List<DeviceEntity>> {
        return db.scanDeviceHistoryDao().observeChangedByNetworkId(networkId, lastScanOnly)
    }

    fun observeNewByNetworkId(networkId: Long, lastScanOnly: Boolean): Flow<List<DeviceEntity>> {
        return db.scanDeviceHistoryDao().observeNewByNetworkId(networkId, lastScanOnly)
    }

    suspend fun markTrusted(device: DeviceEntity) {
        val existing = db.deviceDao().findById(device.id) ?: return

        db.deviceDao().update(
            existing.copy(
                isTrusted = true
            )
        )

        db.deviceEventDao().insert(
            DeviceEventEntity(
                deviceId = existing.id,
                timestamp = LocalDateTime.now(),
                eventType = EventType.TRUSTED_ON
            )
        )
    }

    suspend fun removeTrusted(device: DeviceEntity) {
        val existing = db.deviceDao().findById(device.id) ?: return

        db.deviceDao().update(
            existing.copy(
                isTrusted = false
            )
        )

        db.deviceEventDao().insert(
            DeviceEventEntity(
                deviceId = existing.id,
                timestamp = LocalDateTime.now(),
                eventType = EventType.TRUSTED_OFF
            )
        )
    }
}