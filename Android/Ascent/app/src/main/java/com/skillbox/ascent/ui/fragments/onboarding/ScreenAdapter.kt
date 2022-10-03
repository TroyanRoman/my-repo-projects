package com.skillbox.ascent.ui.fragments.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skillbox.ascent.databinding.ItemOnboardingBinding


class ScreenAdapter(private val onBoardingScreens: List<OnBoardingScreen>) :
    RecyclerView.Adapter<OnBoardingViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnBoardingViewHolder {
        return OnBoardingViewHolder(
            ItemOnboardingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun onBindViewHolder(
        holder:OnBoardingViewHolder,
        position: Int
    ) {
        holder.bind(onBoardingScreens[position])
    }

    override fun getItemCount(): Int = onBoardingScreens.size

}