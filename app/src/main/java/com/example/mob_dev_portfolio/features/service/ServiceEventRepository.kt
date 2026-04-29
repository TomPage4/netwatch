package com.example.mob_dev_portfolio.features.service

import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.entity.ServiceEventEntity
import kotlinx.coroutines.flow.Flow

class ServiceEventRepository(
    private val db: AppDatabase
) {

    fun observeServiceLogsByServiceId(serviceId: Long): Flow<List<ServiceEventEntity>> {
        return db.serviceEventDao().observeServiceLogsByServiceId(serviceId)
    }
}