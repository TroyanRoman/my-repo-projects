package com.skillbox.humblr.di


import com.skillbox.humblr.data.RedditorRepository
import com.skillbox.humblr.data.RedditorRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RedditorRepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun provideRedditorRepository(impl: RedditorRepositoryImpl): RedditorRepository
}