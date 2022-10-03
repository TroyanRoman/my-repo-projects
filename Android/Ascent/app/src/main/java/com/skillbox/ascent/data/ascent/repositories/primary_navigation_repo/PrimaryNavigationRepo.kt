package com.skillbox.ascent.data.ascent.repositories.primary_navigation_repo

interface PrimaryNavigationRepo {

    suspend fun provideNavigation(
        onLoginNeeded: () -> Unit,
        onLoggedIn: () -> Unit,
        onFirstEnter: () -> Unit,
    )

    fun provideLoginNavigation(
        onLoginNeeded: () -> Unit,
    )
}