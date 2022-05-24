package com.skillbox.ascent.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.PagingData
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityEntity
import com.skillbox.ascent.ui.fragments.sport_activities.ActivityItemUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect {
            action(it)
        }
    }
}

suspend fun  Flow<PagingData<ActivityEntity>>.collectItems(stateItems: MutableStateFlow<ActivityItemUIState>) {
    this.collectLatest {
        stateItems.value = ActivityItemUIState.Success(it)
    }
}