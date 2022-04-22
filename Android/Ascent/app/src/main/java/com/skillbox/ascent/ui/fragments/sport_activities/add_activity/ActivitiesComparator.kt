package com.skillbox.ascent.ui.fragments.sport_activities.add_activity

import androidx.recyclerview.widget.DiffUtil
import com.skillbox.ascent.data.ascent.models.ActivityEntity
import com.skillbox.ascent.data.ascent.models.AscentActivity

object ActivitiesComparator : DiffUtil.ItemCallback<ActivityEntity>() {
    override fun areItemsTheSame(oldItem: ActivityEntity, newItem: ActivityEntity): Boolean {
        return oldItem.activity.id == newItem.activity.id
    }

    override fun areContentsTheSame(oldItem: ActivityEntity, newItem: ActivityEntity): Boolean {
        return oldItem == newItem
    }
}