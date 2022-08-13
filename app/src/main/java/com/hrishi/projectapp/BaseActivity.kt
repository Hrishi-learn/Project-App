package com.hrishi.projectapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.hrishi.projectapp.databinding.ActivityBaseBinding

open class BaseActivity : AppCompatActivity() {
    private var binding:ActivityBaseBinding?=null
    private lateinit var dialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }
    fun showProgressDialog(){
        dialog= Dialog(this)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.show()
    }
    fun dismissDialog(){
        dialog.dismiss()
    }
    fun currentUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}