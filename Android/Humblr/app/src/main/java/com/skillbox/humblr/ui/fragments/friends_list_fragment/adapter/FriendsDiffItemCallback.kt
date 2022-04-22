package com.skillbox.humblr.ui.fragments.friends_list_fragment.adapter

import androidx.recyclerview.widget.DiffUtil
import com.skillbox.humblr.data.RedditItem.*

object FriendsDiffItemCallback: DiffUtil.ItemCallback<Redditor>() {
    override fun areItemsTheSame(oldItem: Redditor, newItem: Redditor): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Redditor, newItem: Redditor): Boolean {
        return oldItem == newItem
    }
}