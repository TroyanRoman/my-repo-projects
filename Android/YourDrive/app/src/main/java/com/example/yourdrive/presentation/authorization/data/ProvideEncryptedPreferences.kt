package com.example.yourdrive.presentation.authorization.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.github.johnnysc.coremvvm.sl.SharedPrefs

abstract class ProvideEncryptedPreferences(
    private val context: Context,
    private val name: String,
) : SharedPrefs {

    private val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
    private val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

    override fun sharedPreferences(key: String): SharedPreferences =
        EncryptedSharedPreferences.create(
            name,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    class Release(context: Context) : ProvideEncryptedPreferences(
        context,
        "release"
    )

    class Debug(context: Context) : ProvideEncryptedPreferences(
        context,
        "debug"
    )
}





