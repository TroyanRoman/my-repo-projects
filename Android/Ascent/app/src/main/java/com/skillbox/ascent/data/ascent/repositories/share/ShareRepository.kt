package com.skillbox.ascent.data.ascent.repositories.share

import android.content.Context
import com.skillbox.ascent.data.ascent.models.AscentContact

interface ShareRepository {
    suspend fun getAllContacts() : List<AscentContact>
    suspend fun shareContact(userId : Long, phoneNumber: String)
}