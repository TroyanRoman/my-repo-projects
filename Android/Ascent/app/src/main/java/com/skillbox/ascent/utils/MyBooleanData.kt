package com.skillbox.ascent.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//сделал враппер для статуса загрузки новых активностей. Механизм такой- активность подгружается(или нет)
//и пользователя переносит на экран со списком активностей, где в зависимости от статуса подгрузки пользователю
// показывается сообщение об удачной или не удачной загрузке активности. Но, так как на этот экран
// можно так же попасть из боттом бара и нажав на приходящее напоминание в нотификации, то потребовалась нуллабельная
// булин дата, чтобы можно было дефолтное третье значение передать на случай, если нет никакого сообщения,
// но без враппера ее нельзя передать через навигацию. В принципе можно было бы передавать статус
// каким-нибудь целым числом, но с ними со временем можно запутаться, случайно где-то не то передать.
// А тут все просто - загрузилось, не загрузилось , дефолтное нулловое
@Parcelize
data class MyBooleanData(
    val isDataUploaded: Boolean?
) : Parcelable