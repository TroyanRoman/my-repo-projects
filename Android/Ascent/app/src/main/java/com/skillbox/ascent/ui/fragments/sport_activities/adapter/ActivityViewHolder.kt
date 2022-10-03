package com.skillbox.ascent.ui.fragments.sport_activities.adapter


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityUIEntity
import com.skillbox.ascent.databinding.ItemActivityBinding
import com.skillbox.ascent.utils.*

class ActivityViewHolder(
    private val itemActivityBinding: ItemActivityBinding,
) : RecyclerView.ViewHolder(itemActivityBinding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(UIEntity: ActivityUIEntity) {

        with(itemActivityBinding) {

            userName.text = UIEntity.userFullName
            distance.text = UIEntity.activity.distance .roundUpDistance()
            activityElapsedTime.text = UIEntity.activity.time.setActivityTimeString()
            elevation.text = UIEntity.activity.elevation?.setElevationTextString()
            activityStartTime.text =
                UIEntity.activity.startDate.getActivitiesDateString()

            activityName.text = UIEntity.activity.name
            activityType.text = UIEntity.setActivityTypeString()

            Glide.with(avatarImage)
                .load(UIEntity.avatarLink)
                .circleCrop()
                .placeholder(R.drawable.ic_load_avatar)
                .error(R.drawable.ic_no_avatar)
                .into(avatarImage)

            Glide.with(activityTypePic)
                .asDrawable()
                .load(UIEntity.setActivityTypeImage())
                .optionalCenterCrop()
                .into(activityTypePic)
        }
    }




}

