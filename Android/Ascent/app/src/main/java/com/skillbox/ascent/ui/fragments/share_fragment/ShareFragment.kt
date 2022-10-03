package com.skillbox.ascent.ui.fragments.share_fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.ui_messages.ErrorUIMessage
import com.skillbox.ascent.data.ascent.models.ui_messages.UIMessage
import com.skillbox.ascent.databinding.FragmentShareBinding
import com.skillbox.ascent.ui.fragments.share_fragment.adapter.ContactsAdapter
import com.skillbox.ascent.utils.ImageOffsetDecoration
import com.skillbox.ascent.utils.autoCleared
import com.skillbox.ascent.utils.showDialog
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

@AndroidEntryPoint
class ShareFragment : Fragment(R.layout.fragment_share) {

    private val binding by viewBinding(FragmentShareBinding::bind)

    private val viewModel by viewModels<ShareViewModel>()

    private var contactAdapter: ContactsAdapter by autoCleared()

    private var permissionRequester: PermissionsRequester by autoCleared()

    private val args: ShareFragmentArgs by navArgs()


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        permissionRequester = constructPermissionsRequest(
            permissions = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS),
            onShowRationale = ::showRationaleForReadContacts,
            onPermissionDenied = ::onContactsDenied,
            onNeverAskAgain = ::onContactsNeverAskAgain,
            requiresPermission = ::showContactsListPermitted
        )

        initList()
        bindViewModel()
        onPermissionResult()


        binding.closeNotification.setOnClickListener {
            binding.notifyLayout.visibility = View.GONE
        }

        handleOnBackPressed()

        binding.allowReadContactsBtn.setOnClickListener {
            onPermissionResult()
        }
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }


    private fun initList() {
        contactAdapter = ContactsAdapter { contact ->
            contact.phoneNumbers?.first()?.let { phone ->
                viewModel.shareLink(phone, args.profile)
            }
        }
        binding.contactsList.apply {
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(ImageOffsetDecoration(requireContext()))
            setHasFixedSize(true)
        }
    }




    private fun bindViewModel() {
        viewModel.contacts.observe(viewLifecycleOwner) {
            contactAdapter.items = it
        }

        viewModel.isLoadingContacts.observe(viewLifecycleOwner) { isVisible ->
            binding.progressBar.isVisible = isVisible
        }

    }

    private fun onPermissionResult() {
        Handler(Looper.getMainLooper()).post {
            permissionRequester.launch()
        }
    }

    private fun showContactsListPermitted() {
        binding.allowRationaleTxt.visibility = View.GONE
        binding.allowReadContactsBtn.visibility = View.GONE
        binding.customNotificationLayout.visibility = View.GONE
        binding.contactsList.visibility = View.VISIBLE
        viewModel.loadListContacts()
    }

    private fun showErrorMessage() {
        showNotificationMessage(
            ErrorUIMessage(
                textRes = R.string.contacts_not_allowed,
            )
        )
    }


    private fun showRationaleForReadContacts(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_read_contacts_ratio, request)
    }


    private fun onContactsDenied() {
        with(binding) {
            contactAdapter.items = emptyList()
            contactsList.visibility = View.GONE
            allowRationaleTxt.visibility = View.VISIBLE
            allowReadContactsBtn.visibility = View.VISIBLE
        }
        showErrorMessage()
    }


    private fun onContactsNeverAskAgain() {
        with(binding) {
            contactAdapter.items = emptyList()
            contactsList.visibility = View.GONE
            allowReadContactsBtn.visibility = View.GONE
            allowRationaleTxt.text =
                resources.getText(R.string.allow_ascent_to_use_your_contacts_again)
            allowRationaleTxt.visibility = View.VISIBLE
        }

        showErrorMessage()
    }


    private fun showRationaleDialog(@StringRes ratio: Int, request: PermissionRequest) {
        this.showDialog(
            rationaleTxt = ratio,
            affirmativeAction = { request.proceed() },
            negativeAction = { request.cancel() }
        )
    }

    private fun showNotificationMessage(message: UIMessage) {
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



}