package com.skillbox.humblr.ui.fragments.comments.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.skillbox.humblr.R
import com.skillbox.humblr.data.RedditItem
import com.skillbox.humblr.databinding.ItemCommentBinding
import com.skillbox.humblr.databinding.ItemExpandableCommentBinding

class CommentPageAdapter(
    private val onAuthorNameClicked: (comment: RedditItem.RedditComment) -> Unit,
    private val onSaveCommentBtnClicked: (comment: RedditItem.RedditComment) -> Unit
) : PagingDataAdapter<RedditItem.RedditComment, CommentViewHolder>(CommentDiffItemCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemCommentBinding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(
            onAuthorNameClicked = onAuthorNameClicked,
            onSaveCommentBtnClicked = onSaveCommentBtnClicked,
            itemCommentBinding = itemCommentBinding
        )
    }


    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {

        getItem(position)?.let {
            holder.bind(it)
        }
      //  getItem(position)?.let { comment ->
       //     with(holder) {
              //  val commentDepth = comment.replies?.size ?: 0
         //       bind(comment)
              //  itemCommentBinding.separatorContainer.visibility =
                //    if (comment.replies!!.isNotEmpty()) {
                 //       View.VISIBLE
               //     } else View.GONE



             //   for (commentView in 1..commentDepth) {
             //       val view: View =
              //          LayoutInflater.from(holder.itemCommentBinding.root.context).inflate(
               //             R.layout.separator_view,
                 //           holder.itemCommentBinding.separatorContainer,
                //            false
                //        )
                //    itemCommentBinding.separatorContainer.addView(view)
           //     }
            }

        }







