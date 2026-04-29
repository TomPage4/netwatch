package com.example.mob_dev_portfolio.data.dto

import java.time.LocalDateTime

data class NetworkWithDeviceCountDTO (

    val id: Long,
    val ssid: String,
    val lastSeen: LocalDateTime,
    val deviceCount: Int
)