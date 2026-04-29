package com.example.mob_dev_portfolio.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_types",
)
data class ServiceTypeEntity(

    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "service_type") val serviceType: String,
    @ColumnInfo(name = "selected") val selected: Boolean
)