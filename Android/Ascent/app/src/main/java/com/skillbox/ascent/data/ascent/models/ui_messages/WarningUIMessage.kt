package com.skillbox.ascent.data.ascent.models.ui_messages

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.skillbox.ascent.R

data class WarningUIMessage(
    @StringRes override val textRes: Int,
    @ColorRes override val textColorRes: Int = R.color.snackWarningTextColor,
    @ColorRes override val backColorRes: Int = R.color.snackWarningBackColor,
    override val pictureRes: Int = R.drawable.ic_big_bell
) : UIMessage()
