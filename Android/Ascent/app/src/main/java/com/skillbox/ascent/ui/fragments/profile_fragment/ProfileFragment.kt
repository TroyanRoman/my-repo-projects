package com.skillbox.ascent.ui.fragments.profile_fragment

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.databinding.FragmentProfileBinding
import com.skillbox.ascent.di.UserDataPreferences
import com.skillbox.ascent.ui.activity.MainActivity
import com.skillbox.ascent.utils.getWeightFromSpinner
import com.skillbox.ascent.utils.translit
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.skillbox.ascent.utils.withArguments as withArguments

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {



    private val binding by viewBinding(FragmentProfileBinding::bind)
    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var userDataPrefs : UserDataPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        bindMainInfo()
        handleOnBackPress()
        setWeightAdapter()




        binding.dropdownWeight.setOnItemClickListener { adapterView, _, position, _ ->

            setWeightData(adapterView.getItemAtPosition(position).toString().getWeightFromSpinner())
        }
    }

    override fun onResume() {
        super.onResume()

        share()
        binding.logoutBtn.setOnClickListener {
            showExitDialog()
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

            }
            userDataPrefs.setStoredUser(user)

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


    private fun showExitDialog() {
        val negativeListener = DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
        }

        val positiveListener = DialogInterface.OnClickListener { _, _ ->
            viewModel.corruptToken()
            requireActivity().finishAndRemoveTask()
        }

        val alertDialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_AppCompat_Dialog_Alert
        )
            .setTitle(R.string.alert_dialog_title)
            .setMessage(R.string.dialog_exit_rationale)
            .setPositiveButton(R.string.dialog_action_exit, positiveListener)
            .setNegativeButton(R.string.dialog_action_noexit, negativeListener)

        alertDialog.show()
    }

    private fun share() {
        binding.shareBtn.setOnClickListener {
            //realize share
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
                    showExitDialog()
                }
            })
    }








}