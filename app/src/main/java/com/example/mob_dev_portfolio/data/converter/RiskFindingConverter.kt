package com.example.mob_dev_portfolio.data.converter

import androidx.room.TypeConverter
import com.example.mob_dev_portfolio.data.dto.RiskFindingDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RiskFindingConverter {

    private val gson = Gson()
    private val type = object : TypeToken<List<RiskFindingDTO>>() {}.type

    @TypeConverter
    fun fromRiskFindings(findings: List<RiskFindingDTO>): String {
        return gson.toJson(findings, type)
    }

    @TypeConverter
    fun toRiskFindings(json: String): List<RiskFindingDTO> {
        return gson.fromJson(json, type) ?: emptyList()
    }
}