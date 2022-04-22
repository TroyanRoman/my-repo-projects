package com.skillbox.ascent.data.ascent.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AscentContact(
    val id : Long,
    val name : String,
    val avatar: Bitmap?,
    val phoneNumbers : List<String>
) : Parcelable
