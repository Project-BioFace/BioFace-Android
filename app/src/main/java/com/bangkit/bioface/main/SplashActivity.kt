package com.bangkit.bioface

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.bioface.main.LandingActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.logoSplash)
        val textView = findViewById<TextView>(R.id.tv_splash)

        // Apply animations to logo and text
        val logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        val textAnimation = AnimationUtils.loadAnimation(this, R.anim.text_animation)

        logo.startAnimation(logoAnimation)
        textView.startAnimation(textAnimation)

        // Apply activity transition animation (slide in for LandingActivity, fade out for SplashActivity)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)

            // Transition animation: slide in for LandingActivity and fade out for SplashActivity
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

            finish()
        }, 3000)
    }
}
