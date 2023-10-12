package com.example.yourdrive.presentation.authorization

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.yourdrive.core_add.BaseFragment
import com.example.yourdrive.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>() {
    override fun fragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)

    override fun viewModelClass(): Class<LoginViewModel> = LoginViewModel::class.java
}