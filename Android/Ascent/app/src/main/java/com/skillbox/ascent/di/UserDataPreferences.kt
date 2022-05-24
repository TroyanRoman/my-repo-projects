package com.skillbox.ascent.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.oauth_data.AuthConfig
import com.skillbox.ascent.oauth_data.models.TokenModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataPreferences@Inject constructor(@ApplicationContext context : Context) {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

    private  val prefs =  EncryptedSharedPreferences.create(
        "shared_prefs_filename.txt",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getRequiredUser (prefKey: String) : String {
        return prefs.getString(prefKey, "")?: ""
    }

    fun getRequiredUserId(key: String) : Long {
        return prefs.getLong(key,0)
    }

    fun setStoredUser(user : AscentUser) {
        prefs.edit()
            .putString(AuthConfig.FULL_NAME_KEY, user.firstName + " " + user.lastName)
            .putString(AuthConfig.AVATAR_KEY, user.avatar)

            .apply()


    }
}