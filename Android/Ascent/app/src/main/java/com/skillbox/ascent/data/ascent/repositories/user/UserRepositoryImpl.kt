package com.skillbox.ascent.data.ascent.repositories.user

import com.skillbox.ascent.data.ascent.api.AscentApi
import com.skillbox.ascent.data.ascent.models.AscentUser

import com.skillbox.ascent.di.qualifiers.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val ascentApi: AscentApi,
    @DispatcherIO val contextDispatcher: CoroutineDispatcher,
) : UserRepository {


    override suspend fun getUserIdentity(): AscentUser {
        return withContext(contextDispatcher) {
            ascentApi.getAuthenticatedUser()
        }
    }


    override suspend fun updateUserIdentity(weight: Float) {
        withContext(contextDispatcher) {
            ascentApi.updateUser(weight)
        }
    }



}