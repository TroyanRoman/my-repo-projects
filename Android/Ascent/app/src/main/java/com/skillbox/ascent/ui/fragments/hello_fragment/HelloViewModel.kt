package com.skillbox.ascent.ui.fragments.hello_fragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.ascent.repositories.primary_navigation_repo.PrimaryNavigationRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HelloViewModel @Inject constructor(
    private val navigatorRepo: PrimaryNavigationRepo
) : ViewModel() {


    fun providePrimaryNavigation(
        onLoginNeeded: () -> Unit,
        onLoggedIn: () -> Unit,
        onFirstEnter: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                navigatorRepo.provideNavigation(
                    onLoginNeeded = onLoginNeeded,
                    onLoggedIn = onLoggedIn,
                    onFirstEnter = onFirstEnter
                )
            } catch (t: Throwable) {
                Log.d("NavigationFailed", "Navigation failed with error = $t ")
            }
        }
    }


}