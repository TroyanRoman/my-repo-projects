package com.skillbox.ascent.utils

import android.annotation.SuppressLint
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun getDateFromEditText(editTextField: TextInputLayout): java.util.Date? {
    val stringFromEditText = editTextField.editText?.text?.toString()
    return SimpleDateFormat("dd.MM.yyyy").parse(stringFromEditText!!)
}

@SuppressLint("SimpleDateFormat")
fun formatDate (date: java.util.Date) : String {
    return SimpleDateFormat("dd.MM.yyyy").format(date)
}