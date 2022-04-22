package com.skillbox.ascent.ui.fragments.login_fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.skillbox.ascent.R
import com.skillbox.ascent.databinding.FragmentLoginBinding
import com.skillbox.ascent.ui.fragments.login_fragment.LoginViewModel
import com.skillbox.ascent.utils.launchAndCollectIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)

    private val viewModel: LoginViewModel by viewModels()

    private val getAuthResponse = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        val dataIntent = result.data ?: return@registerForActivityResult
        handleAuthResponseIntent(dataIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel(){
        binding.loginButton.setOnClickListener {
            viewModel.openLoginPage()
        }
        with(viewModel) {

            loadingFlow.launchAndCollectIn(viewLifecycleOwner){
                updateIsLoading(it)
            }
            openAuthPageFlow.launchAndCollectIn(viewLifecycleOwner){
                openAuthPage(it)
            }
            toastFlow.launchAndCollectIn(viewLifecycleOwner) {

            }
            authSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
                val action = LoginFragmentDirections.actionLoginFragmentToProfileFragment2()
                findNavController().navigate(action)
            }


        }
    }

    private fun updateIsLoading(isLoading : Boolean) = binding.loginButton.isClickable == !isLoading

    private fun openAuthPage(intent: Intent) {
        getAuthResponse.launch(intent)
    }

    private fun handleAuthResponseIntent(intent: Intent) {
        val exception = AuthorizationException.fromIntent(intent)
        val tokenExchangeRequest = AuthorizationResponse.fromIntent(intent)
            ?.createTokenExchangeRequest()
        when {
            exception != null ->{
                Log.d("Auth", "OnAuthCodeFailed")
                viewModel.onAuthCodeFailed(exception)}
            tokenExchangeRequest != null -> {
                Log.d("Auth", "OnAuthCodeReceived")
                viewModel.onAuthCodeReceived(tokenExchangeRequest)
            }

        }

    }

}