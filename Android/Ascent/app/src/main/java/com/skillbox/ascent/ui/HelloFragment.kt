package com.skillbox.ascent.ui

import android.os.Handler
import android.os.Looper
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.preferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.skillbox.ascent.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class HelloFragment : Fragment(R.layout.fragment_hello) {

    @Inject
    lateinit var prefs: DataStore<Preferences>
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var  runnable : Runnable

    override fun onResume() {
        super.onResume()
        Timber.tag("Lifecycle").d("HelloFragment OnResume")
        runnable = Runnable {
            lifecycleScope.launch {
                prefs.data.collectLatest {
                    val navOptions = navigateWithAnimation()
                    if (it[preferencesKey<Boolean>("onBoard")] == true) {
                        //navigate to login fragment
                        view?.findNavController()?.navigate(HelloFragmentDirections.actionHelloFragmentToLoginFragment(), navOptions)
                    } else {
                        //navigate to onBoardingFragment

                        view?.findNavController()?.navigate(HelloFragmentDirections.actionHelloFragmentToOnBoardingFragment(), navOptions)
                    }
                }
            }
        }
        handler.postDelayed(runnable,3000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    private fun navigateWithAnimation(): NavOptions = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setEnterAnim(R.anim.enter_anim)
        .setExitAnim(R.anim.exit_anim)
        .setPopEnterAnim(R.anim.pop_enter_anim)
        .setPopExitAnim(R.anim.pop_exit_anim)
        .build()
}