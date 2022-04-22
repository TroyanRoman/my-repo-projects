package com.skillbox.humblr.oauth_data


import com.skillbox.humblr.di.AuthTokenPreference
import okhttp3.*
import javax.inject.Inject

class AuthTokenInterceptor @Inject constructor(private val authTokenPreference: AuthTokenPreference) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val accessToken = authTokenPreference.getStoredData()
        val modifiedRequest = original.newBuilder()
            .addHeader("User-Agent", "Humblr 1.001 by/u/RomaTr  com.skillbox.humblr")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "bearer $accessToken")
            .method(original.method, original.body)
            .build()


        return chain.proceed(modifiedRequest)
    }
}