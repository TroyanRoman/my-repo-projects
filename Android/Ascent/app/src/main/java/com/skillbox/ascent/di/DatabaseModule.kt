package com.skillbox.ascent.di

import android.content.Context
import androidx.room.Room
import com.skillbox.ascent.data.ascent.db.AscentDatabase
import com.skillbox.ascent.data.ascent.db.daos.ActivityDao
import com.skillbox.ascent.data.ascent.db.daos.ActivityRemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun providesActivityKeyDao(database: AscentDatabase): ActivityRemoteKeyDao {
        return database.ascentActivityRemoteKeyDao()
    }

    @Provides
    fun providesActivityDao(database: AscentDatabase): ActivityDao {
        return database.ascentActivityDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AscentDatabase {
        return Room.databaseBuilder(
            appContext,
            AscentDatabase::class.java,
            AscentDatabase.DB_NAME
        ).build()
    }

}