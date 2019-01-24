package com.boostcamp.travery

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View

class TrackingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
    }

    fun startService(v: View) {

        val serviceIntent = Intent(this, MapTrackingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService(v: View) {
        val serviceIntent = Intent(this, MapTrackingService::class.java)
        stopService(serviceIntent)
    }
}
