package com.skillbox.humblr.di

import com.skillbox.humblr.data.MainRepository
import com.skillbox.humblr.data.MainRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class SubredditRepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun provideSubredditRepository(impl: MainRepositoryImpl): MainRepository




}