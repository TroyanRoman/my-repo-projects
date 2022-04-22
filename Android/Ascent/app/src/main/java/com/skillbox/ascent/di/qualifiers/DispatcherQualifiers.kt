package com.skillbox.ascent.di.qualifiers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherIO

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherMain

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherDefault