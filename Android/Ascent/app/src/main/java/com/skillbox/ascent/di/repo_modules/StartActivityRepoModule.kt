package com.skillbox.ascent.di.repo_modules

import com.skillbox.ascent.data.ascent.repositories.activities.StartActivityRepo
import com.skillbox.ascent.data.ascent.repositories.activities.StartActivityRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class StartActivityRepoModule {

    @Binds
    @Singleton
    abstract fun bindStartActivityRepo(impl : StartActivityRepoImpl): StartActivityRepo
}