package com.skillbox.ascent.data.ascent.models.sport_activity


import java.util.*
//для сбора данных из эдиттекстов
data class ActivityCollectData (
    val distance : Float,
    val name : String,
    val type : String,
    val time : Int,
    val desc : String,
    val startDate: Date
        )