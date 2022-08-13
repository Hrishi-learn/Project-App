package com.hrishi.projectapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.hrishi.projectapp.Utils.Constants
import com.hrishi.projectapp.databinding.ActivityBaseBinding
import com.hrishi.projectapp.databinding.ActivityCreateBoardBinding
import com.hrishi.projectapp.firebase.FirestoreClass
import com.hrishi.projectapp.models.Board
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException

class CreateBoard : BaseActivity(){
    private lateinit var binding: ActivityCreateBoardBinding
    private var selectedImageUri: Uri?=null
    private lateinit var userName:String
    private var mBoardImage:String=""
    private var documentId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        if(intent.hasExtra("name")){
            userName=intent.getStringExtra("name")!!
        }
        binding.ivBoardImage.setOnClickListener {
            getPermissions()
        }
        binding.btnCreate.setOnClickListener {
            showProgressDialog()
            if(selectedImageUri!=null){
                uploadBoardImage()
            }else{
                createBoard()
            }
        }
    }
    companion object{
        const val CREATE_BOARD_IMAGE=9
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode== CREATE_BOARD_IMAGE){
                if(data!=null){
                    val uri=data.data
                    selectedImageUri=uri
                    try{
                        Glide
                            .with(this)
                            .load(uri)
                            .centerCrop()
                            .into(binding.ivBoardImage);
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
                    val intent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, CREATE_BOARD_IMAGE)
                }
                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Constants.showPermissionDialog(this@CreateBoard)
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
        setSupportActionBar(binding.toolbarCreateBoardActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarCreateBoardActivity.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun boardCreatedSuccessfully() {
        Toast.makeText(this, "Board created successfully", Toast.LENGTH_SHORT).show()
        dismissDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun uploadBoardImage(){
        val storageRef=FirebaseStorage.getInstance().reference.child(
            Constants.BOARD + System.currentTimeMillis() + "." + getFileExtension(selectedImageUri)
        )
        storageRef.putFile(selectedImageUri!!).addOnSuccessListener {TaskSnapshot->
            TaskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                mBoardImage=it.toString()
                createBoard()
            }?.addOnFailureListener {
                Log.e("download image","${it.message}")
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Failed to upload the image", Toast.LENGTH_SHORT).show()
            Log.e("upload Image","${it.message}")
        }
    }
    private fun createBoard(){
        val assignList=ArrayList<String>()
        assignList.add(FirestoreClass().getCurrentUserId())
        val data=Board(
            binding.etBoardName.text?.toString()!!,
            userName,
            mBoardImage,
            assignList
        )
        FirestoreClass().uploadBoardData(this,data)
    }
    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}