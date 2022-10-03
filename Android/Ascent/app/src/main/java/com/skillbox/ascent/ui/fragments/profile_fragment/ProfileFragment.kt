package com.skillbox.ascent.ui.fragments.profile_fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.data.ascent.models.ui_messages.UIMessage
import com.skillbox.ascent.data.ascent.models.ui_messages.WarningUIMessage
import com.skillbox.ascent.databinding.FragmentProfileBinding
import com.skillbox.ascent.di.networking.NetworkStatus
import com.skillbox.ascent.di.preferences.DarkThemePrefs
import com.skillbox.ascent.utils.setAnimationTransit
import com.skillbox.ascent.utils.showDialog
import com.skillbox.ascent.utils.translit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)


    private val viewModel: ProfileViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())

    @Inject
    lateinit var darkPrefs: DarkThemePrefs


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindUIState()
        handleOnBackPress()
        setWeightAdapter()
        setWeight()
        checkNetworkConnections()

        viewModel.updateMessage.observe(viewLifecycleOwner) {
            showMessage(it)
        }



        binding.logoutBtn.setOnClickListener {
            showDialog(
                rationaleTxt = R.string.dialog_logout_rationale,
                affirmativeAction = {
                    viewModel.logoutCurrentUser(
                        onLogoutSuccess = {
                            val action =
                                ProfileFragmentDirections.actionProfileFragment2ToLoginFragment()
                            val animOptions = NavOptions.Builder().setAnimationTransit()
                            findNavController().navigate(action, animOptions)
                        })
                },
                negativeAction = {}
            )
        }

        binding.retryBtn.setOnClickListener {
            viewModel.checkProfile()
        }


    }


    private fun checkNetworkConnections() {

        viewModel.checkNetworkConnections()
        viewModel.isConnected.observe(viewLifecycleOwner) { networkStatus ->

            when (networkStatus) {

                is NetworkStatus.ConnectSuccess -> handler.postDelayed({
                    if (this.isAdded) binding.notifyLayout.visibility = View.GONE
                }, 3000)

                is NetworkStatus.ConnectError -> showMessage(
                    WarningUIMessage(networkStatus.error)
                )

            }
        }
    }


    private fun showMessage(message: UIMessage) {
        with(binding) {
            notifyLayout.visibility = View.VISIBLE
            notificationMessage.setTextColor(resources.getColor(message.textColorRes, null))
            notifyCard.setCardBackgroundColor(
                resources.getColor(
                    message.backColorRes,
                    null
                )
            )
            notificationMessage.text = resources.getText(message.textRes)
            notificationIcon.setImageResource(message.pictureRes)
        }
    }


    override fun onResume() {
        super.onResume()

        binding.closeNotification.setOnClickListener {
            binding.notifyLayout.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelCheckNetworkJob()
    }

    private fun bindMainInfo(user: AscentUser) {
        with(binding) {
            userName.text =
                getString(R.string.username_str, user.firstName, user.lastName)
            userMail.text = setUserNameText(user.firstName, user.lastName)
            followersQty.text = user.friendsCount.toString()
            followingQty.text = user.friendsCount.toString()
            country.text = user.country ?: NOT_SPECIFIED
            gender.text = user.gender.uppercase()
            weightChooser.hint = when {
                user.weight == null -> NOT_SPECIFIED
                user.weight <= LOW_WEIGHT_FLOAT -> LOW_WEIGHT_STRING
                user.weight >= HIGH_WEIGHT_FLOAT -> HIGH_WEIGHT_STRING
                else -> user.weight.toString() + " kg"
            }


            Glide.with(avatarImage)
                .load(user.avatar)
                .placeholder(R.drawable.ic_load_avatar)
                .error(R.drawable.ic_no_avatar)
                .circleCrop()
                .into(avatarImage)
        }
    }

    private fun viewIsLoadingState(state: ProfileUIState.Loading) {
        with(binding) {
            progressBar.isVisible = state.isLoading
            loadErrorText.visibility = View.GONE
            retryBtn.visibility = View.GONE
        }
    }

    private fun viewLoadedSuccessState(user: AscentUser) {
        with(binding) {

            mainInfoCard.visibility = View.VISIBLE
            additionalInfoCard.visibility = View.VISIBLE
            logoutBtn.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            retryBtn.visibility = View.GONE
            bindMainInfo(user)

        }
    }

    private fun viewLoadedErrorState(state: ProfileUIState.Error) {
        with(binding) {
            progressBar.visibility = View.GONE
            mainInfoCard.visibility = View.GONE
            additionalInfoCard.visibility = View.GONE
            logoutBtn.visibility = View.GONE
            loadErrorText.setText(state.errorMessage)
            loadErrorText.visibility = View.VISIBLE
            retryBtn.visibility = View.VISIBLE
        }
    }

    private fun bindUIState() {

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileUIState.collect { state ->

                    when (state) {
                        is ProfileUIState.Loading -> viewIsLoadingState(state)
                        is ProfileUIState.Error -> viewLoadedErrorState(state)
                        is ProfileUIState.Success -> {

                            val user = state.user

                            viewLoadedSuccessState(user)

                            viewModel.showNotificationMessage()
                            binding.shareBtn.setOnClickListener {

                                val action =
                                    ProfileFragmentDirections.actionProfileFragment2ToShareFragment(
                                        user
                                    )
                                val animOptions = NavOptions.Builder().setAnimationTransit()
                                findNavController().navigate(action, animOptions)
                            }
                        }
                    }

                }
            }
        }
    }


    private fun setWeight() {

        binding.dropdownWeight.setOnItemClickListener { adapterView, _, position, _ ->
            val dataFromSpinner = adapterView.getItemAtPosition(position).toString()
            val dataFloat = when (dataFromSpinner) {
                LOW_WEIGHT_STRING -> LOW_WEIGHT_FLOAT
                HIGH_WEIGHT_STRING -> HIGH_WEIGHT_FLOAT
                else -> dataFromSpinner
                    .filter { it.isDigit() }
                    .toFloat()
            }


            viewModel.updateUser(
                dataFloat,
                onSuccess = {
                    binding.weightChooser.hint = dataFromSpinner
                }
            )
        }
    }


    private fun setUserNameText(userFirstName: String, userLastName: String): String {
        return "@" + userFirstName.translit() + userLastName.translit()
    }

    private fun setWeightAdapter() {

        val weightsArray: Array<String> = resources.getStringArray(R.array.weights_array)
        val weightsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu, weightsArray)

        binding.dropdownWeight.setAdapter(weightsAdapter)
    }


    private fun handleOnBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showDialog(
                        rationaleTxt = R.string.dialog_exit_rationale,
                        affirmativeAction = {
                            requireActivity().finishAndRemoveTask()
                        },
                        negativeAction = {})
                }
            })
    }

    companion object {
        private const val LOW_WEIGHT_STRING = "Less than 40 kg"
        private const val LOW_WEIGHT_FLOAT = 39f
        private const val HIGH_WEIGHT_STRING = "More than 120 kg"
        private const val HIGH_WEIGHT_FLOAT = 121f
        private const val NOT_SPECIFIED = "Not specified"
    }

}