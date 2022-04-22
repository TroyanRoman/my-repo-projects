package com.skillbox.ascent.ui.fragments.sport_activities.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.skillbox.ascent.data.ascent.models.ActivityEntity
import com.skillbox.ascent.databinding.ItemActivityBinding
import com.skillbox.ascent.ui.fragments.sport_activities.add_activity.ActivitiesComparator

class ActivitiesAdapter :
    PagingDataAdapter<ActivityEntity, ActivityViewHolder>(ActivitiesComparator) {
    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActivityViewHolder {
        val itemActivityBinding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context))
        return ActivityViewHolder(itemActivityBinding)
    }

}