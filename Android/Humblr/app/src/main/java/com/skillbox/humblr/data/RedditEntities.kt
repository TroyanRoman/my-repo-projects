package com.skillbox.humblr.data

import android.os.Parcelable
import androidx.room.*
import com.skillbox.humblr.db.DataBaseContract
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

enum class PostsType {
    NEWS, TOP, SEARCH, USER_FAVORITES, REDDITOR_SUBS
}

//-------------------------------------------------------------------//

@Parcelize
@JsonClass(generateAdapter = true)
@Entity(
    tableName = DataBaseContract.RedditorContract.RedditorSub.TABLE_NAME
)
data class RedditorInfo(

    @PrimaryKey(autoGenerate = true)
    @Transient
    @ColumnInfo(name = DataBaseContract.RedditorContract.RedditorSub.Columns.ID)
    val id: Long = 0,

    @ColumnInfo(name = DataBaseContract.RedditorContract.RedditorSub.Columns.ID_SUBREDDIT)
    val subredditId: String? = "",

    @Json(name = "display_name_prefixed")
    @ColumnInfo(name = DataBaseContract.RedditorContract.RedditorSub.Columns.NAME_PREFIXED)
    val namePrefixed: String,

    @Json(name = "banner_img")
    @ColumnInfo(name = DataBaseContract.RedditorContract.RedditorSub.Columns.AVATAR_IMG)
    val avatarImage: String

) : Parcelable


//_______________________________________________//


@Parcelize
@JsonClass(generateAdapter = true)
data class FriendMade(
    val name: String,
    val note: String?
) : Parcelable

//-----------------//
@Parcelize
@JsonClass(generateAdapter = true)
data class User(
    val name: String,
    @Json(name = "icon_img")
    val avatarImage: String,
    val subreddit: UserSubreddit,

    ) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UserSubreddit(
    @Json(name = "display_name_prefixed")
    val displayName: String

) : Parcelable

//-------------------------//


@Parcelize
@JsonClass(generateAdapter = true)
data class UserFriends(
    val name: String
) : Parcelable


//-------RedditWrappers-For-Entities----////
@JsonClass(generateAdapter = true)
data class ItemResponseWrapper<T>(
    @Json(name = "data")
    val dataResponse: T
)

@JsonClass(generateAdapter = true)
data class ItemChildrenDataWrapper<D>(
    val children: List<D>,
    val after: String?,
    val before: String?
)

@JsonClass(generateAdapter = true)
data class RedditItemsWrapper<I>(
    val kind: String,
    @Json(name = "data")
    val redditItem: I
)
/*
data class Comment(
    val name: String,
    val author: String? = "anonymous",
    val createdAt: Long? = 0,
    val body: String? = "",
    val replies: CommentResponseWrapper?,
    var isSubscribed: Boolean? = null,
    var isExpandable: Boolean = false,
)

sealed class CommentResponseWrapper {
    data class CommentResponseObject(val dataResponse: CommentJsonAdapter.CommentChildrenDataWrapper) :
        CommentResponseWrapper()

    data class StringResponseObject(val dataResponse: String) : CommentResponseWrapper()
}

fun CommentJsonAdapter.RedditCommentRaw.mapFromNetworkRequest(): Comment {
    return Comment(
        name = this.name,
        author = this.author,
        createdAt = this.createdAt,
        body = this.body,
        isSubscribed = this.isSubscribed,
        isExpandable = this.isExpandable,
        replies = this.getRepliesFromRaw()
    )
}

private fun CommentJsonAdapter.RedditCommentRaw.getRepliesFromRaw(): CommentResponseWrapper? {
    return (repliesObj as? CommentResponseWrapper.CommentResponseObject)
        ?: (repliesString as? CommentResponseWrapper.StringResponseObject)
}
//--------------------------------------------//


 */
/*
@Parcelize
@JsonClass(generateAdapter = true)
data class CommentResponseWrapper(
    @Json(name = "data")
    val dataResponse: CommentChildrenDataWrapper
): Parcelable
@Parcelize
@JsonClass(generateAdapter = true)
data class CommentChildrenDataWrapper(
    val children: List<RedditCommentsWrapper>,
    val after: String?,
    val before: String?
): Parcelable
@Parcelize
@JsonClass(generateAdapter = true)
data class RedditCommentsWrapper(
    val kind: String,
    @Json(name = "data")
    val redditItem: RedditCommentRaw
): Parcelable


 */

