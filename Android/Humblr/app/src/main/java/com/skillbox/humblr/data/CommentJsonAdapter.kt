package com.skillbox.humblr.data

import android.os.Parcelable
import com.skillbox.humblr.di.CommentsWithReplies
import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import kotlinx.parcelize.Parcelize
import timber.log.Timber


class CommentJsonAdapter {


    @CommentsWithReplies
    @FromJson
    fun fromJson(
        response: List<CommentResponseWrapper>
    ): List<RedditItem.RedditComment> {


        val listOfComments = mutableListOf<RedditItem.RedditComment>()

       // Timber.tag("CommentAdapter").d("response object = $response")
      //  Timber.tag("CommentAdapter").d("data response object response = ${response.last().dataResponse}")
      //  Timber.tag("CommentAdapter").d("children data response object response = ${response.last().dataResponse.children}")

        val rawCommentList = response.last().dataResponse.children.map { redditCommentsWrapper ->
            redditCommentsWrapper.redditItem
        }

      //  Timber.tag("CommentAdapter").d("comment list = $rawCommentList")
        rawCommentList.forEach { rawComment ->
            Timber.tag("CommentAdapter").d("object replies = ${rawComment.repliesObj}")
            Timber.tag("CommentAdapter").d("object replies is Object = ${rawComment.repliesObj is CommentResponseWrapper}")
            Timber.tag("CommentAdapter").d("object replies is String = ${rawComment.repliesObj is String}")
            Timber.tag("CommentAdapter").d("object class is = ${rawComment.repliesObj?.javaClass}")
            if (rawComment.repliesObj is String) {


                Timber.tag("CommentAdapter").d("no replies")
                val comment = RedditItem.RedditComment(
                    name = rawComment.name,
                    author = rawComment.author,
                    body = rawComment.body,
                    createdAt = rawComment.createdAt,
                    isExpandable = rawComment.isExpandable,
                    isSubscribed = rawComment.isSubscribed,
                     replies = emptyList()
                )
                listOfComments.add(comment)
            } else
                    //(rawComment.repliesObj is ReplyType.StringData && rawComment.repliesObj.replies == "")
            {
                Timber.tag("CommentAdapter").d("with replies")
                val comment = RedditItem.RedditComment(
                    name = rawComment.name,
                    author = rawComment.author,
                    body = rawComment.body,
                    createdAt = rawComment.createdAt,
                    isExpandable = rawComment.isExpandable,
                    isSubscribed = rawComment.isSubscribed,
                    replies = repliesFromJson(listOf(rawComment.repliesObj))
                )
                listOfComments.add(comment)


            }


        }



        return listOfComments
    }
    @FromJson
    fun repliesFromJson(
        replieResponse: Any?
    ) : List<RedditItem.RedditComment>  {

        Timber.tag("CommentAdapter").d("repliesFromJsonPushed with replie response = $replieResponse")

        val listOfComments = mutableListOf<RedditItem.RedditComment>()
        Timber.tag("CommentAdapter").d("repliesFromJsonPushed with replie response = ${replieResponse.toString()}")

        if (replieResponse is String) {


            Timber.tag("CommentAdapter").d("replie response is String = $replieResponse")
            return emptyList()
        }  else  {


            Timber.tag("CommentAdapter").d("replie response is CommentResponseWraspper = $replieResponse")



           // val repliesListRaw = replieResponse.dataResponse.children.map { redditCommentsWrapper ->
            //    redditCommentsWrapper.redditItem
           // }
            return emptyList()
        }
      //  return listOfComments

    }


    @ToJson
    fun toJson(@CommentsWithReplies replies: List<RedditItem.RedditComment>): String {
        throw UnsupportedOperationException()
    }


    //---------------------------------------------------//

    sealed class ReplyType {
        @JsonClass(generateAdapter = true)
        data class StringData(val replies: String) : ReplyType()

        @JsonClass(generateAdapter = true)
        data class ObjReply(val replies: CommentResponseWrapper) : ReplyType()
    }

    // @Parcelize
    @JsonClass(generateAdapter = true)
    data class CommentResponseWrapperList(
        val datas: List<CommentResponseWrapper>
    )
   // : Parcelable

   // @Parcelize
    @JsonClass(generateAdapter = true)
    data class CommentResponseWrapper(
        @Json(name = "data")
        val dataResponse: CommentChildrenDataWrapper
    )
   // : Parcelable

  //   @Parcelize
    @JsonClass(generateAdapter = true)
    data class CommentChildrenDataWrapper(
        val children: List<RedditCommentsWrapper>,
        val after: String?,
        val before: String?
    )
      //: Parcelable

      //@Parcelize
    @JsonClass(generateAdapter = true)
    data class RedditCommentsWrapper(
        val kind: String,
        @Json(name = "data")
        val redditItem: RedditItem.RedditCommentRaw
    )
          //: Parcelable

    ///---------------------------/////


}

