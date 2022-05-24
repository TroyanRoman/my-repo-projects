package com.skillbox.ascent.ui.fragments.share_fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.ascent.models.AscentContact
import com.skillbox.ascent.data.ascent.repositories.share.ShareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
@HiltViewModel
class ShareViewModel @Inject constructor(
    private val shareRepo : ShareRepository
) : ViewModel() {

    private val contactsMutableLiveData = MutableLiveData<List<AscentContact>>()
    val contacts: LiveData<List<AscentContact>>
        get() = contactsMutableLiveData

    fun loadListContacts() {
        viewModelScope.launch {
            try {
                val contacts = shareRepo.getAllContacts()
                Log.d("ContactLog", " Contacts in vm = $contacts ")
                contactsMutableLiveData.postValue(contacts)
            } catch (t: Throwable) {
                Log.e("ContactLog", "contact list error", t)
                contactsMutableLiveData.postValue(emptyList())
            }
        }

    }

    fun shareLink(contactd: Long, phoneNumber : String) {
        viewModelScope.launch {
            try {
                shareRepo.shareContact(contactd, phoneNumber)
                Log.d("ShareContact", "Share success")
            }catch(t: Throwable) {
                Log.d("ShareContact", "Share error = $t")
            }
        }
    }
}