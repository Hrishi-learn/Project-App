package com.hrishi.projectapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hrishi.projectapp.databinding.ActivityIntroBinding

class Intro : AppCompatActivity() {
    private var binding: ActivityIntroBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        binding=ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.btnSignUpIntro?.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
        binding?.btnSignInIntro?.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }
    }
}