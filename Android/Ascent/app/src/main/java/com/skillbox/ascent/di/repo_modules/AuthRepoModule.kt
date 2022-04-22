package com.skillbox.ascent.di.repo_modules

import com.skillbox.ascent.oauth_data.AuthRepository
import com.skillbox.ascent.oauth_data.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AuthRepoModule {
    @Binds
    @ViewModelScoped
    abstract fun bindAuthRepository(impl : AuthRepositoryImpl): AuthRepository


}