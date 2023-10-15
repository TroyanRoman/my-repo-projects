package com.example.yourdrive.presentation.authorization.data


interface Token {

    interface Save : com.github.johnnysc.coremvvm.core.Save<TokenModel>
    interface Read : com.github.johnnysc.coremvvm.core.Read<String>

    interface Mutable : Save, Read

    fun corruptAccessToken()
    fun tokenExpirationTimeInMillis(): Long

    class Base(private val preferences: ProvideEncryptedPreferences) : Mutable, Token {

        private val prefs = preferences.sharedPreferences("token_key_prefs")

        override fun read(): String = prefs.getString(AuthConfig.ACCESS_PREF_KEY, "") ?: ""

        override fun save(data: TokenModel) = prefs.edit()
            .putString(AuthConfig.ACCESS_PREF_KEY, data.accessToken)
            .putString(AuthConfig.REFRESH_PREF_KEY, data.refreshToken)
            .putString(AuthConfig.EXPIRED_TIME_PREF_KEY, data.expirationTimeInMillis.toString())
            .apply()

        override fun corruptAccessToken() =
            prefs.edit().putString(AuthConfig.ACCESS_PREF_KEY, "corrupted_key").apply()

        override fun tokenExpirationTimeInMillis(): Long =
            prefs.getString(AuthConfig.EXPIRED_TIME_PREF_KEY, "0")?.toLong() ?: 0
    }

}