package com.skillbox.ascent.utils

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ImageOffsetDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {


        val offset = 4.fromDpToPixels(context)
        with(outRect) {
            left = offset
            top = offset
            bottom = offset
            right = offset

        }

    }

    private fun Int.fromDpToPixels(context: Context): Int {
        val density = context.resources.displayMetrics.densityDpi
        val pixelsInDp = density / DisplayMetrics.DENSITY_DEFAULT
        return this * pixelsInDp
    }
}