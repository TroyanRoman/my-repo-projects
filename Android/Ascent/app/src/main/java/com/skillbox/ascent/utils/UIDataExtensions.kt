package com.skillbox.ascent.utils

import android.icu.util.GregorianCalendar
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*


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

fun Int.setActivityTimeString(): String {
    val timeInMin = this / TIME_UNITS
    val timeInHours = timeInMin / TIME_UNITS
    return when {
        timeInHours >= 1 -> "$timeInHours h ${timeInMin % TIME_UNITS} min "
        timeInHours < 1 -> "${timeInMin % TIME_UNITS} min"
        else -> ""
    }
}

//Находим таймстэмп начала дня в секундах.вычитаем таймстемп события(начало активности, в данном случае)
//Если разница меньше 0 значит события произошло сегодня, если больше 0 но меньше одного дня - вчера, в
//остальных случаях показываем дату и время


//t------------->|-----YESTERDAY-----_SECONDS_IN_DAY_-------------|-------------TODAY-------------->
//-------|startOfDayYesterday|-----|dateLongInSec2|------|startOfDayToday|------|dateLongInSec1|--->
@RequiresApi(Build.VERSION_CODES.O)
fun Date.getDateString(): String {

    val dateLongInSec = this.toInstant().epochSecond


    val localDate = LocalDate.now()
    val startOfDay = localDate.atStartOfDay(ZoneId.systemDefault())
    val startOfDayTimeStamp = startOfDay.toInstant().epochSecond


    val diff = startOfDayTimeStamp - dateLongInSec
    val timeActivityStarted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(this)

    return when {
        diff <= 0 -> "Today at " + timeActivityStarted.dropWhile { it != ' ' }
        diff in 1 until SECONDS_IN_DAY -> "Yesterday at " + timeActivityStarted.dropWhile { it != ' ' }
        else -> timeActivityStarted.replace(" ", " at ")
    }

}

fun TextInputLayout.getDateFromEditText(startTimeString: String): Date? {
    val stringFromEditText = this.editText?.text?.toString() + " " + startTimeString
    return SimpleDateFormat(ISO_DATE_PATTERN, Locale.getDefault()).parse(stringFromEditText)
}

private const val TIME_UNITS = 60
private const val SECONDS_IN_DAY = 86400
private const val MILLIS_IN_SEC = 1000
private const val ISO_DATE_PATTERN = "yyyy.MM.dd HH:mm"

