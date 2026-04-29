package com.example.mob_dev_portfolio.data.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.mob_dev_portfolio.data.entity.ScanServiceHistoryEntity

data class ScanServiceWithTrustedDTO(

    @Embedded val scanServiceHistory: ScanServiceHistoryEntity,
    @ColumnInfo(name = "service_is_trusted") val serviceIsTrusted: Boolean
)
