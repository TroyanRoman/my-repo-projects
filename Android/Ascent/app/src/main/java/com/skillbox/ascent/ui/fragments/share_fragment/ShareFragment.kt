package com.skillbox.ascent.ui.fragments.share_fragment

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skillbox.ascent.R
import com.skillbox.ascent.databinding.FragmentShareBinding
import com.skillbox.ascent.utils.autoCleared
import com.skillbox.mycontentprovider.list.adapter.ContactsAdapter
import permissions.dispatcher.*

@RuntimePermissions
class ShareFragment : Fragment(R.layout.fragment_share) {

    private val binding by viewBinding(FragmentShareBinding::bind)

    private val viewModel: ShareViewModel by viewModels()

    private var contactAdapter: ContactsAdapter by autoCleared()

    private fun initList() {
        contactAdapter = ContactsAdapter()
        binding.contactsList.apply {
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun bindViewModel() {
        viewModel.contacts.observe(viewLifecycleOwner) {
            contactAdapter.items = it
        }
    }

    @OnShowRationale(Manifest.permission.READ_CONTACTS)
    fun showRationaleForReadContacts(request: PermissionRequest) {
        showRationaleDialog(R.string.permission_read_contacts_ratio, request)
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    fun onContactsDenied() {
        with(binding) {
            contactAdapter.items = emptyList()
            contactsList.visibility = View.INVISIBLE
            allowRationaleTxt.visibility = View.VISIBLE
            allowReadContactsBtn.visibility = View.VISIBLE
        }


    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    fun onContactsNeverAskAgain() {
        with(binding) {
            contactAdapter.items = emptyList()
            contactsList.visibility = View.INVISIBLE
            allowReadContactsBtn.visibility = View.INVISIBLE
            allowRationaleTxt.text =
                resources.getText(R.string.allow_ascent_to_use_your_contacts_again)
            allowRationaleTxt.visibility = View.VISIBLE
        }
    }

    private fun showRationaleDialog(@StringRes ratio: Int, request: PermissionRequest ) {

    }




}