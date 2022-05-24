package com.skillbox.ascent.data.ascent.repositories.activities.time_processing_repo

interface TimeProcessing {
    fun calculateElapsingTime(hoursDiff: Int, minutesDiff: Int) : Int
    fun setCreateTimeString(hoursDiff: Int, minutesDiff: Int) : String
}