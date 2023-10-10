package com.example.yourdrive

import androidx.fragment.app.FragmentManager
import com.example.yourdrive.presentation.FirstScreen
import com.github.johnnysc.coremvvm.presentation.NavigationScreen

class BaseFragmentFactory(
    containerId: Int,
    fragmentManager: FragmentManager
) : com.github.johnnysc.coremvvm.presentation.FragmentFactory.Abstract(
    containerId,
    fragmentManager
) {
    override val screens: List<NavigationScreen> = listOf(FirstScreen)
}