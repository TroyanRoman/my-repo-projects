package com.skillbox.ascent.tracker_service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.*
import com.skillbox.ascent.data.ascent.models.sport_activity.TrackerState
import com.skillbox.ascent.data.ascent.models.sport_activity.UserLocation
import com.skillbox.ascent.data.ascent.repositories.activities.StartActivityRepo
import com.skillbox.ascent.utils.Constants.ACTION_PAUSE_SERVICE
import com.skillbox.ascent.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.skillbox.ascent.utils.Constants.ACTION_STOP_SERVICE
import com.skillbox.ascent.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.skillbox.ascent.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.skillbox.ascent.utils.Constants.NOTIFICATION_ID
import com.skillbox.ascent.utils.TimeUtil
import com.skillbox.ascent.utils.getDistanceString
import com.skillbox.ascent.utils.zipWith
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class TrackerService : LifecycleService() {

    private var isServiceStarted = false

    private var locations: ArrayList<UserLocation> = arrayListOf()
    private lateinit var client: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var serviceRepo: StartActivityRepo

    private var currentDistance: Double = 0.0
    private var lapTime = 0L
    private var timeStarted = 0L
    private var isFirstRun = true


    override fun onCreate() {
        super.onCreate()
        initValues()

        val timeDistanceLiveData = serviceRepo.timeLiveData
            .zipWith(serviceRepo.distanceLiveData)

        timeDistanceLiveData.observe(this) { timeDistancePair ->
            val notificationMessage =
                "Time : " + TimeUtil.getFormattedTime(timeDistancePair.first) + "\n" +
                        "Distance : " + timeDistancePair.second.getDistanceString()
            if (isServiceStarted) {
                notificationBuilder.setContentText(
                    notificationMessage
                )
                    .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage))
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {

                    if (isFirstRun) {

                        startForegroundTimerService(it)
                        isFirstRun = false
                        isServiceStarted = true
                    } else {
                        serviceRepo.trackerState.postValue(TrackerState.START)
                        isServiceStarted = true
                        startTimer()
                        startAutoLocationsUpdate()
                    }
                }
                ACTION_STOP_SERVICE -> {

                    stopForegroundTimerService()
                }
                ACTION_PAUSE_SERVICE -> {
                    setOnPause()
                }
                else -> {
                }
            }
        }

        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        stopForegroundTimerService()
        stopForeground(true)
        stopSelf()
    }

    private fun setOnPause() {
        serviceRepo.trackerState.postValue(TrackerState.PAUSE)
        client.removeLocationUpdates(locationCallback)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            NOTIFICATION_ID
        )
        pauseTimer()
    }

    private fun pauseTimer() {
        isServiceStarted = false
        locations.clear()
        serviceRepo.timeLiveData.postValue(lapTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun stopForegroundTimerService() {


        isServiceStarted = false
        isFirstRun = true
        serviceRepo.trackerState.postValue(TrackerState.END)
        initValues()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            NOTIFICATION_ID
        )
        client.removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startForegroundTimerService(intent: Intent?) {
        serviceRepo.trackerState.postValue(TrackerState.START)
        startTimer()
        startAutoLocationsUpdate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            startForeground(NOTIFICATION_ID, notificationBuilder.build())
        } else {
            startService(intent)
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
            )

        notificationManager.createNotificationChannel(channel)
    }

    private fun startTimer() {
        timeStarted = System.currentTimeMillis() - lapTime
        CoroutineScope(Dispatchers.Main).launch {
            while (serviceRepo.trackerState.value!! == TrackerState.START && isServiceStarted) {
                lapTime = System.currentTimeMillis() - timeStarted
                serviceRepo.timeLiveData.postValue(lapTime)
                delay(1000L)
            }
        }
    }

    private fun startAutoLocationsUpdate() {


        client = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.create().apply {
            smallestDisplacement = MIN_DISPLACEMENT
            interval = TIME_INTERVAL
            fastestInterval = FASTEST_TIME_INTERVAL
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                for (location in locationResult.locations) {

                    val newLocation = UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )

                    locations.add(newLocation)

                    if (locations.size > 2) {
                        locations.remove(locations.first())


                        val previousIndex = locations.lastIndex - 1

                        val newDistance = calculateDistance(
                            currentLocation = locations.last(),
                            previousLocation = locations[previousIndex]
                        )

                        currentDistance += newDistance
                        serviceRepo.distanceLiveData.postValue(currentDistance)
                    }

                }


            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())


    }

    private fun calculateDistance(
        currentLocation: UserLocation,
        previousLocation: UserLocation
    ): Double {


        val currentLat = currentLocation.latitude
        val currentLong = currentLocation.longitude
        val prevLat = previousLocation.latitude
        val prevLong = previousLocation.longitude

        val resultsArray = FloatArray(1)
        Location.distanceBetween(prevLat, prevLong, currentLat, currentLong, resultsArray)
        return resultsArray[0].toDouble()
    }

    private fun initValues() {
        with(serviceRepo) {
            distanceLiveData.postValue(0.0)
            timeLiveData.postValue(0)
        }

    }

    companion object {
        private const val TIME_INTERVAL: Long = 5000
        private const val FASTEST_TIME_INTERVAL: Long = 5000
        private const val MIN_DISPLACEMENT = 10F
    }


}