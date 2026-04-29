package com.example.mob_dev_portfolio.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mob_dev_portfolio.data.constants.ScanType
import com.example.mob_dev_portfolio.data.converter.DateTimeConverters
import com.example.mob_dev_portfolio.data.converter.RiskFindingConverter
import com.example.mob_dev_portfolio.data.dto.RiskFindingDTO
import java.time.LocalDateTime

@Entity(
    tableName = "devices"
)
@TypeConverters(DateTimeConverters::class, RiskFindingConverter::class)
data class DeviceEntity(

    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "network_id") val networkId: Long,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "ip_address") val ipAddress: String? = null,
    @ColumnInfo(name = "service_count") val serviceCount: Int,
    @ColumnInfo(name = "is_trusted") val isTrusted: Boolean,
    @ColumnInfo(name = "is_new") val isNew: Boolean,
    @ColumnInfo(name = "is_changed") val isChanged: Boolean,
    @ColumnInfo(name = "discovery_source") val scanType: String,
    @ColumnInfo(name = "risk_rating") val riskRating: RiskRating,
    @ColumnInfo(name = "risk_finding") val riskFinding: List<RiskFindingDTO>,
    @ColumnInfo(name = "risk_mode_at_rating") val riskRuleAtRating: RiskRule,
    @ColumnInfo(name = "first_seen") val firstSeen: LocalDateTime,
    @ColumnInfo(name = "last_seen") val lastSeen: LocalDateTime,
    @ColumnInfo(name = "last_changed") val lastChanged: LocalDateTime? = null
)
