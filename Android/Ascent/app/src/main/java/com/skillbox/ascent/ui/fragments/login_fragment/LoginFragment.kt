package com.skillbox.ascent.ui.fragments.login_fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.skillbox.ascent.R
import com.skillbox.ascent.databinding.FragmentLoginBinding
import com.skillbox.ascent.di.networking.NetworkStatus
import com.skillbox.ascent.utils.launchAndCollectIn
import com.skillbox.ascent.utils.setAnimationTransit
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)

    private val viewModel: LoginViewModel by viewModels()

    private val getAuthResponse =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val dataIntent = result.data ?: return@registerForActivityResult
            handleAuthResponseIntent(dataIntent)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        handleOnBackPressed()
        viewModel.checkConnectionsAndProceed()
        viewModel.isConnectedLiveData.observe(viewLifecycleOwner, ::isConnected)


    }

    private fun isConnected(networkStatus: NetworkStatus) {
        binding.loginButton.isEnabled = networkStatus is NetworkStatus.ConnectSuccess
        when (networkStatus) {
            is NetworkStatus.ConnectSuccess -> {
                binding.loginButton.elevation = 8f
                binding.loginButton.setText(R.string.login_btn_txt)
            }
            is NetworkStatus.ConnectError -> {
                binding.loginButton.elevation = 0f
                binding.loginButton.setText(networkStatus.error)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelCheckNetworkJob()
    }

    private fun bindViewModel() {
        binding.loginButton.setOnClickListener {

            viewModel.openLoginPage()
        }

        viewModel.loadingFlow.launchAndCollectIn(viewLifecycleOwner) {
            updateIsLoading(it)
        }
        viewModel.openAuthPageFlow.launchAndCollectIn(viewLifecycleOwner) {
            openAuthPage(it)
        }
        viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Some auth error happened", Toast.LENGTH_SHORT).show()
        }

        viewModel.authSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
            val action = LoginFragmentDirections.actionLoginFragmentToProfileFragment2()
            val animOptions = NavOptions.Builder().setAnimationTransit()
            findNavController().navigate(action, animOptions)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) = binding.loginButton.isVisible == !isLoading

    private fun openAuthPage(intent: Intent) {
        getAuthResponse.launch(intent)
    }

    private fun handleAuthResponseIntent(intent: Intent) {
        val exception = AuthorizationException.fromIntent(intent)
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            exception != null -> {
                viewModel.onAuthCodeFailed()
            }
            tokenExchangeRequest != null -> {
                viewModel.onAuthCodeReceived(tokenExchangeRequest)
            }

        }

    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.cancelCheckNetworkJob()
                    requireActivity().finishAndRemoveTask()
                }
            })
    }

}