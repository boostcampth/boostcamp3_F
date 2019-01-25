package com.boostcamp.travery

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import android.content.ComponentName
import android.os.IBinder
import android.content.ServiceConnection
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.activity_tracking.*
import android.app.ActivityManager




class TrackingActivity : AppCompatActivity() {

    lateinit var myService: MapTrackingService
    var isService = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        val serviceIntent = Intent(this, MapTrackingService::class.java)
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE)
    }

    fun startService(v: View) {

        val serviceIntent = Intent(this, MapTrackingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE)
    }

    fun stopService(v: View) {
        val serviceIntent = Intent(this, MapTrackingService::class.java)
        unbindService(conn)
        stopService(serviceIntent)
    }

    fun getCount(v: View) {
        Toast.makeText(
            applicationContext,
            "받아온 데이터 : " + myService.getTotalSecond(),
            Toast.LENGTH_LONG
        ).show()
    }

    private var conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            name: ComponentName,
            service: IBinder
        ) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            val mb = service as MapTrackingService.LocalBinder
            myService = mb.service // 서비스가 제공하는 메소드 호출하여
            // 서비스쪽 객체를 전달받을수 있슴
            isService = myService.isRunning

            //서비스가 돌고 있을 때
            if(isService) {
                val counter = Thread(Counter())
                counter.start()
            }else{//서비스는 돌지 않고 바인드만 했을 때 바인드를 끊는다.
                unbindService(this)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false
        }
    }

    inner class Counter : Runnable {

        private val handler = Handler()
        override fun run() {
            while (true) {
                if (!isService) {
                    break
                }
                handler.post {
                    tv_text.text = setIntToTime(myService.getTotalSecond())
                }

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun setIntToTime(timeInt: Int):String {

        var min = timeInt/60
        val hour = min/60
        val sec = timeInt%60
        min %= 60

        return "${String.format("%02d", hour)}:${String.format("%02d", min)}:${String.format("%02d", sec)}"
    }
}
