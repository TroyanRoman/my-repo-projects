package com.skillbox.ascent.ui.fragments.sport_activities.add_activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.ActivityModel
import com.skillbox.ascent.data.ascent.models.ActivityType
import com.skillbox.ascent.databinding.FragmentCreateActivityBinding
import com.skillbox.ascent.utils.getDateFromEditText
import dagger.hilt.android.AndroidEntryPoint

import java.util.*
@AndroidEntryPoint
class CreateActivityFragment : Fragment(R.layout.fragment_create_activity) {

    private val binding by viewBinding(FragmentCreateActivityBinding::bind)

    private val viewModel: CreateActivityViewModel by viewModels()

    private val validityTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            enableAddButton()
        }

        override fun afterTextChanged(p0: Editable?) {}

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActivityTypeAdapter()

        viewModel.isLoading.observe(viewLifecycleOwner, ::loadProgress)
        viewModel.isSuccess.observe(viewLifecycleOwner, ::proceedSuccess)

        binding.addActivityBtn.setOnClickListener {
            checkFieldsValidity(validityTextWatcher)
            createActivity()
        }

    }

    private fun createActivity() {
        with(binding) {
            val activityModel = ActivityModel(
                name = activityNameInput.text.toString(),
                type = activityTypeInput.text.toString(),
                desc = activityDescInput.text.toString(),
                startDate = getDateFromEditText(activityDateInputLayout)!!,
                time = timeInput.text.toString().toInt(),
                distance = activityDistanceInput.text.toString().toFloat()
            )

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




            addActivityBtn.isEnabled =
                (nameInput.isNotEmpty() && typeInput.isNotEmpty() &&
                        startDateInput.isNotEmpty() &&
                        time.isNotEmpty() && distance.isNotEmpty())



        }
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

    private fun loadProgress(isLoading : Boolean) {
        binding.addActivityBtn.isVisible = !isLoading
        binding.progress.isVisible = isLoading
    }
    private fun proceedSuccess(isSuccess: Boolean) {
        val action = CreateActivityFragmentDirections.actionCreateActivityFragmentToActivitiesFragment2()
        if(isSuccess) findNavController().navigate(action)
    }

    private fun setActivityTypeAdapter() {
        val typeStringsArray = ActivityType.values().map {
            it.nameType
        }.toTypedArray()

        val activityTypeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu, typeStringsArray)
        binding.activityTypeInput.setAdapter(activityTypeAdapter)
    }
}