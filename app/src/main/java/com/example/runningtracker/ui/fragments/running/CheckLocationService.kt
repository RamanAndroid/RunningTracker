package com.example.runningtracker.ui.fragments.running

import android.annotation.SuppressLint
import android.app.*
import android.app.Notification.CATEGORY_SERVICE
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build
import android.os.IBinder
import android.telephony.AvailableNetworkInfo.PRIORITY_HIGH
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.runningtracker.R
import com.example.runningtracker.database.entity.Point
import com.example.runningtracker.ui.fragments.running.RunningFragment.Companion.DISTANCE_FROM_SERVICE
import com.example.runningtracker.ui.fragments.running.RunningFragment.Companion.LOCATION_SERVICE_IS_START
import com.example.runningtracker.ui.fragments.running.RunningFragment.Companion.LIST_POINT
import com.example.runningtracker.ui.fragments.running.RunningFragment.Companion.LOCATION_UPDATE

class CheckLocationService : Service(), LocationListener {

    companion object {
        private const val LOCATION_CHANNEL_NAME = "CHANNEL_LOCATION"
        private const val LOCATION_CHANNEL_ID = "DEFAULT_LOCATION_CHANNEL_ID"
        private const val MIN_TIME_MILLISECONDS = 3000L
        private const val MIN_DISTANCE_METERS = 5F
        private const val LOCATION_FOREGROUND_SERVICE_ID = 1
        private const val PENDING_INTENT_REQUEST_CODE = 0
        private const val FIRST_ELEMENT_IN_LIST = 0
        private const val SIZE_OF_FLOAT_ARRAY = 1
    }

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var oldLatitude: Double? = null
    private var oldLongitude: Double? = null
    private val listPoint = mutableListOf<Point>()
    private val distanceList = FloatArray(SIZE_OF_FLOAT_ARRAY)
    private val allDistanceList = mutableListOf<Float>()
    private var locationManager: LocationManager? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        currentLatitude = location.latitude
        currentLongitude = location.longitude
        listPoint.add(Point(currentLongitude, currentLatitude))
        if (oldLatitude == null || oldLongitude == null) {
            oldLatitude = location.latitude
            oldLongitude = location.longitude
        }
        calculateDistance()
        allDistanceList.add(distanceList[FIRST_ELEMENT_IN_LIST])
        oldLatitude = currentLatitude
        oldLongitude = currentLongitude
    }


    private fun calculateDistance() {
        Location.distanceBetween(
            currentLatitude,
            currentLongitude,
            oldLatitude!!,
            oldLongitude!!,
            distanceList
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        createNotificationChanel()
        startForeground()
        locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {
        val notificationChannel = NotificationChannel(
            LOCATION_CHANNEL_ID,
            LOCATION_CHANNEL_NAME,
            IMPORTANCE_HIGH
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager.createNotificationChannel(notificationChannel)
    }

    private fun startForeground() {
        val pendingIntent: PendingIntent =
            Intent(this, CheckLocationService::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, PENDING_INTENT_REQUEST_CODE, notificationIntent, 0)
            }
        val notification = createNotification(pendingIntent = pendingIntent)
        startForeground(LOCATION_FOREGROUND_SERVICE_ID, notification)
    }

    @SuppressLint("InlinedApi")
    private fun createNotification(pendingIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(this, LOCATION_CHANNEL_ID)
            .setOngoing(true)
            .setContentTitle(getString(R.string.check_location))
            .setSmallIcon(R.drawable.ic_run_boots)
            .setContentIntent(pendingIntent)
            .setPriority(
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> IMPORTANCE_HIGH
                    else -> PRIORITY_HIGH
                }
            )
            .setCategory(CATEGORY_SERVICE)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.extras?.get(LOCATION_SERVICE_IS_START) == true) {
            locationManager?.requestLocationUpdates(GPS_PROVIDER, MIN_TIME_MILLISECONDS, MIN_DISTANCE_METERS, this)
        } else {
            val distance = allDistanceList.sum().toInt()
            val intentFinisRunning = Intent(LOCATION_UPDATE)
                .putExtra(LIST_POINT, listPoint as ArrayList<Point>)
                .putExtra(DISTANCE_FROM_SERVICE, distance)
            sendBroadcast(intentFinisRunning)
            stopForeground(true)
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }
}