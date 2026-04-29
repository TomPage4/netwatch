package com.example.mob_dev_portfolio.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mob_dev_portfolio.data.converter.DateTimeConverters
import com.example.mob_dev_portfolio.data.converter.RiskFindingConverter
import com.example.mob_dev_portfolio.data.dto.RiskFindingDTO
import com.example.mob_dev_portfolio.data.dto.ServiceUpdateDTO
import java.time.LocalDateTime

@Entity(
    tableName = "scan_service_history"
)
@TypeConverters(DateTimeConverters::class, RiskFindingConverter::class)
data class ScanServiceHistoryEntity(

    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "scan_id") val scanId: Long,
    @ColumnInfo(name = "network_id") val networkId: Long,
    @ColumnInfo(name = "service_id") val serviceId: Long,
    @ColumnInfo(name = "device_id") val deviceId: Long,
    val name: String,
    val type: String? = null,
    @ColumnInfo(name = "ip_address") val ipAddress: String? = null,
    val port: Int? = null,
    @ColumnInfo(name = "resolve_status") val resolveStatus: ResolveStatus,
    @ColumnInfo(name = "first_seen") val firstSeen: LocalDateTime,
    @ColumnInfo(name = "last_seen") val lastSeen: LocalDateTime,
    @ColumnInfo(name = "last_changed") val lastChanged: LocalDateTime? = null,
    @ColumnInfo(name = "is_new") val isNew: Boolean,
    @ColumnInfo(name = "is_changed") val isChanged: Boolean,
    @ColumnInfo(name = "risk_rating") val riskRating: RiskRating,
    @ColumnInfo(name = "risk_finding") val riskFinding: List<RiskFindingDTO>,
    @ColumnInfo(name = "risk_mode_at_rating") val riskRuleAtRating: RiskRule,
    @ColumnInfo(name = "port_response") val portResponse: String? = null
)
