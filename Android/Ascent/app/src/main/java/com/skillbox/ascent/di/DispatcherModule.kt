package com.skillbox.ascent.di

import com.skillbox.ascent.di.qualifiers.DispatcherDefault
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import com.skillbox.ascent.di.qualifiers.DispatcherMain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class DispatcherModule {

    @DispatcherDefault
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @DispatcherIO
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @DispatcherMain
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}