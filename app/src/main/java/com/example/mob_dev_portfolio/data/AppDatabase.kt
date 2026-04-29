package com.example.mob_dev_portfolio.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mob_dev_portfolio.data.constants.ScanType
import com.example.mob_dev_portfolio.data.constants.ServiceType
import com.example.mob_dev_portfolio.data.converter.DateTimeConverters
import com.example.mob_dev_portfolio.data.dao.DeviceDAO
import com.example.mob_dev_portfolio.data.dao.DeviceEventDAO
import com.example.mob_dev_portfolio.data.dao.NetworkDAO
import com.example.mob_dev_portfolio.data.dao.ScanDAO
import com.example.mob_dev_portfolio.data.dao.ScanDeviceHistoryDAO
import com.example.mob_dev_portfolio.data.dao.ScanServiceHistoryDAO
import com.example.mob_dev_portfolio.data.dao.ServiceDAO
import com.example.mob_dev_portfolio.data.dao.ServiceEventDAO
import com.example.mob_dev_portfolio.data.dao.SettingsDAO
import com.example.mob_dev_portfolio.data.entity.AppSettingEntity
import com.example.mob_dev_portfolio.data.entity.DeviceEntity
import com.example.mob_dev_portfolio.data.entity.DeviceEventEntity
import com.example.mob_dev_portfolio.data.entity.NetworkEntity
import com.example.mob_dev_portfolio.data.entity.RetentionPeriod
import com.example.mob_dev_portfolio.data.entity.RiskRule
import com.example.mob_dev_portfolio.data.entity.ScanDeviceHistoryEntity
import com.example.mob_dev_portfolio.data.entity.ScanEntity
import com.example.mob_dev_portfolio.data.entity.ScanServiceHistoryEntity
import com.example.mob_dev_portfolio.data.entity.ScanTypeEntity
import com.example.mob_dev_portfolio.data.entity.ServiceTypeEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEntity
import com.example.mob_dev_portfolio.data.entity.ServiceEventEntity
import java.time.LocalDateTime
import java.time.ZoneId

@Database(
    entities = [
        AppSettingEntity::class,
        NetworkEntity::class,
        ServiceEntity::class,
        ServiceEventEntity::class,
        DeviceEntity::class,
        DeviceEventEntity::class,
        ScanEntity::class,
        ScanServiceHistoryEntity::class,
        ServiceTypeEntity::class,
        ScanDeviceHistoryEntity::class,
        ScanTypeEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDAO
    abstract fun serviceEventDao(): ServiceEventDAO
    abstract fun networkDao(): NetworkDAO
    abstract fun deviceDao(): DeviceDAO
    abstract fun settingsDao(): SettingsDAO
    abstract fun deviceEventDao(): DeviceEventDAO
    abstract fun scanDao(): ScanDAO
    abstract fun scanServiceHistoryDao(): ScanServiceHistoryDAO
    abstract fun scanDeviceHistoryDao(): ScanDeviceHistoryDAO

    companion object {
        fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "netwatch.db")

                .addCallback(object : RoomDatabase.Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)

                        val cursor1 = db.query("SELECT COUNT(*) FROM service_types")
                        cursor1.moveToFirst()
                        val count = cursor1.getInt(0)
                        cursor1.close()
                        if (count == 0) {
                            seedServiceTypes(db)
                        }

                        val cursor2 = db.query("SELECT COUNT(*) FROM scan_types")
                        cursor2.moveToFirst()
                        val count2 = cursor2.getInt(0)
                        cursor2.close()
                        if (count2 == 0) {
                            seedScanTypes(db)
                        }

                        val cursor3 = db.query("SELECT COUNT(*) FROM app_settings")
                        cursor3.moveToFirst()
                        val settingsCount = cursor3.getInt(0)
                        cursor3.close()
                        if (settingsCount == 0) {
                            seedAppSettings(db)
                        }
                        deleteOldData(db)
                    }

                    private fun seedAppSettings(db: SupportSQLiteDatabase) {
                        val now = LocalDateTime.now().atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
                        db.execSQL(
                            "INSERT INTO app_settings (id, risk_rule, retention_period, last_clear, updated_at) VALUES (?, ?, ?, ?, ?)",
                            arrayOf(1L, RiskRule.STANDARD, RetentionPeriod.THREE_MONTHS, now, now)
                        )
                    }

                    private fun seedServiceTypes(db: SupportSQLiteDatabase) {
                        ServiceType.allServiceTypes.forEach { serviceType ->
                            db.execSQL(
                                "INSERT INTO service_types (service_type, selected) VALUES (?, ?)",
                                arrayOf(serviceType, true)
                            )
                        }
                    }

                    private fun seedScanTypes(db: SupportSQLiteDatabase) {
                        ScanType.allScanTypes.forEach { scanType ->
                            db.execSQL(
                                "INSERT INTO scan_types (scan_type, selected) VALUES (?, ?)",
                                arrayOf(scanType, true)
                            )
                        }

                    }

                    private fun deleteOldData(db: SupportSQLiteDatabase) {
                        val cursor = db.query("SELECT retention_period FROM app_settings")
                        cursor.moveToFirst()
                        val retentionPeriod = cursor.getString(0)
                        cursor.close()
                        val period = RetentionPeriod.valueOf(retentionPeriod)
                        val days = when (period) {
                            RetentionPeriod.WEEK -> 7
                            RetentionPeriod.MONTH -> 30
                            RetentionPeriod.THREE_MONTHS -> 91
                            RetentionPeriod.SIX_MONTHS -> 182
                            RetentionPeriod.YEAR -> 365
                        }

                        val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val cutoff = now - (days * 24 * 60 * 60 * 1000L)

                        db.execSQL(
                            "DELETE FROM services WHERE last_seen < ?",
                            arrayOf(cutoff)
                        )
                        db.execSQL(
                            "DELETE FROM devices WHERE last_seen < ?",
                            arrayOf(cutoff)
                        )
                        db.execSQL(
                            "DELETE FROM device_events WHERE timestamp < ?",
                            arrayOf(cutoff)
                        )
                        db.execSQL(
                            "DELETE FROM networks WHERE last_seen < ?",
                            arrayOf(cutoff)
                        )
                        db.execSQL(
                            "DELETE FROM scan_service_history WHERE last_seen < ?",
                            arrayOf(cutoff)
                        )
                        db.execSQL(
                            "DELETE FROM scans WHERE ended_at < ?",
                            arrayOf(cutoff)
                        )
                        db.execSQL(
                            "DELETE FROM service_events WHERE timestamp < ?",
                            arrayOf(cutoff)
                        )
                        db.execSQL(
                            "DELETE FROM scan_device_history WHERE last_seen < ?",
                            arrayOf(cutoff)
                        )
                    }
                })
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}