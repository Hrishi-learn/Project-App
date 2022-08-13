package com.hrishi.projectapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.hrishi.projectapp.databinding.ActivitySignUpBinding
import com.hrishi.projectapp.firebase.FirestoreClass
import com.hrishi.projectapp.models.User

class SignUp : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnSignUp.setOnClickListener {
            register()
        }
    }
    fun userRegisteredSuccessfully() {
        Toast.makeText(this, "Successfully registered " +
                "now please sign in to use the app", Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        finish()
    }
    private fun register(){
        val name=binding.etName.text.toString()
        val email=binding.etEmail.text.toString()
        val password=binding.etPassword.text.toString()
        if(validate(name,email,password)){
            showProgressDialog()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                dismissDialog()
                if(it.isSuccessful){
                    val firebaseUser:FirebaseUser=it.result!!.user!!
//                    val registeredEmail=firebaseUser.email
                    val user=User(firebaseUser.uid,name,email)
                    FirestoreClass().register(this,user)
                }else{
                    Log.e("sign up","${it.exception!!.message}")
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }

        }else{
            Toast.makeText(this, "Please fill the above details", Toast.LENGTH_SHORT).show()
        }
    }
    private fun validate(name:String,email:String,password:String):Boolean{
        return when{
            name.isEmpty()-> false
            email.isEmpty()->false
            password.isEmpty()->false
            else -> {
                true
            }
        }
    }

}