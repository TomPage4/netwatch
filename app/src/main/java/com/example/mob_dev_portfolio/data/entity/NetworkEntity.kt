package com.example.mob_dev_portfolio.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mob_dev_portfolio.data.converter.DateTimeConverters
import java.time.LocalDateTime

@Entity(
    tableName = "networks",
)
@TypeConverters(DateTimeConverters::class)
data class NetworkEntity(

    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val ssid: String? = null,
    val bssid: String? = null,
    val securityType: Int? = null,
    @ColumnInfo(name = "first_seen") val firstSeen: LocalDateTime,
    @ColumnInfo(name = "last_seen") val lastSeen: LocalDateTime,
    @ColumnInfo(name = "total_scans") val totalScans: Int
)
