package com.skillbox.ascent.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.oauth_data.AuthTokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val authTokenRepository: AuthTokenRepository) :
    ViewModel() {

    fun authTokenRefresh() {
        viewModelScope.launch {
            try {
                authTokenRepository.performNewAccessTokenRequest()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }
}