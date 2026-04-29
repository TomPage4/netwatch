package com.example.mob_dev_portfolio.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mob_dev_portfolio.data.converter.DateTimeConverters
import java.time.LocalDateTime

@Entity(
    tableName = "scans"
)
@TypeConverters(DateTimeConverters::class)
data class ScanEntity(

    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "network_id") val networkId: Long,
    @ColumnInfo(name = "started_at") val startedAt: LocalDateTime,
    @ColumnInfo(name = "ended_at") val endedAt: LocalDateTime? = null,
    @ColumnInfo(name = "service_found_count") val serviceFoundCount: Int,
    @ColumnInfo(name = "device_found_count") val deviceFoundCount: Int,
    @ColumnInfo(name = "risk_mode_at_scan") val riskRuleAtScan: RiskRule
)
