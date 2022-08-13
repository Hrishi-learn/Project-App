package com.hrishi.projectapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.io.LineReader
import com.hrishi.projectapp.Utils.Constants
import com.hrishi.projectapp.databinding.ActivityMemberBinding
import com.hrishi.projectapp.databinding.DialogAddMemberBinding
import com.hrishi.projectapp.firebase.FirestoreClass
import com.hrishi.projectapp.models.Board
import com.hrishi.projectapp.models.User

class member : AppCompatActivity() {
    private lateinit var binding: ActivityMemberBinding
    private lateinit var mBoard: Board
    private lateinit var userList:ArrayList<User>
    private var isChanged:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra(Constants.BOARD_DETAILS)){
            mBoard=intent.getParcelableExtra<Board>(Constants.BOARD_DETAILS)!!
        }
        setActionBar()
        FirestoreClass().getMemberList(this,mBoard.assignedTo)
    }
    fun updateMembers(list:ArrayList<User>){
        userList=list
        binding.rvMembersList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvMembersList.adapter=ItemMemberAdapter(this,list)
    }
    fun addMembers(user:User){
        mBoard.assignedTo.add(user.id)
        FirestoreClass().assignedMembersToBoards(this,mBoard,user)
    }
    fun assignToBoardSuccess(user: User){
        userList.add(user)
        isChanged=true
        updateMembers(userList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_member_add,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.member_add->{
                addMemberDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun addMemberDialog(){
        val dialog= Dialog(this)
        dialog.setContentView(R.layout.dialog_add_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email:String=dialog.findViewById<AppCompatEditText>(R.id.et_email_search_member).text.toString()
            if(email.isNotEmpty()){
                FirestoreClass().addMember(this,email)
            }
            else{
                Toast.makeText(this, "Please provide a valid input", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setActionBar(){
        setSupportActionBar(binding.toolbarMembersActivity)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarMembersActivity.title = "Members"
        binding.toolbarMembersActivity.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
        binding.toolbarMembersActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    override fun onBackPressed() {
        if(isChanged){
            setResult(RESULT_OK)
        }
        super.onBackPressed()
    }
}