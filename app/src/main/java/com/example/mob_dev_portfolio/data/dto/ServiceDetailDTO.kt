package com.example.mob_dev_portfolio.data.dto

import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.ResolveStatus
import com.example.mob_dev_portfolio.data.entity.RiskRule
import com.example.mob_dev_portfolio.data.entity.RiskRating
import java.time.LocalDateTime

data class ServiceDetailDTO (

    val id: Long,
    val networkId: Long,
    val device: DeviceEntity? = null,
    val name: String,
    val type: String? = null,
    val hostname: String? = null,
    val ipAddress: String? = null,
    val port: Int? = null,
    val txtRecord: String? = null,
    val resolveStatus: ResolveStatus,
    val firstSeen: LocalDateTime,
    val lastSeen: LocalDateTime,
    val lastChanged: LocalDateTime? = null,
    val isNew: Boolean,
    val isChanged: Boolean,
    val riskRating: RiskRating,
    val riskFinding: List<RiskFindingDTO>,
    val riskRuleAtRating: RiskRule
)