package com.example.photochat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class splash_activity : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long=1500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_activity)
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity


                startActivity(Intent(this,SignInActivity::class.java))

            finish()
        }, SPLASH_TIME_OUT)
    }
}
