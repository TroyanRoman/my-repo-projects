package com.skillbox.ascent.ui.fragments.sport_activities.adapter


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityEntity
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityType
import com.skillbox.ascent.databinding.ItemActivityBinding
import com.skillbox.ascent.utils.getDateString
import com.skillbox.ascent.utils.setActivityTimeString


class ActivityViewHolder(
    private val itemActivityBinding: ItemActivityBinding
) : RecyclerView.ViewHolder(itemActivityBinding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(entity: ActivityEntity) {

        Log.d("ActivityLog", "entity = $entity")
        with(itemActivityBinding) {
            userName.text = entity.userFullName
            distance.text = entity.activity.distance.toString() + " km "
            activityElapsedTime.text = entity.activity.time.setActivityTimeString()
            elevation.text = entity.activity.elevation.toString() + " meters "
            activityStartTime.text = entity.activity.startDate.getDateString()
          //  activityStartTime.text = entity.activity.startDate.toString()
            activityName.text = entity.activity.name
            activityType.text = setActivityTypeText(entity)
            Glide.with(avatarImage)
                .load(entity.avatarLink)
                .circleCrop()
                .placeholder(R.drawable.ic_load_avatar)
                .error(R.drawable.ic_no_avatar)
                .into(avatarImage)

            Glide.with(activityTypePic)
                .load(setActivityTypeImage(entity))
                .centerCrop()
                .into(activityTypePic)
        }
    }

    private fun setActivityTypeText(entity: ActivityEntity): String {
        val activityType = entity.activity.type
        val distance = entity.activity.distance

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

    private fun setActivityTypeImage(entity: ActivityEntity) : String {
        return when (entity.activity.type){
            ActivityType.RUN.nameType -> "https://www.energy.de/sites/default/files/National/PIX_running_970.jpg"
            ActivityType.RIDE.nameType -> "https://th.bing.com/th/id/R.1569a9bd25c129dbfea022199d9cf15d?rik=wyGrs7jOdLyKlQ&pid=ImgRaw&r=0"
            ActivityType.HIKE.nameType -> "https://winnersdrinkmilk.com/wp-content/uploads/2013/07/hiking-pic-300x2251.jpg"
            ActivityType.SWIM.nameType -> "https://terfit.ru/upload/iblock/dde/dde6b759723be5e7a3fe40e0270dc386.jpg"
            ActivityType.WALK.nameType -> "https://static.wixstatic.com/media/db5837_cc40c700f0a24171850a2e0d28c857ac~mv2.jpg/v1/fill/w_800,h_534,al_c,q_90/db5837_cc40c700f0a24171850a2e0d28c857ac~mv2.jpg"
            ActivityType.WHEELCHAIR.nameType -> "https://s-media-cache-ak0.pinimg.com/736x/09/4f/d4/094fd4c69e3c0a4a9f888ec9adc4048c.jpg"
            else ->""
        }
    }

    companion object {
        const val HALF_MARATHON_DIST = 21.0
        const val MARATHON_DIST = 42.0
        const val WEEKEND_RIDING_DIST = 30.0
    }
}