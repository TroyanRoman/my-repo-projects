package com.skillbox.ascent.ui.fragments.share_fragment.adapter

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.skillbox.ascent.data.ascent.models.AscentContact


class ContactsAdapter(onItemClicked: (contact: AscentContact)-> Unit) : AsyncListDifferDelegationAdapter<AscentContact>(ContactDiffUtilCallback()) {

    init {
        delegatesManager.addDelegate(ContactAdapterDelegate(onItemClicked))
    }

    class ContactDiffUtilCallback : DiffUtil.ItemCallback<AscentContact>() {
        override fun areItemsTheSame(oldItem: AscentContact, newItem: AscentContact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AscentContact, newItem: AscentContact): Boolean {
            return oldItem == newItem
        }
    }

}

