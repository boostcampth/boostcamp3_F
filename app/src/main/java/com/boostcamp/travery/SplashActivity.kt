package com.boostcamp.travery

import android.content.Intent
import com.boostcamp.travery.main.MainActivity
import com.daimajia.androidanimations.library.Techniques
import com.viksaa.sssplash.lib.activity.AwesomeSplash
import com.viksaa.sssplash.lib.model.ConfigSplash

class SplashActivity : AwesomeSplash() {
    override fun initSplash(configSplash: ConfigSplash?) {

        configSplash?.apply {
            backgroundColor = R.color.colorApp
            logoSplash = R.drawable.splash_icon_150
            animCircularRevealDuration = 700
            animLogoSplashDuration = 800
            animLogoSplashTechnique = Techniques.Bounce
            originalHeight = 150
            originalWidth = 150
            titleSplash = getString(R.string.app_name)
            animTitleDuration = 800
            animTitleTechnique = Techniques.FlipInX
        }
    }

    override fun animationsFinished() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}