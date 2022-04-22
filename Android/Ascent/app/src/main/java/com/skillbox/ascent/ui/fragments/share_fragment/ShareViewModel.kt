package com.skillbox.ascent.ui.fragments.share_fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.ascent.models.AscentContact
import com.skillbox.ascent.data.ascent.repositories.share.ShareRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ShareViewModel @Inject constructor(
    private val shareRepo : ShareRepository
) : ViewModel() {

    private val contactsMutableLiveData = MutableLiveData<List<AscentContact>>()
    val contacts: LiveData<List<AscentContact>>
        get() = contactsMutableLiveData

    fun loadListContacts() {
        viewModelScope.launch {
            try {
                contactsMutableLiveData.postValue(shareRepo.getAllContacts())
            } catch (t: Throwable) {
                Log.e("ContactListViewModel", "contact list error", t)
                contactsMutableLiveData.postValue(emptyList())
            }
        }

    }
}