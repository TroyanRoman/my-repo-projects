package com.skillbox.ascent.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.databinding.ActivityMainBinding
import com.skillbox.ascent.ui.fragments.sport_activities.ActivitiesFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = findNavController(R.id.nav_host_fragment)

        setupBottomNavBar(navController)
        setupBottomNavigation(navController)


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
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment2, null, navOptions)
                    true
                }
                R.id.activitiesFragment -> {
                     navController.navigate(R.id.activitiesFragment2, null, navOptions)
                    true
                }
                else -> false
            }
        }

    }

    private fun navigateWithAnimation(): NavOptions {
        return NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.enter_anim)
            .setExitAnim(R.anim.exit_anim)
            .setPopEnterAnim(R.anim.pop_enter_anim)
            .setPopExitAnim(R.anim.pop_exit_anim)
            .build()
    }


}

