package com.skillbox.ascent.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
//import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.WorkManager
//import androidx.work.workDataOf
import by.kirich1409.viewbindingdelegate.viewBinding
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.databinding.ActivityMainBinding
import com.skillbox.ascent.di.NotificationPrefs
import com.skillbox.ascent.notifications.AscentMessageService
//import com.skillbox.ascent.notifications.ReminderWorker
import com.skillbox.ascent.ui.fragments.profile_fragment.ProfileFragment
import com.skillbox.ascent.ui.fragments.sport_activities.ActivitiesFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    private val viewModel:MainViewModel by viewModels()

    private lateinit var pushBroadcastReceiver : BroadcastReceiver

    @Inject
    lateinit var notificationPrefs: NotificationPrefs






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.authTokenRefresh()
        //createWorkRequest(1)

       // viewModel.showNotificationMessage()

        val navController = findNavController(R.id.nav_host_fragment)

        pushBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val extras = intent?.extras
                Log.d("FirebaseLog", "intent extras incoming = $extras")
                extras?.keySet()?.firstOrNull(){
                    it == AscentMessageService.KEY_ACTION
                }?.let { key->
                    when(extras.getString(key)) {
                        AscentMessageService.ACTION_SHOW_MESSAGE -> {
                            extras.getString(AscentMessageService.KEY_MESSGAE)?.let { message->
                                Log.d("FirebaseLog", "message received")
                                Toast.makeText(applicationContext,message, Toast.LENGTH_LONG).show()
                            }
                        }
                        else-> Log.e("FirebaseLog", "no needed key found")
                    }
                }
            }
        }
         val intentFilter = IntentFilter()
        intentFilter.addAction(AscentMessageService.PUSH_INTENT_FILTER)

        registerReceiver(pushBroadcastReceiver, intentFilter)

        setupBottomNavBar(navController)
        setupBottomNavigation(navController)
        handleIntentData()

    }



    override fun onDestroy() {
        notificationPrefs.setIsMessageShown(false)
        unregisterReceiver(pushBroadcastReceiver)
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
       // handleIntentData(intent)
      //  findNavController(R.id.nav_host_fragment).handleDeepLink(intent)
        Log.d("ReminderWorker", " Intent for worker = $intent")
        if(intent!=null) {
            Log.d("ReminderWorker", " Intent for worker = $intent")
            val uri = Uri.parse("ascent://com.skillox.ascent/activities")
            findNavController(R.id.nav_host_fragment).navigate(uri)
        }
    }


    //"https://www.strava.com/athletes/101238812"
    private fun handleIntentData() {

        val appLinkAction = intent.action
        val appLinkData = intent.data

        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.lastPathSegment?.also { athleteId->
                Uri.parse(
                    "ascent://com.mystrava.tro"
                ).buildUpon()
                    .appendPath(athleteId)
                    .build()
                    .also { appData->
                         showData(appData)
                    }
            }}
    }

    private fun showData(appData : Uri) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ProfileFragment())
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

