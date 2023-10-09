package com.example.yourdrive.sl

import com.github.johnnysc.coremvvm.presentation.NavigationCommunication

interface ProvideNavigation {
    fun provideNavigation(): NavigationCommunication.Mutable
}