package com.example.yourdrive.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.yourdrive.core_add.BaseFragment
import com.example.yourdrive.databinding.FragmentFirstScreenBinding


class FirstScreenFragment : BaseFragment<FirstScreenViewModel, FragmentFirstScreenBinding>() {

    override fun viewModelClass(): Class<FirstScreenViewModel> = FirstScreenViewModel::class.java

    override fun fragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFirstScreenBinding = FragmentFirstScreenBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}