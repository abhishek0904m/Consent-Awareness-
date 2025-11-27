package com.example.csuper.di

import android.content.Context
import com.example.csuper.data.AppDatabase
import com.example.csuper.data.Repository
import com.example.csuper.data.dao.CorrelationResultDao
import com.example.csuper.data.dao.SensorEventDao
import com.example.csuper.data.dao.UiEventDao
import com.example.csuper.data.db.ForegroundEventDao
import com.example.csuper.data.db.PermissionUsageDao
import com.example.csuper.util.ConsentStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Dependency Injection Module
 * Provides singleton instances of database, DAOs, and utilities
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideSensorEventDao(database: AppDatabase): SensorEventDao {
        return database.sensorEventDao()
    }
    
    @Provides
    @Singleton
    fun provideUiEventDao(database: AppDatabase): UiEventDao {
        return database.uiEventDao()
    }
    
    @Provides
    @Singleton
    fun provideCorrelationResultDao(database: AppDatabase): CorrelationResultDao {
        return database.correlationResultDao()
    }
    
    @Provides
    @Singleton
    fun provideForegroundEventDao(database: AppDatabase): ForegroundEventDao {
        return database.foregroundEventDao()
    }
    
    @Provides
    @Singleton
    fun providePermissionUsageDao(database: AppDatabase): PermissionUsageDao {
        return database.permissionUsageDao()
    }
    
    @Provides
    @Singleton
    fun provideRepository(
        foregroundEventDao: ForegroundEventDao,
        permissionUsageDao: PermissionUsageDao
    ): Repository {
        return Repository(foregroundEventDao, permissionUsageDao)
    }
    
    @Provides
    @Singleton
    fun provideConsentStore(
        @ApplicationContext context: Context
    ): ConsentStore {
        return ConsentStore(context)
    }
}
