package com.skillbox.ascent.utils

import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skillbox.ascent.R

fun <T : Any, VH : RecyclerView.ViewHolder> SwipeRefreshLayout.initSwipeToRefresh(
    adapter: PagingDataAdapter<T, VH>
) {
    this.setOnRefreshListener {
        isRefreshing = false
        adapter.refresh()
    }
}



