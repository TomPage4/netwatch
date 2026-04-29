package com.example.mob_dev_portfolio.features.service

import com.example.mob_dev_portfolio.data.AppDatabase
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

class ServiceRepository(
    private val db: AppDatabase
) {

    fun observeServicesByDeviceId(deviceId: Long): Flow<List<ServiceEntity>> {
        return db.serviceDao().observeServicesByDeviceId(deviceId)
    }

    fun observeServicesByNetworkId(networkId: Long): Flow<List<ServiceEntity>> {
        return db.serviceDao().observeServicesByNetworkId(networkId)
    }
}