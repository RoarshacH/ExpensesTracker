package com.example.expesestracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation)
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.animation = animation

        val r = Runnable {
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        }

        val h = Handler()
        // The Runnable will be executed after the given delay time
        h.postDelayed(r, SPLASH.toLong()) // will be delayed for 2 seconds

    }
}