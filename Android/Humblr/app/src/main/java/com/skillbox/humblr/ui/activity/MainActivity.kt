package com.skillbox.humblr.ui.activity

import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.skillbox.humblr.R
import com.skillbox.humblr.databinding.ActivityMainBinding
import com.skillbox.humblr.di.DarkLightPrefs
import com.skillbox.humblr.networking.NetworkState
import com.skillbox.humblr.ui.fragments.main_fragment.MainFragment
import com.skillbox.humblr.utils.navigateWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity :
    AppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var networkState: NetworkState

    @Inject
    lateinit var darkPrefs: DarkLightPrefs

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    private var firstCheckedConnectivity: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = findNavController(R.id.nav_host_fragment)

        setupBottomNavBar(navController)
        setupBottomNavigation(navController)

        setupNetworkStateMonitoring()
        setUpAppTheme()



    }


    private fun setUpAppTheme() {
        val dayNightMode = darkPrefs.getDarkThemeStatus()
        AppCompatDelegate.setDefaultNightMode(dayNightMode)
        delegate.applyDayNight()
    }


    private fun setupBottomNavBar(navController: NavController) {

        val bottomNavigationDestanations = listOf(
            R.id.helloFragment,
            R.id.onBoardingFragment,
            R.id.loginFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomMenu.isGone = bottomNavigationDestanations.contains(destination.id)
        }
    }

    private fun setupBottomNavigation(navController: NavController) {

        val navOptions = navigateWithAnimation()
        binding.bottomMenu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.newsFragment -> {
                    navController.navigate(R.id.mainFragment, null, navOptions)
                    true
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.userProfileFragment, null, navOptions)
                    true
                }
                R.id.subredditsFragment -> {
                    navController.navigate(R.id.favoritesFragment, null, navOptions)
                    true
                }

                else -> false
            }
        }




    }

    @ExperimentalCoroutinesApi
    private fun setupNetworkStateMonitoring() {
        lifecycleScope.launchWhenCreated {
            networkState.changes()
                .onEach { isConnected ->
                    checkNetworkConnection(isConnected)
                }
                .launchIn(this)
        }

    }

    private fun checkNetworkConnection(isConnected: Boolean) {
        if (isConnected.not()) showSnackMessage() else if (firstCheckedConnectivity) Snackbar.make(
            binding.snackbarContainer,
            R.string.snackbar_update,
            Snackbar.LENGTH_SHORT
        )
            .show()
    }

    private fun showSnackMessage() {
        val contextView = binding.snackbarContainer
        firstCheckedConnectivity = true
        Snackbar.make(contextView, R.string.snackbar_connection_loss, Snackbar.LENGTH_INDEFINITE)
            .show()

    }
}