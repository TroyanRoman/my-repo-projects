package com.skillbox.ascent.di.preferences

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.skillbox.ascent.data.ascent.models.AscentUser
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

    fun  getRequiredUser (prefKey: String) : String {
        return prefs.getString(prefKey, "")?: ""
    }


    fun setStoredUser(user : AscentUser) {
        prefs.edit()
            .putString(FULL_NAME_KEY, user.firstName + " " + user.lastName)
            .putString(AVATAR_KEY, user.avatar)
            .apply()


    }

    companion object {
        const val FULL_NAME_KEY = "full_name_key"
        const val AVATAR_KEY = "avatar_key"
    }
}