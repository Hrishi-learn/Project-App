package com.hrishi.projectapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hrishi.projectapp.databinding.ActivitySignInBinding
import com.hrishi.projectapp.firebase.FirestoreClass
import com.hrishi.projectapp.models.User

class SignIn : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        binding=ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnSignIn.setOnClickListener {
            signInUser()
        }
    }
    fun onSuccessSignIn(data: User) {
        dismissDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
    private fun signInUser(){
        val email=binding.etEmailSignIn.text.toString()
        val password=binding.etPasswordSignIn.text.toString()
        showProgressDialog()
        if(validate(email,password)){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if(it.isSuccessful){
                   FirestoreClass().LoadUserData(this)
                }else{
                    dismissDialog()
                    Log.e("Sign in","${it.exception!!.message}")
                    Toast.makeText(this, "Sign in went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this, "Please fill up the above details", Toast.LENGTH_SHORT).show()
        }
    }
    private fun validate(email:String,password:String):Boolean{
        return when{
            email.isEmpty()-> false
            password.isEmpty()->false
            else -> {
                true
            }
        }
    }

}