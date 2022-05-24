package com.skillbox.ascent.ui.fragments.profile_fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.databinding.FragmentProfileBinding
import com.skillbox.ascent.utils.getWeightFromSpinner
import com.skillbox.ascent.utils.launchAndCollectIn
import com.skillbox.ascent.utils.resetNavGraph
import com.skillbox.ascent.utils.translit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {


    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindMainInfo()
        handleOnBackPress()
        setWeightAdapter()
        setWeight()

        viewModel.showNotificationMessage()


    }



    override fun onResume() {
        super.onResume()
        binding.logoutBtn.setOnClickListener {
            showExitDialog(exitMessageRes = R.string.dialog_logout_rationale ) {
                viewModel.logoutCurrentUser{requireActivity().finishAndRemoveTask()}
            }
        }
    }

    private fun bindMainInfo() {

        viewModel.getUserIdentity()
        viewModel.userLiveData.observe(viewLifecycleOwner) { user ->

            with(binding) {
                userName.text = getString(R.string.username_str, user.firstName, user.lastName)
                userMail.text = setUserNameText(user.firstName, user.lastName)
                followersQty.text = user.friendsCount.toString()
                followingQty.text = user.friendsCount.toString()
                country.text = user.country ?: "Not specified"
                gender.text = user.gender.uppercase()
                weight.hint = user.weight.toString() + " kg"


                Glide.with(avatarImage)
                    .load(user.avatar)
                    .placeholder(R.drawable.ic_load_avatar)
                    .error(R.drawable.ic_no_avatar)
                    .circleCrop()
                    .into(avatarImage)

                shareBtn.setOnClickListener {
                    val action =
                        ProfileFragmentDirections.actionProfileFragment2ToShareFragment(user)
                    findNavController().navigate(action)
                }

            }
           viewModel.saveUserData(user)

        }
    }

    private fun setWeight() {
        binding.dropdownWeight.setOnItemClickListener { adapterView, _, position, _ ->

            setWeightData(
                adapterView
                    .getItemAtPosition(position)
                    .toString()
                    .getWeightFromSpinner()
            )
        }
    }

    private fun setUserNameText(userFirstName: String, userLastName: String): String {
        return "@" + userFirstName.translit() + userLastName.translit()
    }

    private fun setWeightData(data: Float) {
        viewModel.updateUser(data) { updateHint(data) }

    }

    private fun updateHint(data: Float) {
        binding.weight.hint = "$data kg"
    }


    private fun showExitDialog(
        @StringRes exitMessageRes: Int,
        exitAppAction: () -> Unit
    ) {
        val negativeListener = DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
        }

        val positiveListener = DialogInterface.OnClickListener { _, _ ->
            exitAppAction()
        }

        val alertDialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_AppCompat_Dialog_Alert
        )
            .setTitle(R.string.alert_dialog_title)
            .setMessage(exitMessageRes)
            .setPositiveButton(R.string.dialog_action_positive, positiveListener)
            .setNegativeButton(R.string.dialog_action_negative, negativeListener)

        alertDialog.show()
    }

    private fun share(user: AscentUser) {

        Log.d("Share", "current userid = ${user.id}")
        binding.shareBtn.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragment2ToShareFragment(user)
            findNavController().navigate(action)
        }
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
                    showExitDialog(exitMessageRes = R.string.dialog_exit_rationale) {
                        requireActivity().finishAndRemoveTask()
                    }
                }
            })
    }

    private fun setMainInfo(profile: AscentUser) {
        with(binding) {
            userName.text = getString(R.string.username_str, profile.firstName, profile.lastName)
            userMail.text = setUserNameText(profile.firstName, profile.lastName)
            followersQty.text = profile.friendsCount.toString()
            followingQty.text = profile.friendsCount.toString()
            country.text = profile.country ?: "Not specified"
            gender.text = profile.gender.uppercase()
            weight.hint = profile.weight.toString() + " kg"


            Glide.with(avatarImage)
                .load(profile.avatar)
                .placeholder(R.drawable.ic_load_avatar)
                .error(R.drawable.ic_no_avatar)
                .circleCrop()
                .into(avatarImage)
        }


    }
}