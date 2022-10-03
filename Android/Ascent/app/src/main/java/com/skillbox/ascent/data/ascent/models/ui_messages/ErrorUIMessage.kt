package com.skillbox.ascent.data.ascent.models.ui_messages

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.skillbox.ascent.R

data class ErrorUIMessage(
    @StringRes override val textRes: Int,
    @ColorRes override val textColorRes: Int = R.color.snackNegativeTextColor,
    @ColorRes override val backColorRes: Int = R.color.snackNegativeBackColor,
    override val pictureRes: Int = R.drawable.ic_error_icon
) : UIMessage()
