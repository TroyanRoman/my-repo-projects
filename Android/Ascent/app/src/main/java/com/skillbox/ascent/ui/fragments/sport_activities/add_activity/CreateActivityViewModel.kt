package com.skillbox.ascent.ui.fragments.sport_activities.add_activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityModel
import com.skillbox.ascent.data.ascent.repositories.activities.ActivitiesRepository
import com.skillbox.ascent.data.ascent.repositories.activities.time_processing_repo.TimeProcessing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class CreateActivityViewModel @Inject constructor(
    private val activityRepo: ActivitiesRepository,
    private val timeProcessingRep : TimeProcessing
) : ViewModel() {

    private val _isLoadingLiveData = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoadingLiveData

    private val _isSuccessLiveData = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean>
        get() = _isSuccessLiveData




    fun createActivity(
        activityModel: ActivityModel,
    ) {
        _isLoadingLiveData.postValue(true)
        viewModelScope.launch {
            try {
                activityRepo.createActivity(activityModel)
                _isLoadingLiveData.postValue(false)
                _isSuccessLiveData.postValue(true)
            } catch (t: Throwable) {
                _isLoadingLiveData.postValue(false)
                _isSuccessLiveData.postValue(false)
                t.printStackTrace()
            } finally {
                _isLoadingLiveData.postValue(false)
            }
        }
    }

    fun calculateElapsingTime(hoursDiff: Int, minutesDiff : Int) : Int {
      return  try {
           timeProcessingRep.calculateElapsingTime(hoursDiff, minutesDiff)
        }catch(e: IllegalArgumentException) {
            //toast об ошибке
            0
        }
    }
    fun setCreateTimeString(hoursDiff: Int, minutesDiff : Int) : String {
        return  try {
            timeProcessingRep.setCreateTimeString(hoursDiff, minutesDiff)
        }catch(e: IllegalArgumentException) {
            //toast об ошибке
            ""
        }
    }
}