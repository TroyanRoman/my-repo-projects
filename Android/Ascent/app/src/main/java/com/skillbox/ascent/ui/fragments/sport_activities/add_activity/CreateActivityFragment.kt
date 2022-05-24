package com.skillbox.ascent.ui.fragments.sport_activities.add_activity

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityModel
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityType
import com.skillbox.ascent.databinding.FragmentCreateActivityBinding
import com.skillbox.ascent.utils.autoCleared
import com.skillbox.ascent.utils.getDateFromEditText
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IllegalArgumentException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

import java.util.*

@AndroidEntryPoint
class CreateActivityFragment : Fragment(R.layout.fragment_create_activity) {

    private val binding by viewBinding(FragmentCreateActivityBinding::bind)

    private val viewModel: CreateActivityViewModel by viewModels()

    private var elapsedTimeInSec: Int by autoCleared()

    private var startTime: String by autoCleared()

    private val validityTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            enableAddButton()
        }

        override fun afterTextChanged(p0: Editable?) {}

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActivityTypeAdapter()

        viewModel.isLoading.observe(viewLifecycleOwner, ::loadProgress)
        viewModel.isSuccess.observe(viewLifecycleOwner, ::proceedSuccess)

        binding.addActivityBtn.setOnClickListener {
            checkFieldsValidity(validityTextWatcher)
            createActivity()
        }

        datePicker()
        timePicker()

    }

    private fun createActivity() {
        with(binding) {
            val activityModel = ActivityModel(
                name = activityNameInput.text.toString(),
                type = activityTypeInput.text.toString(),
                desc = activityDescInput.text.toString(),
                startDate = activityDateInputLayout.getDateFromEditText(startTime)!!,
                time = elapsedTimeInSec,
                distance = activityDistanceInput.text.toString().toFloat()
            )
            Log.d("CreateActivityLog", "elapsed time = $elapsedTimeInSec sec")
            viewModel.createActivity(activityModel)
        }
    }

    private fun enableAddButton() {
        with(binding) {
            val nameInput = activityNameInput.text.toString()
            val typeInput = activityTypeInput.text.toString()
            val startDateInput = activityDateInput.text.toString()
            val time = timeInput.text.toString()
            val distance = activityDistanceInput.text.toString()




            addActivityBtn.isVisible =
                (nameInput.isNotEmpty() && typeInput.isNotEmpty() &&
                        startDateInput.isNotEmpty() &&
                        time.isNotEmpty() && distance.isNotEmpty())


        }
    }

    private fun datePicker() {

        binding.activityDateInput.setOnClickListener {

            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(requireActivity().supportFragmentManager, DATE_PICKER_TAG)
            datePicker.addOnPositiveButtonClickListener {
                // formatting date in yyyy.MM.dd format.
                val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
                val date = dateFormatter.format(Date(it))
                binding.activityDateInput.setText(date)
            }
            datePicker.addOnCancelListener {
                datePicker.dismiss()
            }
            datePicker.addOnNegativeButtonClickListener {
                datePicker.dismiss()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timePicker() {
        binding.timeInput.setOnClickListener {
            val startTimePicker = getTimePicker(R.string.timeStartActivity)
            startTimePicker.show(requireActivity().supportFragmentManager, TIME_START_PICKER_TAG)


            startTimePicker.addOnPositiveButtonClickListener {

                val endTimePicker = getTimePicker(R.string.timeEndActivity)
                val startHours = startTimePicker.hour
                val startMinutes = startTimePicker.minute

                startTime = "$startHours:$startMinutes"

                endTimePicker.show(requireActivity().supportFragmentManager, TIME_END_PICKER_TAG)
                endTimePicker.addOnPositiveButtonClickListener {
                    val hoursDiff = endTimePicker.hour - startHours
                    val minutesDiff = endTimePicker.minute - startMinutes
                    Log.d("CreateActivityLog", "hoursDiff = $hoursDiff minDiff = $minutesDiff   timePickerMethod")
                    try {
                       // elapsedTimeInSec = calculateElapsedTime(hoursDiff, minutesDiff)
                           elapsedTimeInSec  = viewModel.calculateElapsingTime(hoursDiff, minutesDiff)
                        Log.d("CreateActivityLog", "elapsed time = $elapsedTimeInSec sec")
                    } catch(e: IllegalArgumentException) {
                        Log.d("CreateActivityLog", "calculate error =   $e")
                        e.printStackTrace()
                    }
                   // binding.timeInput
                    //    .setText(setElapsedTimeField(hoursDiff, minutesDiff))
                    binding.timeInput.setText(viewModel.setCreateTimeString(hoursDiff, minutesDiff))
                }
            }
            startTimePicker.addOnCancelListener {
                startTimePicker.dismiss()
            }
            startTimePicker.addOnNegativeButtonClickListener {
                startTimePicker.dismiss()
            }


        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTimePicker(@StringRes titleRes: Int): MaterialTimePicker {
        val localDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
        return MaterialTimePicker.Builder()
            .setTitleText(titleRes)
            .setHour(localDateTime.hour)
            .setMinute(localDateTime.minute)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setInputMode(INPUT_MODE_CLOCK)
            .build()
    }


    private fun checkFieldsValidity(watcher: TextWatcher) {
        with(binding) {
            val viewsArray = arrayListOf(
                activityTypeInput,
                activityDateInput,
                activityDistanceInput,
                activityNameInput,
                timeInput
            )
            viewsArray.forEach { view ->
                view.addTextChangedListener(watcher)
            }
        }

    }

    private fun loadProgress(isLoading: Boolean) {
        binding.addActivityBtn.isVisible = !isLoading
        binding.progress.isVisible = isLoading
    }

    private fun proceedSuccess(isSuccess: Boolean) {
        val action =
            CreateActivityFragmentDirections.actionCreateActivityFragmentToActivitiesFragment2()
        if (isSuccess) findNavController().navigate(action)
    }

    private fun setActivityTypeAdapter() {
        val typeStringsArray = ActivityType.values().map {
            it.nameType
        }.toTypedArray()

        val activityTypeAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu, typeStringsArray)
        binding.activityTypeInput.setAdapter(activityTypeAdapter)
    }

    companion object {
        const val DATE_PICKER_TAG = "DatePicker"
        const val TIME_START_PICKER_TAG = "TimePickerStart"
        const val TIME_END_PICKER_TAG = "TimePickerEnd"
        const val DATE_FORMAT = "yyyy.MM.dd"
    }
}