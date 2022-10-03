package com.skillbox.ascent.ui.fragments.sport_activities.adapter

import androidx.recyclerview.widget.DiffUtil
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityUIEntity

object ActivitiesComparator : DiffUtil.ItemCallback<ActivityUIEntity>() {
    override fun areItemsTheSame(oldItem: ActivityUIEntity, newItem: ActivityUIEntity): Boolean {
        return oldItem.activity.id == newItem.activity.id
    }

    override fun areContentsTheSame(oldItem: ActivityUIEntity, newItem: ActivityUIEntity): Boolean {
        return oldItem == newItem
    }
}