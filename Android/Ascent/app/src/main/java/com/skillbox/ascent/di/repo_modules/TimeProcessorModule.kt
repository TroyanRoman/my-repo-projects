package com.skillbox.ascent.di.repo_modules

import com.skillbox.ascent.data.ascent.repositories.activities.time_processing_repo.TimeProcessing
import com.skillbox.ascent.data.ascent.repositories.activities.time_processing_repo.TimeProcessingImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class TimeProcessorModule {
    @Binds
    @ViewModelScoped
    abstract fun bindTimeProcessor(impl : TimeProcessingImpl): TimeProcessing
}