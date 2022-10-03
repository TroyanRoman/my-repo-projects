package com.skillbox.ascent.utils

import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.navigation.NavOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityType
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityUIEntity
import com.skillbox.ascent.utils.Constants.HALF_MARATHON_DIST
import com.skillbox.ascent.utils.Constants.ISO_DATE_TIME_PATTERN
import com.skillbox.ascent.utils.Constants.MARATHON_DIST
import com.skillbox.ascent.utils.Constants.METERS_IN_KM
import com.skillbox.ascent.utils.Constants.SECONDS_IN_DAY
import com.skillbox.ascent.utils.Constants.TIMER_FORMAT
import com.skillbox.ascent.utils.Constants.TIME_UNITS
import com.skillbox.ascent.utils.Constants.WEEKEND_RIDING_DIST
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import kotlin.math.roundToInt


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


fun getCurrentDate(): Date = Calendar.getInstance().time


fun getCurrentTime(): String {
    val dateTime = System.currentTimeMillis()
    val dateTimeFormat = SimpleDateFormat(ISO_DATE_TIME_PATTERN, Locale.getDefault())
    val date = Date(dateTime)
    val dateTimeString = dateTimeFormat.format(date)
    Log.d("CurrentDateTime", "current date timeString = $dateTimeString")
    return dateTimeString.drop(11)
}

fun Double.getDistanceString(): String {
    return when {
        this < 1000 -> ((this.roundToInt() / 10) * 10).toString() + " m"
        else -> this.roundDoubleStringToKm()
    }
}


fun Double.roundDoubleStringToKm(): String {
    val formatter = DecimalFormat("#.##")
    formatter.roundingMode = RoundingMode.HALF_UP
    return formatter.format(this / 1000).replace(",", ".") + " km"
}

fun String.setTimeInputField(): String {
    val hoursString = this.dropLast(6)
    val minutesString = this.drop(3).dropLast(3)
    return when {
        hoursString == "00" && minutesString == "00" -> "Less than 1 min"
        hoursString == "00" -> "$minutesString min"
        else -> "$hoursString h $minutesString min"
    }
}


fun String.getSecondsFromTimeString(): Int {
    // 12:34:56

    val hoursString = this.dropLast(6)
    val secondsString = this.drop(6)
    val minutesString = this.drop(3).dropLast(3)

    val elapsedTimeInSec =
        hoursString.toInt() * 3600 + minutesString.toInt() * 60 + secondsString.toInt()

    Log.d("TimeSeconds", " elapsed secs = $elapsedTimeInSec")
    return elapsedTimeInSec
}


fun makeTimeString(hours: Int, mins: Int, secs: Int): String = String
    .format(TIMER_FORMAT, hours, mins, secs)

fun TextInputLayout.getDateFromEditText(startTimeString: String): Date? {
    val stringFromEditText = this.editText?.text?.toString() + " " + startTimeString
    return SimpleDateFormat(ISO_DATE_TIME_PATTERN, Locale.getDefault()).parse(stringFromEditText)
}

fun TextInputEditText.getDistanceFromEditText(): Float = this.text.toString().toFloat() * 1000

fun Date.getActivitiesDateString(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //Находим таймстэмп начала дня в секундах.вычитаем таймстемп события(начало активности, в данном случае)
        //Если разница меньше 0 значит события произошло сегодня, если больше 0 но меньше одного дня - вчера, в
        //остальных случаях показываем дату и время

        val dateLongInSec = this.toInstant().epochSecond
        val localDate = LocalDate.now()
        val startOfDay = localDate.atStartOfDay(ZoneId.systemDefault())
        val startOfDayTimeStamp = startOfDay.toInstant().epochSecond
        val diff = startOfDayTimeStamp - dateLongInSec
        val timeActivityStarted =
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(this)

        when {
            diff <= 0 -> "Today at " + timeActivityStarted.dropWhile { it != ' ' }
            diff in 1 until SECONDS_IN_DAY -> "Yesterday at " + timeActivityStarted.dropWhile { it != ' ' }
            else -> timeActivityStarted.replace(" ", " at ")
        }

    } else {
        this.toString().replaceRange(16, 29, "")
    }
}

fun Float.setElevationTextString(): String = "$this meters "

fun Float.roundUpDistance(): String = "%.2f".format(this / METERS_IN_KM) + " km"

fun Int.setActivityTimeString(): String {
    val timeInMin = this / TIME_UNITS
    val timeInHours = timeInMin / TIME_UNITS
    return when {
        timeInHours >= 1 -> "$timeInHours h ${timeInMin % TIME_UNITS} min "
        timeInHours < 1 -> "${timeInMin % TIME_UNITS} min"
        else -> ""
    }
}

fun ActivityUIEntity.setActivityTypeImage(): Int {
    return when (this.activity.type) {
        ActivityType.RUN.nameType -> R.drawable.runners
        ActivityType.RIDE.nameType -> R.drawable.ridingfree
        ActivityType.HIKE.nameType -> R.drawable.hiking
        ActivityType.SWIM.nameType -> R.drawable.swim
        ActivityType.WALK.nameType -> R.drawable.walk
        ActivityType.WHEELCHAIR.nameType -> R.drawable.wheelchair
        else -> 0
    }
}

fun ActivityUIEntity.setActivityTypeString(): String {
    val activityType = this.activity.type
    val distance = this.activity.distance

    return when {
        activityType == ActivityType.RUN.nameType && distance >= HALF_MARATHON_DIST
                && distance < MARATHON_DIST -> "Long distance running"
        activityType == ActivityType.RUN.nameType && distance < HALF_MARATHON_DIST -> "Running training"
        activityType == ActivityType.RUN.nameType && distance >= MARATHON_DIST -> "Marathon distance running"
        activityType == ActivityType.RIDE.nameType && distance <= WEEKEND_RIDING_DIST -> "Riding training"
        activityType == ActivityType.RIDE.nameType && distance > WEEKEND_RIDING_DIST -> "Weekend riding :)"
        activityType == ActivityType.HIKE.nameType -> "Hiking"
        activityType == ActivityType.SWIM.nameType -> "Swimming"
        activityType == ActivityType.WALK.nameType -> "Walking"
        activityType == ActivityType.WHEELCHAIR.nameType -> "Chair makes wroom-wroom"

        else -> "Some activity, whatever"
    }
}

fun NavOptions.Builder.setAnimationTransit(): NavOptions = this
    .setLaunchSingleTop(true)
    .setEnterAnim(R.anim.enter_anim)
    .setExitAnim(R.anim.exit_anim)
    .setPopEnterAnim(R.anim.pop_enter_anim)
    .setPopExitAnim(R.anim.pop_exit_anim)
    .build()





