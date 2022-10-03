package com.skillbox.ascent.ui.fragments.hello_fragment

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.skillbox.ascent.R
import com.skillbox.ascent.utils.setAnimationTransit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
class HelloFragment : Fragment(R.layout.fragment_hello) {

    private val viewModel: HelloViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onResume() {
        super.onResume()

        val loginFragmentAction = HelloFragmentDirections
            .actionHelloFragmentToLoginFragment()
        val profileFragmentAction = HelloFragmentDirections
            .actionHelloFragmentToProfileFragment2()
        val onBoardingFragmentAction = HelloFragmentDirections
            .actionHelloFragmentToOnBoardingFragment()

        runnable = Runnable {
            viewModel.providePrimaryNavigation(
                onLoginNeeded = { navigateToFragment(loginFragmentAction) },
                onLoggedIn = { navigateToFragment(profileFragmentAction) },
                onFirstEnter = { navigateToFragment(onBoardingFragmentAction) })
        }

        handler.postDelayed(runnable, 3000)

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }


    private fun navigateToFragment(action: NavDirections) {
        val animOptions = NavOptions.Builder().setAnimationTransit()
        view?.findNavController()?.navigate(action, animOptions)
    }


}