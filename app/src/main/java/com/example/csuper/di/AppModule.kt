package com.example.csuper.di

import android.content.Context
import androidx.room.Room
import com.example.csuper.data.db.AppDatabase
import com.example.csuper.data.dao.SensorEventDao
import com.example.csuper.data.dao.UiEventDao
import com.example.csuper.data.dao.CorrelationResultDao
import com.example.csuper.data.db.PermissionUsageDao
import com.example.csuper.data.db.ForegroundEventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "csuper-db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSensorEventDao(db: AppDatabase): SensorEventDao = db.sensorEventDao()

    @Provides
    fun provideUiEventDao(db: AppDatabase): UiEventDao = db.uiEventDao()

    @Provides
    fun provideCorrelationResultDao(db: AppDatabase): CorrelationResultDao = db.correlationResultDao()

    @Provides
    fun providePermissionUsageDao(db: AppDatabase): PermissionUsageDao = db.permissionUsageDao()

    @Provides
    fun provideForegroundEventDao(db: AppDatabase): ForegroundEventDao = db.foregroundEventDao()
}