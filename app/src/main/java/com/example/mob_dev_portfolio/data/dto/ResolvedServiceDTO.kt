package com.example.mob_dev_portfolio.data.dto

data class ResolvedServiceDTO(
    val name: String,
    val type: String,
    val port: Int,
    val ipAddress: String?,
    val server: String?
)
