package com.example.mob_dev_portfolio.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mob_dev_portfolio.data.converter.DateTimeConverters
import java.time.LocalDateTime

@Entity(
    tableName = "service_events"
)
@TypeConverters(DateTimeConverters::class)
data class ServiceEventEntity(

    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "service_id") val serviceId: Long,
    val timestamp: LocalDateTime,
    @ColumnInfo(name = "event_type") val eventType: String,
    val change: String? = null
)
