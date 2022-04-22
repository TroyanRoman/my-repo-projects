package com.skillbox.ascent.di.repo_modules

import com.skillbox.ascent.data.ascent.repositories.activities.ActivitiesRepository
import com.skillbox.ascent.data.ascent.repositories.activities.ActivitiesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ActivitiesRepoModule {
    @Binds
    @ViewModelScoped
    abstract fun bindActivitiesRepo(impl: ActivitiesRepositoryImpl) : ActivitiesRepository
}