package com.skillbox.ascent.ui.fragments.sport_activities.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityEntity
import com.skillbox.ascent.databinding.ItemActivityBinding
import com.skillbox.ascent.ui.fragments.sport_activities.add_activity.ActivitiesComparator

class ActivitiesAdapter :
    PagingDataAdapter<ActivityEntity, ActivityViewHolder>(ActivitiesComparator) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        getItem(position)?.let {
            Log.d("ActivityLog", "activity entity = $it")
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