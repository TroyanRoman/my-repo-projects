package com.skillbox.ascent.ui.fragments.sport_activities

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.skillbox.ascent.data.ascent.models.ActivityEntity
import com.skillbox.ascent.data.ascent.models.AscentActivity
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.data.ascent.repositories.activities.ActivitiesRepository
import com.skillbox.ascent.utils.collectItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val activitiesRepo : ActivitiesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        if (!savedStateHandle.contains(KEY_ACTIVITIES)) {
            savedStateHandle.set(KEY_ACTIVITIES, DEFAULT_ASCENT)
        }
    }

    private var _ascentJob = MutableLiveData<Job?>()
    private val ascentJob: LiveData<Job?>
        get() = _ascentJob

    private val _activityUIState: MutableStateFlow<ActivityItemUIState> = MutableStateFlow(ActivityItemUIState.Success(
        PagingData.empty()))
    val activityUIState: MutableStateFlow<ActivityItemUIState>
        get() = _activityUIState

    private val clearListChannel = Channel<Unit>(Channel.CONFLATED)

    @OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
    private  val activitiesFlow = flowOf(
        clearListChannel.receiveAsFlow().map { PagingData.empty<ActivityEntity>() },
        savedStateHandle.getLiveData<String>(KEY_ACTIVITIES)
            .asFlow()
            .flatMapLatest {
                activitiesRepo.getActivitiesList(pageSize = DEFAULT_PAGE_SIZE)
            }
            .cachedIn(viewModelScope)
            .catch {
                Log.d("GetActivity","get activities error in vm  ") }
    ).flattenMerge(DEFAULT_CONCURRENCY)

    init {
        _ascentJob.value = viewModelScope.launch {
            activitiesFlow.collectItems(_activityUIState)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ascentJob.value?.cancel()
    }

    companion object {
        private const val KEY_ACTIVITIES = "key_activities"
        private const val DEFAULT_ASCENT = "androiddev"
        private const val DEFAULT_PAGE_SIZE = 20
    }

}