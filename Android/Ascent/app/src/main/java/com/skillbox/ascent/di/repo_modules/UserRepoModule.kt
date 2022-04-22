package com.skillbox.ascent.di.repo_modules

import com.skillbox.ascent.data.ascent.repositories.user.UserRepository
import com.skillbox.ascent.data.ascent.repositories.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class UserRepoModule {
    @Binds
    @ViewModelScoped
    abstract fun bindUserRepository(impl : UserRepositoryImpl) : UserRepository
}