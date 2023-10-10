package com.example.yourdrive.sl

import androidx.lifecycle.ViewModel
import com.example.yourdrive.MainViewModel
import com.github.johnnysc.coremvvm.sl.DependencyContainer
import com.github.johnnysc.coremvvm.sl.Module

class MainDependencyContainer(
    private val dependencyContainer: DependencyContainer,
    private val coreModule: com.github.johnnysc.coremvvm.sl.CoreModule
) : DependencyContainer {
    override fun <T : ViewModel> module(clazz: Class<T>): Module<*> =
        if (clazz == MainViewModel::class.java)
            MainModule(coreModule)
        else
            dependencyContainer.module(clazz)

}