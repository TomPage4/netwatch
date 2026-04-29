package com.example.mob_dev_portfolio.data.dto

import com.example.mob_dev_portfolio.data.entity.RiskRating

data class RiskFindingDTO(
    val points: Int,
    val severity: RiskRating,
    val title: String,
    val detail: String
)