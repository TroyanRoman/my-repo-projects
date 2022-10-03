package com.skillbox.ascent.ui.fragments.share_fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.skillbox.ascent.data.ascent.models.AscentContact
import com.skillbox.ascent.databinding.ItemFriendBinding


class ContactAdapterDelegate(private val onItemClicked: (contact : AscentContact) -> Unit): AbsListItemAdapterDelegate<AscentContact, AscentContact, ContactsViewHolder>() {

    override fun isForViewType(item: AscentContact, items: MutableList<AscentContact>, position: Int): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup): ContactsViewHolder {
        val itemViewBinding =
            ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactsViewHolder(itemViewBinding, onItemClicked)
    }

    override fun onBindViewHolder(item: AscentContact, holder: ContactsViewHolder, payloads: MutableList<Any>) {
        holder.bind(item)
    }


}