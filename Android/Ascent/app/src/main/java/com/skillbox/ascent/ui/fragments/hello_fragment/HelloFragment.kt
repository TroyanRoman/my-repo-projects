package com.skillbox.ascent.ui.fragments.hello_fragment

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.skillbox.ascent.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class HelloFragment : Fragment(R.layout.fragment_hello) {

    private val viewModel : HelloViewModel by viewModels()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var  runnable : Runnable

    override fun onResume() {
        super.onResume()
        Timber.tag("Lifecycle").d("HelloFragment OnResume")

        val loginFragmentAction = HelloFragmentDirections
            .actionHelloFragmentToLoginFragment()
        val profileFragmentAction = HelloFragmentDirections
            .actionHelloFragmentToProfileFragment2()
        val onBoardingFragmentAction = HelloFragmentDirections
            .actionHelloFragmentToOnBoardingFragment()

        runnable = Runnable {
            viewModel.providePrimaryNavigation(
                onLoginNeeded = { navigateToFragment(loginFragmentAction) },
                onLoggedIn = {navigateToFragment(profileFragmentAction)},
                onFirstEnter = {navigateToFragment(onBoardingFragmentAction)} )
        }
        handler.postDelayed(runnable,3000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    private fun navigateToFragment( action : NavDirections) {
        val navOptions = navigateWithAnimation()
        view?.findNavController()?.navigate(action, navOptions)
    }

    private fun navigateWithAnimation(): NavOptions = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setEnterAnim(R.anim.enter_anim)
        .setExitAnim(R.anim.exit_anim)
        .setPopEnterAnim(R.anim.pop_enter_anim)
        .setPopExitAnim(R.anim.pop_exit_anim)
        .build()


}