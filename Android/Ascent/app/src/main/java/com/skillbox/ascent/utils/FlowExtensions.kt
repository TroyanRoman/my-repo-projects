package com.skillbox.ascent.utils

import androidx.lifecycle.*
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityUIEntity
import com.skillbox.ascent.ui.fragments.sport_activities.ActivityItemUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

fun <A, B> LiveData<A>.zipWith(stream: LiveData<B>): LiveData<Pair<A, B>> {
    val result = MediatorLiveData<Pair<A, B>>()
    result.addSource(this) { a ->
        if (a != null && stream.value != null) {
            result.value = Pair(a, stream.value!!)
        }
    }
    result.addSource(stream) { b ->
        if (b != null && this.value != null) {
            result.value = Pair(this.value!!, b)
        }
    }
    return result
}

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

suspend fun Flow<PagingData<ActivityUIEntity>>.collectItems(stateItems: MutableStateFlow<ActivityItemUIState>) {
    this.collectLatest {
        stateItems.value = ActivityItemUIState.Success(it)
    }
}


