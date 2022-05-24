package com.skillbox.ascent.data.ascent.api

import com.google.firebase.installations.remote.TokenResult
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.oauth_data.AuthConfig
import com.skillbox.ascent.oauth_data.models.TokenModel
import com.skillbox.ascent.oauth_data.models.TokenRefreshResponse
import net.openid.appauth.GrantTypeValues
import retrofit2.http.*

interface AscentUserApi {



     @GET("api/v3/athlete")
     suspend fun getAuthenticatedUser() : AscentUser

     @PUT("api/v3/athlete")
     suspend fun updateUser(
          @Query("weight") weight : Float
     )

     @POST("oauth/deauthorize")
     suspend fun logoutCurrentUser(
         @Query("access_token") accessToken : String
     )

     @POST("api/v3/oauth/token")
     suspend fun getRefreshedAccessToken(
          @Query("client_id") clientId: Int = AuthConfig.CLIENT_ID.toInt(),
          @Query("client_secret") clientSecret: String = AuthConfig.CLIENT_SECRET,
          @Query("grant_type") grantType: String = GrantTypeValues.REFRESH_TOKEN,
          @Query("refresh_token") refreshToken: String
     ) : TokenRefreshResponse


}