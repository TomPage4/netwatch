package com.example.mob_dev_portfolio.data.converter

import com.example.mob_dev_portfolio.data.dto.ScanDeviceHistoryWithDeviceDTO
import com.example.mob_dev_portfolio.data.dto.ServiceDetailDTO
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.ScanDeviceHistoryEntity
import com.example.mob_dev_portfolio.data.entity.ScanServiceHistoryEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEntity


fun ScanServiceHistoryEntity.toServiceEntity(): ServiceEntity {
    return ServiceEntity(
        id = this.serviceId,
        networkId = this.networkId,
        deviceId = this.deviceId,
        name = this.name,
        type = this.type,
        ipAddress = this.ipAddress,
        port = this.port,
        resolveStatus = this.resolveStatus,
        firstSeen = this.firstSeen,
        lastSeen = this.lastSeen,
        lastChanged = this.lastChanged,
        isNew = this.isNew,
        isChanged = this.isChanged,
        riskRating = this.riskRating,
        riskFinding = this.riskFinding,
        riskRuleAtRating = this.riskRuleAtRating
    )
}

fun ScanDeviceHistoryWithDeviceDTO.toDeviceEntity(): DeviceEntity {
    return DeviceEntity(
        id = history.deviceId,
        networkId = history.networkId,
        displayName = history.displayName,
        ipAddress = history.ipAddress,
        serviceCount = history.serviceCount,
        isTrusted = device?.isTrusted ?: false,
        isNew = history.isNew,
        isChanged = history.isChanged,
        scanType = history.scanType,
        riskRating = history.riskRating,
        riskFinding = history.riskFinding,
        riskRuleAtRating = history.riskRuleAtRating,
        firstSeen = history.firstSeen,
        lastSeen = history.lastSeen
    )
}

fun ServiceEntity.toServiceDetailDTO(device: DeviceEntity?): ServiceDetailDTO {
    return ServiceDetailDTO(
        id = this.id,
        networkId = this.networkId,
        device = device,
        name = this.name,
        type = this.type,
        ipAddress = this.ipAddress,
        port = this.port,
        resolveStatus = this.resolveStatus,
        firstSeen = this.firstSeen,
        lastSeen = this.lastSeen,
        isNew = this.isNew,
        isChanged = this.isChanged,
        riskRating = this.riskRating,
        riskFinding = this.riskFinding,
        riskRuleAtRating = this.riskRuleAtRating
    )
}