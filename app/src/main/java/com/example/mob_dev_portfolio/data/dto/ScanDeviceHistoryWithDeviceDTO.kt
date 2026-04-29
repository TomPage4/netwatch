package com.example.mob_dev_portfolio.data.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.ScanDeviceHistoryEntity

data class ScanDeviceHistoryWithDeviceDTO(

    @Embedded val history: ScanDeviceHistoryEntity,
    @Relation(
        parentColumn = "device_id",
        entityColumn = "id"
    )
    val device: DeviceEntity?
)
