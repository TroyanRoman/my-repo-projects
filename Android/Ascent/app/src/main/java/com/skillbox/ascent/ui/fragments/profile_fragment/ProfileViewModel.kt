package com.skillbox.ascent.ui.fragments.profile_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.data.ascent.repositories.user.UserRepository
import com.skillbox.ascent.oauth_data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepo : UserRepository,
    private val authRepo : AuthRepository
) : ViewModel()  {

    private val _userLiveData = MutableLiveData<AscentUser>()
    val userLiveData: LiveData<AscentUser>
        get() = _userLiveData


    fun getUserIdentity() {
        viewModelScope.launch {
            try {
                val user = userRepo.getUserIdentity()
                _userLiveData.postValue(user)
            } catch (t: Throwable) {

            }
        }
    }

    fun updateUser(wheight : Float, updateCallback:(data : Float)-> Unit ) {
        viewModelScope.launch {
            try {
                userRepo.updateUserIdentity(wheight)
                updateCallback(wheight)
            } catch (t: Throwable) {
                Timber.tag("UpdateUserIdentityError").d("User Identity update error = $t")
            }
        }
    }

    fun corruptToken(){
        authRepo.corruptToken()
    }

}