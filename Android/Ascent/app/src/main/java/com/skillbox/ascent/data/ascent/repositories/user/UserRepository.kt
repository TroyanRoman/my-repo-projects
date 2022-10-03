package com.skillbox.ascent.data.ascent.repositories.user

import com.skillbox.ascent.data.ascent.models.AscentUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getUserIdentity() : Flow<AscentUser>

    suspend fun updateUserIdentity(weight : Float)

    suspend fun showNotificationMessage()

    fun storeUserData(user: AscentUser)

}