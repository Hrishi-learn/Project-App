package com.hrishi.projectapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.DialogTitle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrishi.projectapp.Utils.Constants
import com.hrishi.projectapp.databinding.ActivityTaskListBinding
import com.hrishi.projectapp.firebase.FirestoreClass
import com.hrishi.projectapp.models.Board
import com.hrishi.projectapp.models.Card
import com.hrishi.projectapp.models.Task

class TaskList : BaseActivity() {
    private lateinit var binding: ActivityTaskListBinding
    private lateinit var mBoardDetails:Board
    private lateinit var documentId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        documentId=intent.getStringExtra(Constants.DOCUMENT_ID)!!
        showProgressDialog()
        FirestoreClass().getBoardData(this,documentId)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== Activity.RESULT_OK &&
            (requestCode==Constants.REQUEST_MEMBER_CODE || requestCode==Constants.CARD_DETAILS_CODE)){
            showProgressDialog()
            FirestoreClass().getBoardData(this,documentId)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setActionBar(){
        setSupportActionBar(binding.toolbarTaskListActivity)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarTaskListActivity.title = mBoardDetails.name
        binding.toolbarTaskListActivity.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
        binding.toolbarTaskListActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    fun cardDetails(cardPosition:Int,listPosition:Int){
        val intent=Intent(this,CardActivity::class.java)
        intent.putExtra(Constants.CARD_POS,cardPosition)
        intent.putExtra(Constants.LIST_POS,listPosition)
        intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
        startActivityForResult(intent,Constants.CARD_DETAILS_CODE)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.member_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members->{
                val intent= Intent(this,member::class.java)
                intent.putExtra(Constants.BOARD_DETAILS,mBoardDetails)
                startActivityForResult(intent,Constants.REQUEST_MEMBER_CODE)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun onSuccessUpdatedList(){
        FirestoreClass().getBoardData(this,mBoardDetails.documentId)
    }
    fun showBoardDetails(data: Board){
        mBoardDetails=data
        setActionBar()
        dismissDialog()
        val addItem:String=resources.getString(R.string.add_list)
        val task=Task(addItem,"", ArrayList())
        mBoardDetails.taskList.add(task)
        binding.rvTaskList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.rvTaskList.setHasFixedSize(true)
        val adapter=ItemTaskAdapter(this@TaskList,mBoardDetails.taskList)
        binding.rvTaskList.adapter=adapter
    }
    fun createTaskList(listTitle:String){
        Log.e("Task List Name", listTitle)
        showProgressDialog()
        mBoardDetails.taskList.add(0,Task(listTitle,FirestoreClass().getCurrentUserId()))
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        FirestoreClass().uploadTaskList(this,mBoardDetails)
    }
    fun updateTaskList(newListTitle:String,position:Int,model:Task){
        showProgressDialog()
        Log.e("edited list title",newListTitle)
        var task=Task(newListTitle,model.createdBy,model.CardList)
        mBoardDetails.taskList[position]=task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        FirestoreClass().uploadTaskList(this,mBoardDetails)
    }
    fun deleteTaskList(position:Int){
        showProgressDialog()
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        FirestoreClass().uploadTaskList(this,mBoardDetails)
    }
    fun uploadCard(position:Int,cardName:String,task:Task){
        var assignedCardList:ArrayList<String> = ArrayList()
        assignedCardList.add(FirestoreClass().getCurrentUserId())
        val card= Card(cardName,FirestoreClass().getCurrentUserId(),assignedCardList)
        task.CardList.add(card)
        mBoardDetails.taskList[position]=task
        showProgressDialog()
        FirestoreClass().uploadTaskList(this,mBoardDetails)
    }
}