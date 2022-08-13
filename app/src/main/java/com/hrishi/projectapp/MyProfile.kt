package com.hrishi.projectapp

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.Instrumentation
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.hrishi.projectapp.Utils.Constants
import com.hrishi.projectapp.databinding.ActivityMyProfileBinding
import com.hrishi.projectapp.firebase.FirestoreClass
import com.hrishi.projectapp.models.User
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException


class MyProfile : BaseActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private var selectedImageUri:Uri?=null
    private var mProfileImageData:Uri?=null
    private lateinit var mProfileData:User
    override fun onCreate(savedInstanceState: Bundle?) {0
        super.onCreate(savedInstanceState)
        binding=ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        FirestoreClass().LoadUserData(this)
        binding.ivUserImageProfile.setOnClickListener {
            getPermissions()
        }
        binding.btnUpdate.setOnClickListener {
            showProgressDialog()
            if(selectedImageUri!=null){
                uploadImage()
            }
            else{
                updateUserData()
            }
        }
    }
    companion object{
        private const val GALLERY=1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode== GALLERY){
                if(data!=null){
                    val uri=data.data
                    selectedImageUri=uri
                    try{
                        Glide
                            .with(this)
                            .load(uri)
                            .centerCrop()
                            .into(binding.ivUserImageProfile);
                    }catch(e: IOException){
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    private fun getPermissions() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, GALLERY)
                }
                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Constants.showPermissionDialog(this@MyProfile)
                }
                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    private fun setActionBar(){
        setSupportActionBar(binding.toolbarMyProfileActivity)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarMyProfileActivity.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
        binding.toolbarMyProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun popUpProfileDetails(data: User) {
        Glide.with(this).load(data.image).centerCrop().into(binding.ivUserImageProfile)
        mProfileData=data
        binding.etEmail.setText(data.email)
        binding.etName.setText(data.name)
        if(data.mobile!=0L){
            binding.etMobile.setText(data.mobile.toString())
        }
    }
    private fun uploadImage(){
        if(selectedImageUri!=null){
            val storageRef=FirebaseStorage.getInstance().reference.child("USER_IMAGE"
            + System.currentTimeMillis() + "." + getFileExtension(selectedImageUri)
            )
            storageRef.putFile(selectedImageUri!!).addOnSuccessListener { taskSnapshot ->
                Log.i("image uri","${taskSnapshot.metadata?.reference?.downloadUrl}")
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    mProfileImageData=it
                    updateUserData()
                }
            }.addOnFailureListener{
                Log.e("Upload image error","${it.message}")
                updateUserData()
            }
        }
    }
    private fun updateUserData() {
        val userHashMap=HashMap<String,Any>()
        if(mProfileImageData!=null)
            userHashMap["image"]= mProfileImageData!!.toString()
        if(binding.etName.text?.isNotEmpty()==true)
            userHashMap["name"]=binding.etName.text!!.toString()
        if(binding.etMobile.text?.isNotEmpty()==true)
            userHashMap["mobile"]=binding.etMobile.text!!.toString().toLong()
        FirestoreClass().updateUserProfileDate(this,userHashMap)
    }
    fun successfulUpdate(){
        dismissDialog()
        setResult(RESULT_OK)
        finish()
    }
    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}