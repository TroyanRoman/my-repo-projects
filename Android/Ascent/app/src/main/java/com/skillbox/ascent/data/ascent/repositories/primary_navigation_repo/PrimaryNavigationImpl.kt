package com.skillbox.ascent.data.ascent.repositories.primary_navigation_repo


import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.preferencesKey
import com.skillbox.ascent.di.AuthTokenState
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class PrimaryNavigationImpl @Inject constructor(
    private val authState: AuthTokenState,
    private val dataStore: DataStore<Preferences>,
) : PrimaryNavigationRepo {

    override suspend fun provideNavigation(
        onLoginNeeded: () -> Unit,
        onLoggedIn: () -> Unit,
        onFirstEnter: () -> Unit,
    ) {
        dataStore.data.collectLatest { prefs ->
            val notFirstEntry = prefs[preferencesKey<Boolean>("onBoard")]
            when {
                notFirstEntry == true && authState.isCurrentTokenInvalid() -> {
                    onLoginNeeded()
                }
                notFirstEntry == true && !authState.isCurrentTokenInvalid()  -> {
                    onLoggedIn()
                }

                else -> onFirstEnter()
            }

        }
    }

    override fun provideLoginNavigation(onLoginNeeded: () -> Unit) {
        if(authState.isCurrentTokenCorrupted()) onLoginNeeded()
    }


}