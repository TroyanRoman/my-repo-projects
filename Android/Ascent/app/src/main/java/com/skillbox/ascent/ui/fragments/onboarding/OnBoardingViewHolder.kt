package com.skillbox.ascent.ui.fragments.onboarding

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.skillbox.ascent.databinding.ItemOnboardingBinding

class OnBoardingViewHolder(private val binding: ItemOnboardingBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(onBoardingScreen: OnBoardingScreen) {
        with(binding) {
            onBoardingTitle.text = onBoardingScreen.title
            onBoardingDescription.text = onBoardingScreen.description
            Glide.with(onBoardingIcon)
                .asDrawable()
                .load(onBoardingScreen.icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(onBoardingIcon)
        }


    }
}