package com.example.unitconverter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    private val openNextScreen = Runnable {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handler.postDelayed(openNextScreen, SPLASH_DELAY_MS)
    }

    override fun onDestroy() {
        handler.removeCallbacks(openNextScreen)
        super.onDestroy()
    }

    private companion object {
        const val SPLASH_DELAY_MS = 2500L
    }
}