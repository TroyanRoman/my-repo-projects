package com.skillbox.ascent.data.ascent.db

object DatabaseContract {

    object UserContract {
        const val TABLE_NAME = "current_user"
        const val USER_ID_KEY = "current_user_id_key"
        object Columns {
            const val ID = "id"
            const val FIRST_NAME = "first_name"
            const val LAST_NAME = "last_name"
            const val GENDER = "gender"
            const val COUNTRY = "country"
            const val WEIGHT = "weight"
            const val AVATAR = "avatar"
            const val FOLLOWERS_COUNT = "followers_count"
            const val FRIENDS_COUNT = "friends_count"
        }
    }

    object ActivityContract {
        const val TABLE_NAME = "activity"
        const val ACTIVITY_ID_KEY = "activity_id_key"
        object Columns {
            const val ID = "id"
            const val PARENT_ID = "id_parent"
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