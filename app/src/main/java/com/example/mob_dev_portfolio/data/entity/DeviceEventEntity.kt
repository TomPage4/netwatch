package com.example.mob_dev_portfolio.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mob_dev_portfolio.data.converter.DateTimeConverters
import java.time.LocalDateTime

@Entity(
    tableName = "device_events"
)
@TypeConverters(DateTimeConverters::class)
data class DeviceEventEntity(

    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "device_id") val deviceId: Long,
    val timestamp: LocalDateTime,
    @ColumnInfo(name = "event_type") val eventType: String,
    @ColumnInfo(name = "event_info") val eventInfo: String? = null
)
