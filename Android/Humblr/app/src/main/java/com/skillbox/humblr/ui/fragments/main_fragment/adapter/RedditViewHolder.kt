package com.skillbox.humblr.ui.fragments.main_fragment.adapter

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.skillbox.humblr.R
import com.skillbox.humblr.data.RedditItem
import com.skillbox.humblr.databinding.ItemSubsBinding
import com.skillbox.humblr.utils.convertDpToPixel
import com.skillbox.humblr.utils.decorateStringWithSpan


class RedditViewHolder(
    private val itemSubsBinding: ItemSubsBinding,
    onSubscribeBtnClicked: (post: RedditItem.RedditPost) -> Unit,
    onOpenCommentsBtnClicked: (post: RedditItem.RedditPost) -> Unit,
    onAuthorNameClicked: (redditor: String) -> Unit,


    ) : RecyclerView.ViewHolder(itemSubsBinding.root) {

    private var subreddit: RedditItem.RedditPost? = null

    val hiddenLayout = itemSubsBinding.hiddenCardView
    val cardView = itemSubsBinding.itemSubredditCard


    init {

        with(itemSubsBinding) {
            subscribeBtn.setOnClickListener {

                subreddit?.let { redditPost ->

                    //по нажатии меняем цвет иконки и фона текста названия саба
                    if (redditPost.isSubscribed) {
                        subscribeBtn.setImageResource(R.drawable.ic_vector_subscribe)
                        itemSubsTitle.text = redditPost.title?.decorateStringWithSpan(
                            color = itemSubsTitle.context.getString(R.string.reddit_color_unsubscribed),
                            view = itemSubsTitle
                        )
                    } else {
                        subscribeBtn.setImageResource(R.drawable.ic_subsubscribed)
                        itemSubsTitle.text = redditPost.title?.decorateStringWithSpan(
                            color = itemSubsTitle.context.getString(R.string.reddit_color_subscribed),
                            view = itemSubsTitle
                        )
                    }
                    onSubscribeBtnClicked(redditPost)
                }
            }

            openCommentsBtn.setOnClickListener {
                subreddit?.let(onOpenCommentsBtnClicked)
            }
            subAuthorTextView.setOnClickListener {
                subreddit?.author?.let(onAuthorNameClicked)
            }
        }
    }

    fun bind(redditPost: RedditItem.RedditPost) {

        val convertedWidth = 320f.convertDpToPixel()
        val convertedHeight = 180f.convertDpToPixel()
        subreddit = redditPost
        with(itemSubsBinding) {

            countOfCommentsTextView.text = redditPost.num_comments.toString()
            subAuthorTextView.text = redditPost.author
            subTextView.text = redditPost.subSelfText

            if (subreddit?.isSubscribed == true) {
                subscribeBtn.setImageResource(R.drawable.ic_subsubscribed)
                itemSubsTitle.text = redditPost.title?.decorateStringWithSpan(
                    color = itemSubsTitle.context.getString(R.string.reddit_color_subscribed),
                    view = itemSubsTitle
                )

            } else {
                subscribeBtn.setImageResource(R.drawable.ic_vector_subscribe)
                itemSubsTitle.text = redditPost.title?.decorateStringWithSpan(
                    color = itemSubsTitle.context.getString(R.string.reddit_color_unsubscribed),
                    view = itemSubsTitle
                )
            }


            if (((redditPost.url)
                    ?.contains(".jpg") == true || (redditPost.url)
                    ?.contains(".png") == true || (redditPost.url)
                    ?.contains(".gif") == true)
            ) {
                Glide.with(subImageView)
                    .load(redditPost.url)
                    .transform(CenterInside(), RoundedCorners(8))
                    .override(convertedWidth.toInt(), convertedHeight.toInt())
                    .into(subImageView)

                subImageView.adjustViewBounds = true
            } else {
                subTextView.text = redditPost.url +  System.getProperty("line.separator") +
                     System.getProperty("line.separator") + redditPost.subSelfText


                    //Resources.getSystem()
                   // .getString(
                   //     R.string.subreddit_text_concat, redditPost.url,
                   //     System.getProperty("line.separator"),
                   //     System.getProperty("line.separator"),
                   //     redditPost.subSelfText
                   // )


            }

        }

    }


}




