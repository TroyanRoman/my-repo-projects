package com.example.yourdrive

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.yourdrive.presentation.FirstScreen

import com.github.johnnysc.coremvvm.presentation.Communication
import com.github.johnnysc.coremvvm.presentation.NavigationCommunication
import com.github.johnnysc.coremvvm.presentation.NavigationScreen

interface MainViewModel {

    fun init()

    class Base(private val navigation: NavigationCommunication.Mutable) :
        ViewModel(),
        MainViewModel,
        Communication.Observe<NavigationScreen> {

        override fun init() = navigation.map(FirstScreen)

        override fun observe(owner: LifecycleOwner, observer: Observer<NavigationScreen>) =
            navigation.observe(owner, observer)

    }
}