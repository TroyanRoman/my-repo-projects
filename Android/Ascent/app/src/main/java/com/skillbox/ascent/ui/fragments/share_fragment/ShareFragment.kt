package com.skillbox.ascent.ui.fragments.share_fragment

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.skillbox.ascent.R
import com.skillbox.ascent.databinding.FragmentShareBinding
import com.skillbox.ascent.utils.autoCleared
import com.skillbox.ascent.ui.fragments.share_fragment.adapter.ContactsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import permissions.dispatcher.PermissionRequest

import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

@AndroidEntryPoint
class ShareFragment : Fragment(R.layout.fragment_share) {

    private val binding by viewBinding(FragmentShareBinding::bind)

    private val viewModel by viewModels<ShareViewModel>()

    private var contactAdapter: ContactsAdapter by autoCleared()

    private var permissionRequester : PermissionsRequester by autoCleared()

    private val args: ShareFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        permissionRequester = constructPermissionsRequest(
           *arrayOf( Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS),
            onShowRationale = ::showRationaleForReadContacts,
            onPermissionDenied = ::onContactsDenied,
            onNeverAskAgain = ::onContactsNeverAskAgain,
            requiresPermission = {viewModel.loadListContacts()}
        )

    }





    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initList()
        bindViewModel()
        onPermissionResult()




        binding.allowReadContactsBtn.setOnClickListener {
            onPermissionResult()
        }
    }


    private fun initList() {
        contactAdapter = ContactsAdapter { contact ->
            contact.phoneNumbers?.first()?.let { viewModel.shareLink(args.profile.id, it) }
        }
        binding.contactsList.apply {
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }


    private fun bindViewModel() {
        viewModel.contacts.observe(viewLifecycleOwner) {
            contactAdapter.items = it
            if(contactAdapter.itemCount!= 0) binding.contactsList.visibility = View.VISIBLE else View.GONE

        }

    }

    private fun onPermissionResult() {
        Handler(Looper.getMainLooper()).post {
           permissionRequester.launch()
        }
    }


    private fun showRationaleForReadContacts(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_read_contacts_ratio, request)
    }


    private fun onContactsDenied() {
        with(binding) {
            contactAdapter.items = emptyList()
            contactsList.visibility = View.INVISIBLE
            allowRationaleTxt.visibility = View.VISIBLE
            allowReadContactsBtn.visibility = View.VISIBLE
        }


    }


    private fun onContactsNeverAskAgain() {
        with(binding) {
            contactAdapter.items = emptyList()
            contactsList.visibility = View.INVISIBLE
            allowReadContactsBtn.visibility = View.INVISIBLE
            allowRationaleTxt.text =
                resources.getText(R.string.allow_ascent_to_use_your_contacts_again)
            allowRationaleTxt.visibility = View.VISIBLE
        }
    }


    private fun showRationaleDialog(@StringRes ratio: Int, request: PermissionRequest) {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.dialog_action_positive) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.dialog_action_negative) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(ratio)
            .show()

    }


}