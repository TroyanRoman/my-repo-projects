package com.skillbox.ascent.ui.fragments.share_fragment.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.AscentContact
import com.skillbox.ascent.databinding.ItemFriendBinding

class ContactsViewHolder(
    private val itemViewBinding: ItemFriendBinding,
    private val   onItemClicked: (contact: AscentContact) -> Unit
) : RecyclerView.ViewHolder(itemViewBinding.root) {


    fun bind(contact: AscentContact) {

        itemViewBinding.itemContact.setOnClickListener {
            onItemClicked(contact)
        }

        with(itemViewBinding) {
            userName.text = contact.name
            contactPhoneNumber.text =
                contact.phoneNumbers?.firstOrNull().toString()
            Glide.with(avatarImage)
                .load(contact.avatar)
                .circleCrop()
                .placeholder(R.drawable.ic_load_avatar)
                .error(R.drawable.ic_no_avatar)
                .into(avatarImage)


        }
    }
}