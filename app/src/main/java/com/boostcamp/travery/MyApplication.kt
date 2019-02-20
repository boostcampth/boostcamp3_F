package com.boostcamp.travery

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.facebook.stetho.Stetho
import com.google.android.libraries.places.api.Places

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        createNotificationChannel()

        Places.initialize(this, resources.getString(R.string.google_maps_key))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}