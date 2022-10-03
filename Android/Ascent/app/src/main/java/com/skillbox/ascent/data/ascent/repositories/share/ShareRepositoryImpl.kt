package com.skillbox.ascent.data.ascent.repositories.share

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.skillbox.ascent.data.ascent.models.AscentContact
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.UnsupportedEncodingException
import javax.inject.Inject

class ShareRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context,
    @DispatcherIO val contextDispatcher: CoroutineDispatcher,
) : ShareRepository {

    override suspend fun getAllContacts(): List<AscentContact> {
        return withContext(contextDispatcher) {
            val contactsList = context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )?.use { cursor ->
                getContactsFromCursor(cursor)
            }.orEmpty()

            contactsList
        }
    }


    override suspend fun shareContact(
        phoneNumber: String, user: AscentUser
    ) =
        withContext(contextDispatcher) {
            val link = getLinkEncoded(user)
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, link)
                putExtra("address", phoneNumber)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)

            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(shareIntent)
        }

    private fun getLinkEncoded(user: AscentUser): String {
        return try {
            val url = "https://www.strava.com/athletes/${user.id}"
            val queryString = "f=${user.firstName}" +
                    "&l=${user.lastName}&w=${user.weight}&fls=${user.followersCount}" +
                    "&flg=${user.friendsCount}&g=${user.gender}&c=${user.country}" +
                    "&a=${user.avatar}"
            val encodedQueryString = Uri.encode(queryString)

            "$url?$encodedQueryString"

        } catch (e: UnsupportedEncodingException) {
            Log.d("Exception", "Unsupported exception while encoding = $e")
            ""
        }
    }


    private fun getContactsFromCursor(cursor: Cursor): List<AscentContact> {
        if (cursor.moveToFirst().not()) return emptyList()
        val contactsList = mutableListOf<AscentContact>()
        do {
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val name = cursor.getString(nameIndex).orEmpty()

            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val id = cursor.getLong(idIndex)

            val phonesList = getPhonesForContact(id)
            try {
                val phoneNumber: String = phonesList?.first() ?: ""
                val thumbnailId = fetchThumbnailId(phoneNumber)

                contactsList.add(
                    AscentContact(
                        id = id,
                        name = name,
                        avatar = thumbnailId?.let { fetchAvatar(it) },
                        phoneNumbers = getPhonesForContact(id) ?: emptyList()
                    )
                )
            } catch (e: NoSuchElementException) {
                Log.d("ContactLog", "Empty phone number")
            }


        } while (cursor.moveToNext())



        return contactsList
    }

    @SuppressLint("Range")
    private fun fetchThumbnailId(phoneNumber: String): Int? {


        val uri = Uri.withAppendedPath(
            ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val cursor = context.contentResolver.query(
            uri,
            PHOTO_ID_PROJECTION,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )
        val photoId = cursor.use {
            var thumbnailId: Int? = null
            if (it?.moveToFirst() == true) {
                thumbnailId =
                    it.getInt(it.getColumnIndex(ContactsContract.Contacts.PHOTO_ID))
            }
            thumbnailId

        }

        return photoId
    }

    private fun fetchAvatar(thumbnailId: Int): Bitmap? {
        val uri: Uri = ContentUris.withAppendedId(
            ContactsContract.Data.CONTENT_URI,
            thumbnailId.toLong()
        )
        val cursor: Cursor? = context.contentResolver.query(
            uri,
            PHOTO_BITMAP_PROJECTION,
            null,
            null,
            null
        )
        val avatarBitmap = cursor.use {
            var avatar: Bitmap? = null
            if (it?.moveToFirst() == true) {
                val thumbnailBytes: ByteArray = it.getBlob(0)
                avatar =
                    BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.size)
            }
            avatar
        }

        return avatarBitmap
    }


    private fun getPhonesForContact(contactId: Long): List<String>? {
        val phonesList = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null
        )?.use {
            getPhonesFromCursor(it)
        }.orEmpty()

        return phonesList
    }

    private fun getPhonesFromCursor(cursor: Cursor): List<String> {
        if (cursor.moveToFirst().not()) return emptyList()
        val list = mutableListOf<String>()
        do {

            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val number = cursor.getString(numberIndex)
            list.add(number)
        } while (cursor.moveToNext())
        return list
    }


    companion object {
        private val PHOTO_ID_PROJECTION = arrayOf<String>(
            ContactsContract.Contacts.PHOTO_ID
        )
        private val PHOTO_BITMAP_PROJECTION = arrayOf<String>(
            ContactsContract.CommonDataKinds.Photo.PHOTO
        )
    }

}