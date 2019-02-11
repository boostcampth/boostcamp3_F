package com.boostcamp.travery

import android.content.Intent
import com.boostcamp.travery.main.MainActivity
import com.daimajia.androidanimations.library.Techniques
import com.viksaa.sssplash.lib.activity.AwesomeSplash
import com.viksaa.sssplash.lib.model.ConfigSplash

class SplashActivity : AwesomeSplash() {
    override fun initSplash(configSplash: ConfigSplash?) {

        configSplash?.backgroundColor = R.color.colorApp
        configSplash?.logoSplash = R.drawable.ic_splash
        configSplash?.animLogoSplashDuration = 1000
        configSplash?.animLogoSplashTechnique = Techniques.Bounce

        configSplash?.originalHeight = 400
        configSplash?.originalWidth = 400
        configSplash?.titleSplash = getString(R.string.app_name)
        configSplash?.animTitleDuration = 1000
    }

    override fun animationsFinished() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}