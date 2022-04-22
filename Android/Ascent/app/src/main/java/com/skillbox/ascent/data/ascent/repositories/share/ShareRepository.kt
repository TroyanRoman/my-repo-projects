package com.skillbox.ascent.data.ascent.repositories.share

import com.skillbox.ascent.data.ascent.models.AscentContact

interface ShareRepository {
    suspend fun getAllContacts() : List<AscentContact>
}