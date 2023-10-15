package com.example.yourdrive.sl

import com.example.yourdrive.MainViewModel
import com.example.yourdrive.core_add.YouDriveCore
import com.github.johnnysc.coremvvm.presentation.CanGoBack
import com.github.johnnysc.coremvvm.presentation.NavigationCommunication
import com.github.johnnysc.coremvvm.sl.CoreModule
import com.github.johnnysc.coremvvm.sl.Module

class MainModule(private val coreModule: CoreModule) : Module<MainViewModel> {
    override fun viewModel(): MainViewModel = MainViewModel(
        navigationCommunication = NavigationCommunication.Base(),
        progressCommunication = coreModule.provideProgressCommunication(),
        dispatchers = coreModule.dispatchers(),
        communication = coreModule.provideGlobalErrorCommunication(),
        canGoBack = CanGoBack.Empty()
    )
}