package com.skillbox.ascent.data.ascent.models


import java.util.*
//для сбора данных
data class ActivityModel (
    val distance : Float,
    val name : String,
    val type : String,
    val time : Int,
    val desc : String,
    val startDate: Date
        )