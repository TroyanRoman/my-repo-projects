package com.skillbox.ascent.oauth_data

import com.skillbox.ascent.di.preferences.AuthTokenPreference
import com.skillbox.ascent.di.qualifiers.AuthInterceptorQualifier
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

@AuthInterceptorQualifier
class AuthInterceptor @Inject constructor(private val authTokenPreference: AuthTokenPreference) : Interceptor {

       override fun intercept(chain: Interceptor.Chain): Response {
           return chain.request()
               .addTokenHeader()
               .let{chain.proceed(it)}
       }

       private fun Request.addTokenHeader() : Request {

           val headerName = "Authorization"
           return newBuilder()
               .apply {
                   val token = authTokenPreference.getRequiredToken(AuthConfig.ACCESS_PREF_KEY)
                   if (token.isNotEmpty()) {
                       header(headerName, token.withBearer())
                   }
               }.method(this.method, this.body)
               .build()
       }

       private fun String.withBearer() = "Bearer $this"


}