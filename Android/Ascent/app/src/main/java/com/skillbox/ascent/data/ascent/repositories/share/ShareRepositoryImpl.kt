package com.skillbox.ascent.data.ascent.repositories.share

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import com.skillbox.ascent.data.ascent.models.AscentContact
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ShareRepositoryImpl @Inject constructor(
    @ApplicationContext val context : Context,
    @DispatcherIO val contextDispatcher: CoroutineDispatcher,
) : ShareRepository{

    override suspend fun getAllContacts(): List<AscentContact> = withContext(contextDispatcher) {
        context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )?.use { cursor->
            getContactsFromCursor(cursor)
        }.orEmpty()
    }

    private fun getContactsFromCursor(cursor: Cursor) : List<AscentContact> {
        if(cursor.moveToFirst().not()) return emptyList()
        val contactsList = mutableListOf<AscentContact>()
        do {
            val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            val name = cursor.getString(nameIndex).orEmpty()

            val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
            val id = cursor.getLong(idIndex)


            val phoneNumber = getPhonesForContact(id).first()
            val thumbnailId = fetchThumbnailId(phoneNumber)

            contactsList.add(
                AscentContact(
                    id= id,
                    name = name,
                    avatar = thumbnailId?.let { fetchAvatar(it) },
                    phoneNumbers = getPhonesForContact(id)
                )
            )

        }while (cursor.moveToNext())

        return contactsList
    }

    @SuppressLint("Range")
    private fun fetchThumbnailId(phoneNumber : String) : Int? {


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
        return cursor.use { cursor->
            var thumbnailId: Int? = null
            if(cursor?.moveToFirst() == true) {
                 thumbnailId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID))
            }
            thumbnailId

        }
    }

    private fun fetchAvatar(thumbnailId : Int) : Bitmap? {
        val uri: Uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI,
            thumbnailId.toLong()
        )
        val cursor: Cursor? = context.contentResolver.query(
            uri,
            PHOTO_BITMAP_PROJECTION,
            null,
            null,
            null
        )
        return cursor.use { cursor->
            var avatar: Bitmap? = null
            if (cursor?.moveToFirst() == true) {
                val thumbnailBytes: ByteArray = cursor.getBlob(0)
                avatar =
                    BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.size)
            }
            avatar
        }
    }



    private fun getPhonesForContact(contactId: Long): List<String> {
        return context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null
        )?.use {
            getPhonesFromCursor(it)
        }.orEmpty()
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