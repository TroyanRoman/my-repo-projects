package com.skillbox.ascent.data.ascent.repositories.share

import com.skillbox.ascent.data.ascent.models.AscentContact
import com.skillbox.ascent.data.ascent.models.AscentUser

interface ShareRepository {
    suspend fun getAllContacts() : List<AscentContact>
    suspend fun shareContact( phoneNumber: String, user: AscentUser)
}