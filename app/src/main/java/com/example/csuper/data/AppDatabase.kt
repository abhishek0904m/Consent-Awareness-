package com.example.csuper.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.csuper.data.dao.CorrelationResultDao
import com.example.csuper.data.dao.SensorEventDao
import com.example.csuper.data.dao.UiEventDao
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

/**
 * Main Room Database for C-SUPER
 * All data is encrypted using SQLCipher for privacy protection
 * 
 * Privacy Statement:
 * This database stores all sensor and UI data locally with AES encryption.
 * No data is transmitted to external servers.
 */
@Database(
    entities = [
        SensorEventEntity::class,
        UiEventEntity::class,
        CorrelationResultEntity::class,
        ConsentReceiptEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun sensorEventDao(): SensorEventDao
    abstract fun uiEventDao(): UiEventDao
    abstract fun correlationResultDao(): CorrelationResultDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "csuper_db"
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context)
                INSTANCE = instance
                instance
            }
        }
        
        private fun buildDatabase(context: Context): AppDatabase {
            // Use a simple passphrase derived from app-specific data
            // In production, this should use Android Keystore
            val passphrase = SQLiteDatabase.getBytes("csuper_encryption_key_v1".toCharArray())
            val factory = SupportFactory(passphrase)
            
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Database created
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
        }
        
        fun clearInstance() {
            INSTANCE = null
        }
    }
}
