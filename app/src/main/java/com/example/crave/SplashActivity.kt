package com.example.crave

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

/**
 * Splash screen: shows "CRAVE" and the subtitle for a moment and then opens MainActivity.
 * It's the app's launcher Activity (see AndroidManifest.xml).
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // After 1500L we open the main screen.
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            // finish(): the splash is closed so the back button doesn't return to it.
            finish()
        }, 1500L)
    }
}
