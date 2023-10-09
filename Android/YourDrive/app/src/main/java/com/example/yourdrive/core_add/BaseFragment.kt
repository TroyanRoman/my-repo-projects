package com.example.yourdrive.core_add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewModel, V : ViewBinding> :
    com.github.johnnysc.coremvvm.presentation.BaseFragment<T>() {

    private var _binding: V? = null

    protected val binding get() = _binding!!

    protected abstract fun fragmentBinding(inflater: LayoutInflater, container: ViewGroup?): V

    override val layoutResId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = fragmentBinding(inflater, container)
        return _binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}