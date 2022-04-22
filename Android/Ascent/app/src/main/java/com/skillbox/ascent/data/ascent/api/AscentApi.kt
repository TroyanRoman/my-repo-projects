package com.skillbox.ascent.data.ascent.api

import com.skillbox.ascent.data.ascent.models.AscentUser
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AscentApi {



     @GET("athlete")
     suspend fun getAuthenticatedUser() : AscentUser

     @PUT("athlete")
     suspend fun updateUser(
          @Query("weight") weight : Float
     )


}