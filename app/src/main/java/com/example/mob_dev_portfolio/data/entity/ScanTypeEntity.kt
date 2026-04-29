package com.example.mob_dev_portfolio.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "scan_types"
)
data class ScanTypeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "scan_type") val scanType: String,
    @ColumnInfo(name = "selected") val selected: Boolean
)