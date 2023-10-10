package com.example.yourdrive

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.yourdrive.presentation.FirstScreen
import com.github.johnnysc.coremvvm.core.Dispatchers
import com.github.johnnysc.coremvvm.presentation.BackPress
import com.github.johnnysc.coremvvm.presentation.CanGoBack
import com.github.johnnysc.coremvvm.presentation.GlobalErrorCommunication
import com.github.johnnysc.coremvvm.presentation.NavigationCommunication
import com.github.johnnysc.coremvvm.presentation.NavigationScreen
import com.github.johnnysc.coremvvm.presentation.ProgressCommunication
import com.github.johnnysc.coremvvm.presentation.Visibility


class MainViewModel(
    canGoBack: CanGoBack,
    private val navigationCommunication: NavigationCommunication.Mutable,
    private val progressCommunication: ProgressCommunication.Mutable,
    dispatchers: Dispatchers,
    communication: GlobalErrorCommunication.Mutable
) : BackPress.Activity.ViewModel<String>(
    canGoBack, communication, dispatchers
) {

    init {
        navigationCommunication.map(FirstScreen)
    }

    fun observeNavigation(owner: LifecycleOwner, observer: Observer<NavigationScreen>) =
        navigationCommunication.observe(owner, observer)

    fun observeProgress(owner: LifecycleOwner, observer: Observer<Visibility>) {
        progressCommunication.observe(owner, observer)
    }

}
