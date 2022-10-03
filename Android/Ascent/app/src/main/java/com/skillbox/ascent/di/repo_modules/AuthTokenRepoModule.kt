package com.skillbox.ascent.di.repo_modules

import com.skillbox.ascent.oauth_data.AuthRepository
import com.skillbox.ascent.oauth_data.AuthRepositoryImpl
import com.skillbox.ascent.oauth_data.AuthTokenRepository
import com.skillbox.ascent.oauth_data.AuthTokenRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AuthTokenRepoModule {
    @Binds
    @ViewModelScoped
    abstract fun bindAuthTokenRepository(impl : AuthTokenRepositoryImpl): AuthTokenRepository
}