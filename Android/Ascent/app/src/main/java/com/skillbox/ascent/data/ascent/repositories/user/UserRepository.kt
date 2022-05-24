package com.skillbox.ascent.data.ascent.repositories.user

import com.skillbox.ascent.data.ascent.models.AscentUser

interface UserRepository {

    suspend fun getUserIdentity() : AscentUser

    suspend fun updateUserIdentity(weight : Float)

    suspend fun showNotificationMessage()

    fun storeUserData(user: AscentUser)


}