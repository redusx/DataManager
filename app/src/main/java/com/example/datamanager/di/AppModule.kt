package com.example.datamanager.di

import android.content.Context
import com.example.datamanager.data.dao.DataEntryDao
import com.example.datamanager.data.database.AppDatabase
import com.example.datamanager.data.security.CryptoManager
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
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager
    ): AppDatabase {
        return AppDatabase.getInstance(context, cryptoManager)
    }

    @Provides
    @Singleton
    fun provideDataEntryDao(database: AppDatabase): DataEntryDao {
        return database.dataEntryDao()
    }
}
