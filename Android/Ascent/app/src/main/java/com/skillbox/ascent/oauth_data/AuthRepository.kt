package com.skillbox.ascent.oauth_data


import android.content.Intent
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.TokenRequest

interface AuthRepository {

    fun getAuthRequest(): AuthorizationRequest

    fun disposeAuthService()

    fun getAuthIntent() : Intent



}