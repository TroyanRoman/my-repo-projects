package com.skillbox.humblr.ui.fragments.main_fragment.adapter

import androidx.recyclerview.widget.DiffUtil
import com.skillbox.humblr.data.RedditItem
//import com.skillbox.humblr.data.RedditPost
import timber.log.Timber

object RedditDiffItemCallback : DiffUtil.ItemCallback<RedditItem.RedditPost>() {


    override fun areItemsTheSame(oldItem: RedditItem.RedditPost, newItem: RedditItem.RedditPost): Boolean {

        return oldItem.subredditId == newItem.subredditId
    }

    override fun areContentsTheSame(oldItem: RedditItem.RedditPost, newItem: RedditItem.RedditPost): Boolean {
        return oldItem == newItem
    }


}