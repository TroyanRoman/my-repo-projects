package com.skillbox.ascent.di.repo_modules

import com.skillbox.ascent.data.ascent.repositories.primary_navigation_repo.PrimaryNavigationImpl
import com.skillbox.ascent.data.ascent.repositories.primary_navigation_repo.PrimaryNavigationRepo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class NavigateRepoModule {

    @Binds
    @ViewModelScoped
    abstract fun bindsNavigateRepo(impl : PrimaryNavigationImpl) : PrimaryNavigationRepo
}

