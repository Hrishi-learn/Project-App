package com.hrishi.projectapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.hrishi.projectapp.firebase.FirestoreClass

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler(Looper.myLooper()!!).postDelayed({
            if(FirestoreClass().getCurrentUserId().isEmpty())
                startActivity(Intent(this, Intro::class.java))
            else
                startActivity(Intent(this,MainActivity::class.java))
            finish()
        },2000)
    }
}