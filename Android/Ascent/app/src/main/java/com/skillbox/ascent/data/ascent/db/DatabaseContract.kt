package com.skillbox.ascent.data.ascent.db

object DatabaseContract {

    object ActivityContract {
        const val TABLE_NAME = "activity"
        object Columns {
            const val ID = "id"
            const val NAME = "activity_name"
            const val TYPE = "activity_type"
            const val DISTANCE = "distance"
            const val ELEVATION = "elevation"
            const val TIME = "time_elapsed"
            const val DESC = "activity_description"
            const val START_DATE = "start_date"
        }
    }



}

