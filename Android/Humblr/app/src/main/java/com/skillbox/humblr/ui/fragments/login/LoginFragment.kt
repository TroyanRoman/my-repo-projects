package com.skillbox.humblr.ui.fragments.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding

import com.skillbox.humblr.R
import com.skillbox.humblr.databinding.FragmentLoginBinding
import com.skillbox.humblr.di.AuthTokenPreference
import com.skillbox.humblr.oauth_data.AuthConfig
import com.skillbox.humblr.utils.navigateWithAnimation
import com.skillbox.humblr.utils.toast
//import com.skillbox.humblr.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)

    private val viewModel: LoginViewModel by viewModels()

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val tokenExchangeRequest = result.data?.let { intent ->
                    AuthorizationResponse.fromIntent(intent)
                        ?.createTokenExchangeRequest()
                }


                val exception = AuthorizationException.fromIntent(result.data)
                when {

                    tokenExchangeRequest != null && exception == null ->
                        viewModel.onAuthCodeReceived(tokenExchangeRequest)
                    exception != null -> {
                        viewModel.onAuthCodeFailed(exception)
                    }
                }
            } else {
                Timber.tag("TokenStatus").d("ResultCode = ${result.resultCode}")

                Timber.tag("TokenStatus").d("Data = ${result.data}")
            }

        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        Timber.tag("Lifecycle").d("LoginFragment OnViewCreated")

    }




    private fun bindViewModel() {
        with(viewModel) {

            binding.loginBtn.setOnClickListener {
                this.openLoginPage()
            }

            loadingLiveData.observe(viewLifecycleOwner, ::updateIsLoading)
            openAuthPageLiveData.observe(viewLifecycleOwner, ::openAuthPage)
            toastLiveData.observe(viewLifecycleOwner, ::toast)
            authSuccessLiveData.observe(viewLifecycleOwner) {

                val navOptions = navigateWithAnimation()
                val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
                findNavController().navigate(action, navOptions)
                toast(R.string.auth_success)

            }
        }


    }

    private fun updateIsLoading(isLoading: Boolean) {
        binding.loginBtn.isVisible = !isLoading
    }

    private fun openAuthPage(intent: Intent) {

        startForResult.launch(intent)
    }


}