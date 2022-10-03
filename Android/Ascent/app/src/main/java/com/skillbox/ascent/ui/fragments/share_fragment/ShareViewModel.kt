package com.skillbox.ascent.ui.fragments.share_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.ascent.models.AscentContact
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.data.ascent.repositories.share.ShareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val shareRepo: ShareRepository
) : ViewModel() {

    private val contactsMutableLiveData = MutableLiveData<List<AscentContact>>()
    val contacts: LiveData<List<AscentContact>>
        get() = contactsMutableLiveData

    private val _isLoadingContacts = MutableLiveData<Boolean>()
    val isLoadingContacts: LiveData<Boolean>
        get() = _isLoadingContacts

    fun loadListContacts() {
        viewModelScope.launch {
            _isLoadingContacts.postValue(true)
            try {
                contactsMutableLiveData.postValue(shareRepo.getAllContacts())
            } catch (t: Throwable) {
                Timber.tag("LoadContacts").d("load contacts error = $t")
                contactsMutableLiveData.postValue(emptyList())
            }finally {
                _isLoadingContacts.postValue(false)
            }
        }

    }

    fun shareLink( phoneNumber: String, user: AscentUser) {
        viewModelScope.launch {
            try {
                shareRepo.shareContact( phoneNumber, user)

            } catch (t: Throwable) {
                Timber.tag("ShareError").d("share contacts error = $t")
            }
        }
    }
}