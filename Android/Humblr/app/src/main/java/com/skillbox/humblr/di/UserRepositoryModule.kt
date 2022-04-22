package com.skillbox.humblr.di

import com.skillbox.humblr.data.UserRepository
import com.skillbox.humblr.data.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class UserRepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun provideUserRepository(impl: UserRepositoryImpl): UserRepository
}