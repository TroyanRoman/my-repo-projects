package com.skillbox.humblr.di

import com.skillbox.humblr.data.MainRepository
import com.skillbox.humblr.data.MainRepositoryImpl
import com.skillbox.humblr.oauth_data.AuthRepository
import com.skillbox.humblr.oauth_data.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AuthModule {
    @Binds
    @ViewModelScoped
    abstract fun provideAuthRepository(impl : AuthRepositoryImpl): AuthRepository


}