package com.skillbox.ascent.ui.fragments.sport_activities.start_activity

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.sport_activity.ActivityRecordData
import com.skillbox.ascent.data.ascent.models.sport_activity.TrackerState
import com.skillbox.ascent.databinding.FragmentStartActivityBinding
import com.skillbox.ascent.tracker_service.TrackerService
import com.skillbox.ascent.utils.*
import com.skillbox.ascent.utils.Constants.ACTION_PAUSE_SERVICE
import com.skillbox.ascent.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.skillbox.ascent.utils.Constants.ACTION_STOP_SERVICE
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.LocationPermission
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructLocationPermissionRequest

@AndroidEntryPoint
class StartActivityFragment : Fragment(R.layout.fragment_start_activity) {


    private val viewModel : StartActivityViewModel by viewModels()
    private var recData: ActivityRecordData? = null
    private var _binding: FragmentStartActivityBinding? = null
    private val binding get() = _binding!!
    private var isTimerRunning = false
    private var curDistance = 0.0

    private var permissionRequester: PermissionsRequester by autoCleared()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartActivityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        permissionRequester = constructLocationPermissionRequest(
            permissions = arrayOf(LocationPermission.FINE, LocationPermission.COARSE),
            onShowRationale = ::showRationaleOnLocationPermissions,
            onNeverAskAgain = ::onLocationsNeverAskAgain,
            onPermissionDenied = ::onLocationsPermissionsDenied,
            requiresPermission = ::showTimerStartActivity
        )

        onPermissionResult()

        binding.allowLocations.setOnClickListener {
            onPermissionResult()
        }

        binding.actionBtn.setOnClickListener {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
        setObservers()
    }


    private fun showRationaleOnLocationPermissions(request: PermissionRequest) {
        this.showRationaleDialog(
            ratio = R.string.permission_detect_locations_ratio,
            request = request
        )
    }

    private fun onPermissionResult() {
        Handler(Looper.getMainLooper()).post {
            permissionRequester.launch()
        }
    }

    private fun onLocationsPermissionsDenied() {
        with(binding) {
            timeChronometer.text = resources.getText(R.string.permission_detect_locations_denied)
            actionBtn.visibility = View.GONE
            distanceMeter.visibility = View.GONE
            distanceDesc.visibility = View.GONE
            timeDesc.visibility = View.GONE
            allowLocations.visibility = View.VISIBLE

        }
    }

    private fun onLocationsNeverAskAgain() {
        with(binding) {
            timeChronometer.text =
                resources.getText(R.string.permission_detect_locations_denied_forevr)
            actionBtn.visibility = View.GONE
            distanceMeter.visibility = View.GONE
            distanceDesc.visibility = View.GONE
            timeDesc.visibility = View.GONE
            allowLocations.visibility = View.GONE
        }
    }

    private fun showTimerStartActivity() {
        with(binding) {
            actionBtn.visibility = View.VISIBLE
            actionBtn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            distanceMeter.visibility = View.VISIBLE
            distanceDesc.visibility = View.VISIBLE
            timeDesc.visibility = View.VISIBLE
            allowLocations.visibility = View.GONE
        }
    }

    private fun setObservers() {

        viewModel.timeLiveData.observe(viewLifecycleOwner){ currentTime ->
            binding.timeChronometer.text = TimeUtil.getFormattedTime(currentTime)
        }

        viewModel.distanceLiveData.observe(viewLifecycleOwner) { currentDistance ->
            binding.distanceMeter.text = currentDistance.getDistanceString()
            curDistance = currentDistance
        }

        viewModel.trackerUIState.observe(viewLifecycleOwner) { state ->
            updateRecordUi(state)
        }

    }

    private fun updateRecordUi(state: TrackerState) {
        isTimerRunning = when (state) {
            is TrackerState.START -> {
                uiScreenStateRun()
                binding.actionBtn.setOnClickListener {
                    sendCommandToService(ACTION_PAUSE_SERVICE)
                }
                true
            }
            is TrackerState.PAUSE -> {
                uiScreenStatePaused()
                binding.continueBtn.setOnClickListener {
                    sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                }
                binding.actionBtn.setOnClickListener {

                    showRecordDialog(
                        finishAction = {
                            goToActivityCreateFragment()
                            sendCommandToService(ACTION_STOP_SERVICE)
                        },
                        cancelAction = {
                            //just dismiss dialog and do nothing
                        },
                        resetAction = {
                            showDialog(
                                affirmativeAction = { sendCommandToService(ACTION_STOP_SERVICE) },
                                negativeAction = {},
                                rationaleTxt = R.string.data_deleted_rationale
                            )
                        }
                    )

                }
                false
            }
            is TrackerState.END -> {
                uiScreenStateStopped()
                binding.actionBtn.setOnClickListener {
                    sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                }
                false
            }

        }
    }


    private fun sendCommandToService(action: String) {
        requireActivity().startService(Intent(requireActivity(), TrackerService::class.java).apply {
            this.action = action
        })
    }

    private fun uiScreenStateRun() {
        with(binding) {


            actionBtn.setIconResource(R.drawable.ic_pause_36)
            actionBtn.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.accent_color_primary, null))
            actionBtn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            continueBtn.visibility = View.GONE
        }
    }

    private fun uiScreenStatePaused() {
        with(binding) {
            actionBtn.setIconResource(R.drawable.ic_stop_36)

            actionBtn.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.snackWarningTextColor, null))
            actionBtn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            continueBtn.visibility = View.VISIBLE
            continueBtn.setIconResource(R.drawable.ic_play_arrow_36)
            continueBtn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        }
    }

    private fun uiScreenStateStopped() {
        with(binding) {

            actionBtn.setIconResource(R.drawable.ic_play_arrow_36)
            actionBtn.backgroundTintList = ColorStateList.valueOf(resources.getColor(
               R.color.accent_color_primary, null))
            actionBtn.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            continueBtn.visibility = View.GONE
        }

    }

    private fun goToActivityCreateFragment() {
        recData = ActivityRecordData(
            time = binding.timeChronometer.text.toString(),
            distance = curDistance.roundDoubleStringToKm().dropLastWhile { it != ' ' },
            startDate = getCurrentDate(),
            startTime = getCurrentTime()
        )

        val action = StartActivityFragmentDirections
            .actionStartActivityFragmentToCreateActivityFragment(
                recordData = recData
            )
        val animOptions = NavOptions.Builder().setAnimationTransit()
        findNavController().navigate(action, animOptions)
    }

    private fun showRecordDialog(
        finishAction: () -> Unit,
        resetAction: () -> Unit,
        cancelAction: () -> Unit
    ) {
        val dialog = Dialog(requireActivity())
        with(dialog) {
            setContentView(R.layout.dialog_finish_record)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setCancelable(false)

            window?.attributes?.windowAnimations = R.style.animation

            val finishBtn = this.findViewById<MaterialButton>(R.id.finishBtn)
            val cancelBtn = this.findViewById<MaterialButton>(R.id.cancelBtn)
            val resetBtn = this.findViewById<TextView>(R.id.resetBtn)


            finishBtn.setOnClickListener {
                finishAction()
                this.dismiss()
            }

            cancelBtn.setOnClickListener {
                cancelAction()
                this.dismiss()
            }

            resetBtn.setOnClickListener {
                resetAction()
                this.dismiss()
            }

            this.show()
        }
    }


}