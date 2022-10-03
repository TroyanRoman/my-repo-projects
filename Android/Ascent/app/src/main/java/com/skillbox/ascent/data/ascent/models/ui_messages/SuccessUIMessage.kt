package com.skillbox.ascent.data.ascent.models.ui_messages

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.skillbox.ascent.R

data class SuccessUIMessage(
    @StringRes  override val textRes: Int,
    @ColorRes override val textColorRes: Int = R.color.snackPositiveTextColor,
    @ColorRes override val backColorRes: Int = R.color.snackPositiveBackColor,
    override val pictureRes: Int = R.drawable.ic_success_icon
) : UIMessage()
