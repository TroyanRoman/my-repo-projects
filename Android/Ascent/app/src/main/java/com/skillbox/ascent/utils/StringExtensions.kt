package com.skillbox.ascent.utils

import android.util.Log

fun String.translit(): String {

    return lowercase()
        .replace("а", "a")
        .replace("б", "b")
        .replace("в", "v")
        .replace("г", "g")
        .replace("д", "d")
        .replace("е", "e")
        .replace("ё", "yo")
        .replace("ж", "zh")
        .replace("з", "z")
        .replace("и", "i")
        .replace("й", "i")
        .replace("к", "k")
        .replace("л", "l")
        .replace("м", "m")
        .replace("н", "n")
        .replace("о", "o")
        .replace("п", "p")
        .replace("р", "r")
        .replace("с", "s")
        .replace("т", "t")
        .replace("у", "u")
        .replace("ф", "f")
        .replace("х", "kh")
        .replace("ц", "ts")
        .replace("ч", "ch")
        .replace("ш", "sh")
        .replace("щ", "sh")
        .replace("ъ", "")
        .replace("ы", "y")
        .replace("ь", "")
        .replace("э", "e")
        .replace("ю", "u")
        .replace("я", "ya")
}


 fun String.getWeightFromSpinner() = this.dropLast(3).toFloat()


