package com.skillbox.ascent.data.ascent.api

import com.skillbox.ascent.data.ascent.models.AscentActivity
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*

interface AscentActivityApi {

    @GET("athlete/activities")
    suspend fun getActivitiesByUser(
        @Query("before") before: Int? = null,
        @Query("after") after: Int? = null,
        @Query("page") pageNumber: Int,
        @Query("per_page") perPage: Int = 30
    ): List<AscentActivity>

    @POST("activities")
    suspend fun createActivity(
        @Query("name") name: String,
        @Query("type") type: String,
        @Query("start_date_local") startDate: Date,
        @Query("elapsed_time") elapsedTime: Int,
        @Query("description") description: String? = "",
        @Query("distance") distance: Float,
        @Query("trainer") trainer: Int = 1,
        @Query("commute") commute: Int = 1,
        @Query("hide_from_home") hide: Boolean = true
    )
}