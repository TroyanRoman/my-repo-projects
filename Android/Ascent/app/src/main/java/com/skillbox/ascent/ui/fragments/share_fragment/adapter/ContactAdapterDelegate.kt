package com.skillbox.mycontentprovider.list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.AscentContact
import com.skillbox.ascent.databinding.ItemFriendBinding


class ContactAdapterDelegate: AbsListItemAdapterDelegate<AscentContact, AscentContact, ContactAdapterDelegate.Holder>() {

    override fun isForViewType(item: AscentContact, items: MutableList<AscentContact>, position: Int): Boolean {
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        val itemViewBinding =
            ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(itemViewBinding)
    }

    override fun onBindViewHolder(item: AscentContact, holder: Holder, payloads: MutableList<Any>) {
        holder.bind(item)
    }

    class Holder(
        private val itemViewBinding: ItemFriendBinding,

    ) : RecyclerView.ViewHolder(itemViewBinding.root) {


        fun bind(contact: AscentContact) {

            with(itemViewBinding) {
                userName.text = contact.name
                contactPhoneNumber.text =
                    contact.phoneNumbers.firstOrNull().toString()
                Glide.with(avatarImage)
                    .load(contact.avatar)
                    .placeholder(R.drawable.ic_no_avatar)
                    .error(R.drawable.ic_load_avatar)
                    .into(avatarImage)


            }
        }
    }
}