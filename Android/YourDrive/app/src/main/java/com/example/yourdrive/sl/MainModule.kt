package com.example.yourdrive.sl

import com.example.yourdrive.MainViewModel
import com.github.johnnysc.coremvvm.presentation.NavigationCommunication
import com.github.johnnysc.coremvvm.sl.CoreModule
import com.github.johnnysc.coremvvm.sl.Module

class MainModule(private val coreModule: CoreModule) : Module<MainViewModel.Base> {
    override fun viewModel(): MainViewModel.Base = MainViewModel.Base(
        NavigationCommunication.Base(),
        coreModule.provideProgressCommunication(),
        coreModule.dispatchers(),
        coreModule.provideGlobalErrorCommunication()
    )
}