package com.example.mob_dev_portfolio.data.dto

import java.time.LocalDateTime

sealed class ServiceUpdateDTO {
    data class Found(
        val name: String,
        val type: String,
        val updatedAt: LocalDateTime
    ): ServiceUpdateDTO()

    data class Lost(
        val name: String,
        val type: String,
        val updatedAt: LocalDateTime
    ): ServiceUpdateDTO()

    data class ResolveFailed(
        val serviceName: String,
        val serviceType: String,
        val errorCode: Int,
        val failedAt: LocalDateTime
    ): ServiceUpdateDTO()

    data class Updated(
        val service: ResolvedServiceDTO,
        val updatedAt: LocalDateTime
    ): ServiceUpdateDTO()

    data class HostDiscovered(
        val hostname: String?,
        val ipAddress: String,
        val updatedAt: LocalDateTime
    ): ServiceUpdateDTO()

    data class PortScan(
        val portResult: PortResultDTO,
        val ipAddress: String,
        val updatedAt: LocalDateTime
    ): ServiceUpdateDTO()
}
