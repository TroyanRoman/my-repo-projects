package com.skillbox.ascent.di

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.skillbox.ascent.oauth_data.AuthConfig
import com.skillbox.ascent.oauth_data.models.TokenModel
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenPreference @Inject constructor(@ApplicationContext context : Context) {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

    private  val prefs =  EncryptedSharedPreferences.create(
        "shared_preferences_filename.txt",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getRequiredToken (prefKey: String) : String {
        return prefs.getString(prefKey, "")?: ""
    }

    fun setStoredData(token : TokenModel) {
        prefs.edit()
            .putString(AuthConfig.ACCESS_PREF_KEY, token.accessToken)
            .putString(AuthConfig.REFRESH_PREF_KEY, token.refreshToken)
            .putString(AuthConfig.ID_PREF_KEY, token.idToken)
            .apply()


    }
}