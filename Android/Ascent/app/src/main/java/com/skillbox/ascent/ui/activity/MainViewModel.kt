package com.skillbox.ascent.ui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.oauth_data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

   fun authTokenRefresh() {
       viewModelScope.launch {
           try {
               authRepository.performNewAccessTokenRequest()
               Log.d("AuthChange","Auth change success")
           }catch (t : Throwable) {
               Log.d("AuthChange","Auth change error = $t")
           }
       }
   }
}