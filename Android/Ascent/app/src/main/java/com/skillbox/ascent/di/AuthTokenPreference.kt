package com.skillbox.ascent.di

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.skillbox.ascent.oauth_data.AuthConfig
import com.skillbox.ascent.oauth_data.models.TokenModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenPreference @Inject constructor(@ApplicationContext context: Context) {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

    private val prefs = EncryptedSharedPreferences.create(
        "shared_preferences_filename.txt",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getRequiredToken(prefKey: String): String {
        return prefs.getString(prefKey, "") ?: ""
    }


    fun setStoredData(token: TokenModel) {
        prefs.edit()
            .putString(AuthConfig.ACCESS_PREF_KEY, token.accessToken)
            .putString(AuthConfig.REFRESH_PREF_KEY, token.refreshToken)
            .putLong(AuthConfig.EXPIRED_TIME_PREF_KEY, token.expTime)
            .apply()
    }

    fun corruptAccessToken() {
        prefs.edit()
            .putString(AuthConfig.ACCESS_PREF_KEY, "corrupted_token")
            .apply()
        Log.d("AuthNotify", "current token from prefs = ${getRequiredToken(AuthConfig.ACCESS_PREF_KEY)}")
    }

     fun getTokenExpirationTime(): Long = prefs.getLong(AuthConfig.EXPIRED_TIME_PREF_KEY, 0)




}