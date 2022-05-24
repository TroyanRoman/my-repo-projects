package com.skillbox.ascent.di.repo_modules

import com.skillbox.ascent.data.ascent.repositories.share.ShareRepository
import com.skillbox.ascent.data.ascent.repositories.share.ShareRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class SharedRepoModule {
    @Binds
    @ViewModelScoped
    abstract fun bindsShareRepository(impl : ShareRepositoryImpl) : ShareRepository
}