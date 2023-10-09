package com.example.yourdrive.presentation

import com.github.johnnysc.coremvvm.presentation.NavigationScreen
import com.github.johnnysc.coremvvm.presentation.ShowStrategy

object FirstScreen : NavigationScreen(
    FirstScreenFragment::class.java.name,
    FirstScreenFragment::class.java,
    ShowStrategy.REPLACE
)