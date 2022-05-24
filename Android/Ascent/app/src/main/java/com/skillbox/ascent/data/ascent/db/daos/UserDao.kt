package com.skillbox.ascent.data.ascent.db.daos

import androidx.room.*
import com.skillbox.ascent.data.ascent.db.DatabaseContract
import com.skillbox.ascent.data.ascent.models.AscentUser
/*
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthorizedUser(user: AscentUser)

    @Query("SELECT * FROM ${DatabaseContract.UserContract.TABLE_NAME} WHERE ${DatabaseContract.UserContract.Columns.ID} =:userId")
    suspend fun getCurrentUserById(userId : Long)

    @Update
    suspend fun updateUser(user: AscentUser)
}

 */