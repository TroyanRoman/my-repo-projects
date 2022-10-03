package com.skillbox.ascent.ui.fragments.sport_activities.add_activity

import android.content.res.ColorStateList
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityCollectData
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityType
import com.skillbox.ascent.databinding.FragmentCreateActivityBinding
import com.skillbox.ascent.di.networking.NetworkStatus
import com.skillbox.ascent.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@AndroidEntryPoint
class CreateActivityFragment : Fragment(R.layout.fragment_create_activity) {

    private val binding by viewBinding(FragmentCreateActivityBinding::bind)

    private val viewModel: CreateActivityViewModel by viewModels()

    private var elapsedTimeInSec: Int? = null

    private var startTime: String? = null

    private val args: CreateActivityFragmentArgs by navArgs()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("start_time", startTime)
        elapsedTimeInSec?.let { outState.putInt("elapsed_time", it) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            startTime = savedInstanceState.getString(START_TIME_KEY, "")
            elapsedTimeInSec = savedInstanceState.getInt(ELAPSED_TIME_KEY, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val validityTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                enableAddButton()
            }

            override fun afterTextChanged(p0: Editable?) {}

        }


        checkFieldsValidity(validityTextWatcher)
        setActivityTypeAdapter()
        datePicker()
        timePicker()
        handleOnBackPressed()
        manageNetworkConnection()
        setUpRecordedFields()






        viewModel.isLoading.observe(viewLifecycleOwner, ::loadProgress)
        viewModel.isSuccess.observe(viewLifecycleOwner) { uploadStatus ->
            if (uploadStatus.isDataUploaded != null)
                proceedSuccess(uploadStatus)
        }
        viewModel.wrongTimeSetData.observe(viewLifecycleOwner) { isIncorrectTime ->

            if (isIncorrectTime) {
                binding.timeinputLayout.boxStrokeColor =
                    resources.getColor(R.color.snackNegativeTextColor, null)
                binding.timeinputLayout.error = "Wrong time input"

                binding.timeinputLayout.setErrorTextColor(
                    ColorStateList.valueOf(resources.getColor(R.color.snackNegativeTextColor, null))
                )

            } else {

                binding.timeinputLayout.boxStrokeColor =
                    resources.getColor(R.color.accent_color_primary, null)
                binding.timeinputLayout.error = null
            }
        }

        binding.addActivityBtn.setOnClickListener {
            createActivity()
        }


    }

    private fun manageNetworkConnection() {
        viewModel.checkNetworkConnections()
        viewModel.isConnected.observe(viewLifecycleOwner) { networkStatus ->

            when (networkStatus) {
                is NetworkStatus.ConnectSuccess -> {
                    binding.connectedInfoContainer.visibility = View.VISIBLE
                    binding.noInternetTxt.visibility = View.GONE
                }
                is NetworkStatus.ConnectError -> {
                    binding.connectedInfoContainer.visibility = View.GONE
                    binding.noInternetTxt.visibility = View.VISIBLE
                }
            }


        }
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.cancelCheckNetworkJob()
                    findNavController().popBackStack()
                }

            })
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelCheckNetworkJob()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createActivity() {
        with(binding) {

            if (elapsedTimeInSec != null && startTime != null) {
                val activityModel = ActivityCollectData(
                    name = activityNameInput.text.toString(),
                    type = activityTypeInput.text.toString(),
                    desc = activityDescInput.text.toString(),
                    time = elapsedTimeInSec!!,
                    distance = activityDistanceInput.getDistanceFromEditText(),
                    startDate = activityDateInputLayout.getDateFromEditText(startTime ?: "")!!
                )
                viewModel.createActivity(activityModel)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun enableAddButton() {
        with(binding) {
            if (areFieldsFillIn()) {
                addActivityBtn.visibility = View.VISIBLE
                fillRationaleTxt.visibility = View.GONE
            } else {
                addActivityBtn.visibility = View.GONE
                fillRationaleTxt.visibility = View.VISIBLE
            }
        }
    }


    private fun areFieldsFillIn() =
        with(binding) {
            val nameInput = activityNameInput.text.toString()
            val typeInput = activityTypeInput.text.toString()
            val startDateInput = activityDateInput.text.toString()
            val time = timeInput.text.toString()
            val distance = activityDistanceInput.text.toString()

            nameInput.isNotEmpty() && typeInput.isNotEmpty() &&
                    startDateInput.isNotEmpty() &&
                    time.isNotEmpty() && distance.isNotEmpty()
        }


    private fun datePicker() {

        binding.activityDateInput.setOnClickListener {

            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(requireActivity().supportFragmentManager, DATE_PICKER_TAG)
            datePicker.addOnPositiveButtonClickListener {
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
            val startTimePicker = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                getTimePicker(R.string.timeStartActivity) else getTimePickerOld(R.string.timeStartActivity)

            startTimePicker.show(requireActivity().supportFragmentManager, TIME_START_PICKER_TAG)

            startTimePicker.addOnPositiveButtonClickListener {

                val endTimePicker = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    getTimePicker(R.string.timeEndActivity) else getTimePickerOld(R.string.timeEndActivity)

                val startHours = startTimePicker.hour
                val startMinutes = startTimePicker.minute

                startTime = "$startHours:$startMinutes"

                endTimePicker.show(requireActivity().supportFragmentManager, TIME_END_PICKER_TAG)
                endTimePicker.addOnPositiveButtonClickListener {
                    val hoursDiff = endTimePicker.hour - startHours
                    val minutesDiff = endTimePicker.minute - startMinutes

                    elapsedTimeInSec = viewModel.calculateElapsingTime(hoursDiff, minutesDiff)

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

    private fun getTimePickerOld(@StringRes titleRes: Int): MaterialTimePicker {
        return MaterialTimePicker.Builder()
            .setTitleText(titleRes)
            .setHour(12)
            .setMinute(0)
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
        binding.fillRationaleTxt.isVisible = !isLoading
        binding.progress.isVisible = isLoading
    }

    private fun proceedSuccess(isSuccess: MyBooleanData) {
        val action =
            CreateActivityFragmentDirections.actionCreateActivityFragmentToActivitiesFragment2(
                isSuccess
            )
        val animOptions = NavOptions.Builder().setAnimationTransit()
        findNavController().navigate(action, animOptions)
    }

    private fun setActivityTypeAdapter() {
        val typeStringsArray = ActivityType.values().map {
            it.nameType
        }.toTypedArray()

        val activityTypeAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu, typeStringsArray)
        binding.activityTypeInput.setAdapter(activityTypeAdapter)
    }

    private fun setUpRecordedFields() {
        if (args.recordData != null) {
            val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val date = dateFormatter.format(args.recordData?.startDate)
            binding.timeInput.setText(args.recordData?.time?.setTimeInputField())
            binding.activityDistanceInput.setText(args.recordData?.distance)
            binding.activityDateInput.setText(date)
            elapsedTimeInSec = args.recordData?.time?.getSecondsFromTimeString()
            startTime = args.recordData?.startTime
        }
    }


    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_START_PICKER_TAG = "TimePickerStart"
        private const val TIME_END_PICKER_TAG = "TimePickerEnd"
        private const val DATE_FORMAT = "yyyy-MM-dd"
        private const val START_TIME_KEY = "start_time"
        private const val ELAPSED_TIME_KEY = "elapsed_time"
    }
}