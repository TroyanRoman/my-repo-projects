package com.skillbox.ascent.data.ascent.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skillbox.ascent.data.ascent.db.daos.ActivityDao
import com.skillbox.ascent.data.ascent.db.daos.ActivityRemoteKeyDao
import com.skillbox.ascent.data.ascent.models.sport_activity.db_entities.ActivityRemoteKeys
import com.skillbox.ascent.data.ascent.models.sport_activity.db_entities.ActivityData
import com.skillbox.ascent.utils.DateConverter


@Database(
    entities = [
        ActivityRemoteKeys::class,
        ActivityData::class],
    version = AscentDatabase.DB_VERSION,
    exportSchema = false
)

@TypeConverters(DateConverter::class)
abstract class AscentDatabase : RoomDatabase() {

    abstract fun ascentActivityDao(): ActivityDao
    abstract fun ascentActivityRemoteKeyDao(): ActivityRemoteKeyDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "ascent_database"
    }
}