package com.example.runningtracker.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.runningtracker.ApplicationConstants
import com.example.runningtracker.R
import com.example.runningtracker.RunningTrackerApplication
import com.example.runningtracker.databinding.SplashScreenBinding


class SplashScreen : AppCompatActivity() {

    private lateinit var binding: SplashScreenBinding

    companion object {
        private const val TRANSITION_TIME = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_image)
        binding.logoImage.startAnimation(rotate)
        val token = RunningTrackerApplication.sharedPreferences.getString(
            ApplicationConstants.CONSTANTS_USER_TOKEN,
            ""
        )
        Handler().postDelayed({
            if (token.isNullOrEmpty()) {
                val intent = Intent(this, AuthorizationActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, TRANSITION_TIME)
    }
}