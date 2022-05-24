package com.skillbox.ascent.data.ascent.repositories.share

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.navigation.NavDeepLinkBuilder
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.AscentContact
import com.skillbox.ascent.di.qualifiers.DispatcherIO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
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

            Log.d("ContactLog", "contacts list in repo method getAllContacts = $contactsList ")
            contactsList
        }
    }

    override suspend fun shareContact(userId: Long, phoneNumber: String) =
        withContext(contextDispatcher) {

            val subject = "Please, checkout my Strava profile :"
            val link =
                "https://www.strava.com/athletes/$userId"


            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("smsto:")
                type = "vnd.android-dir/mms-sms"


                putExtra("address", phoneNumber)
                putExtra("sms_body", link)
            }


            val shareIntent = Intent.createChooser(intent, null)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(shareIntent)
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
            Log.d("PhoneLog", "Phones from contact $id = $phonesList")
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

        Log.d("ContactLog", "contacts list from repo method getContactsFromCursor = $contactsList")

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
        val photoId = cursor.use { cursor ->
            var thumbnailId: Int? = null
            if (cursor?.moveToFirst() == true) {
                thumbnailId =
                    cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID))
            }
            thumbnailId

        }
        Log.d("ContactLog", "photo id from method fetchThumbnailId = $photoId")
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
        val avatarBitmap = cursor.use { cursor ->
            var avatar: Bitmap? = null
            if (cursor?.moveToFirst() == true) {
                val thumbnailBytes: ByteArray = cursor.getBlob(0)
                avatar =
                    BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.size)
            }
            avatar
        }
        Log.d("ContactLog", "avatar bitmap from method fetchAvatar = $avatarBitmap")
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
        Log.d("ContactLog", "phones list from method getPhonesForContact = $phonesList ")
        return phonesList
    }

    private fun getPhonesFromCursor(cursor: Cursor): List<String> {
        if (cursor.moveToFirst().not()) return emptyList()
        val list = mutableListOf<String>()
        do {

            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val number = cursor.getString(numberIndex)

            Log.d("ContactLog", "Number Index from method getPhonesFromCursor = $numberIndex")
            Log.d("ContactLog", "Number from method getPhonesFromCursor = $number")

            list.add(number)


        } while (cursor.moveToNext())
        Log.d("ContactLog", "Contacts phones from method getPhonesFromCursor = $list")
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