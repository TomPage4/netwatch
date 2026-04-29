package com.example.mob_dev_portfolio.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mob_dev_portfolio.data.converter.DateTimeConverters
import java.time.LocalDateTime

@Entity(
    tableName = "app_settings"
)
@TypeConverters(DateTimeConverters::class)
data class AppSettingEntity(

    @PrimaryKey val id: Long = 1L,
    @ColumnInfo(name = "risk_rule") val riskRule: RiskRule,
    @ColumnInfo(name = "retention_period") val retentionPeriod: RetentionPeriod,
    @ColumnInfo(name = "last_clear") val lastClear: LocalDateTime,
    @ColumnInfo(name = "updated_at") val updatedAt: LocalDateTime
)
