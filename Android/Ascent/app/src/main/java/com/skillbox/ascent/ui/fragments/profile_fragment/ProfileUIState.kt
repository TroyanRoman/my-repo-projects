package com.skillbox.ascent.ui.fragments.profile_fragment

import com.skillbox.ascent.data.ascent.models.AscentUser

sealed class    ProfileUIState {
    data class Success(val user : AscentUser) : ProfileUIState()
    data class Loading(val isLoading : Boolean) : ProfileUIState()
    data class Error(val errorMessage : Int) : ProfileUIState()
}
