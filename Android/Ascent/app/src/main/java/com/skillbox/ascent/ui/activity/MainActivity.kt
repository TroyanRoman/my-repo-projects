package com.skillbox.ascent.ui.activity


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.skillbox.ascent.R
import com.skillbox.ascent.application.theme_change.AppThemeSelector
import com.skillbox.ascent.databinding.ActivityMainBinding
import com.skillbox.ascent.di.preferences.NotificationPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    private val viewModel: MainViewModel by viewModels()


    @Inject
    lateinit var themeSelector: AppThemeSelector

    @Inject
    lateinit var notificationPrefs: NotificationPrefs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.authTokenRefresh()

        val navController = findNavController(R.id.nav_host_fragment)

        setupDestinations(navController)
        setupBottomNavigation(navController)
        handleIntentData()

        binding.toolbarNew.setOnMenuItemClickListener {
            themeSelector.changeAppTheme()
            true
        }

    }


    private fun handleIntentData() {

        val appLinkAction = intent.action

        if (Intent.ACTION_VIEW == appLinkAction) {
            when (intent.getStringExtra(INTENT_TAG)) {
                "notification" -> {
                    val uri = Uri.parse(ACTIVITIES_DEEPLINK_URI)
                    findNavController(R.id.nav_host_fragment).navigate(uri)
                }

                null -> {
                    val uri = Uri.parse(SHARED_PROFILE_URI)
                    findNavController(R.id.nav_host_fragment).navigate(uri)
                }

            }
        }
    }


    private fun setupDestinations(navController: NavController) {

        val bottomNavigationDestinations = listOf(
            R.id.helloFragment,
            R.id.onBoardingFragment,
            R.id.loginFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->

            binding.bottomMenu.isGone = bottomNavigationDestinations.contains(destination.id)
            binding.appBar.isGone = bottomNavigationDestinations.contains(destination.id)
            binding.toolbarNew.isGone = bottomNavigationDestinations.contains(destination.id)

            when (destination.id) {

                R.id.profileFragment2 -> {
                    binding.toolbarNew.title = resources.getString(R.string.profile_title)
                    binding.bottomMenu.menu.getItem(
                        PROFILE_POSITION
                    ).isChecked = true
                }
                R.id.activitiesFragment2 -> {
                    binding.toolbarNew.title = resources.getString(R.string.activities_title)
                    binding.bottomMenu.menu.getItem(
                        ACTIVITIES_POSITION
                    ).isChecked = true
                }

                R.id.athletesFragment -> {
                    binding.toolbarNew.title = resources.getString(R.string.shared_profile)
                    binding.bottomMenu.menu.getItem(
                        ATHLETE_POSITION
                    ).isChecked =
                        true
                }

                R.id.createActivityFragment -> binding.toolbarNew.title =
                    resources.getString(R.string.add_activities_title)

                R.id.shareFragment -> binding.toolbarNew.title =
                    resources.getString(R.string.share_title)

                R.id.startActivityFragment -> binding.toolbarNew.title = resources.getString(R.string.record_activity)
            }
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
                R.id.athleteFragment -> {
                    navController.navigate(R.id.athletesFragment, null, navOptions)
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


    companion object {
        private const val PROFILE_POSITION = 0
        private const val ACTIVITIES_POSITION = 1
        private const val ATHLETE_POSITION = 2
        private const val INTENT_TAG = "intent_tag"
        private const val ACTIVITIES_DEEPLINK_URI = "ascent://com.skillbox.ascent/activities"
        private const val SHARED_PROFILE_URI = "ascent://com.skillbox.ascent/athlete"
    }


}


