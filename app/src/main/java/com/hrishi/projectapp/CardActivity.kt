package com.hrishi.projectapp

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrishi.projectapp.Utils.Constants
import com.hrishi.projectapp.databinding.ActivityCardBinding
import com.hrishi.projectapp.firebase.FirestoreClass
import com.hrishi.projectapp.models.Board
import com.hrishi.projectapp.models.User
import java.time.Month
import java.util.*
import kotlin.properties.Delegates

class CardActivity : BaseActivity() {
    private lateinit var binding:ActivityCardBinding
    private var cardPos=-1
    private var listPos=-1
    private lateinit var board:Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActionBar()
        getIntentVal()
        val givenDate:String=board.taskList[listPos].CardList[cardPos].date
        binding.etNameCardDetails.setText(board.taskList[listPos].CardList[cardPos].name)
        if(givenDate.isNotEmpty())
            binding.tvSelectDueDate.text=givenDate
        binding.tvSelectDueDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val month:Int=monthOfYear+1
                binding.tvSelectDueDate.text=dayOfMonth.toString()+"/"+month.toString()+"/"+year.toString()
            }, year, month, day)
            dpd.datePicker.minDate=System.currentTimeMillis()
            dpd.show()
        }
        binding.btnUpdateCardDetails.setOnClickListener {
            if(binding.etNameCardDetails.text?.isEmpty()!!){
                Toast.makeText(this, "Card name could not empty", Toast.LENGTH_SHORT).show()
            }else{
                showProgressDialog()
                board.taskList[listPos].CardList[cardPos].name=binding.etNameCardDetails.text?.toString()!!
                board.taskList[listPos].CardList[cardPos].date=binding.tvSelectDueDate.text.toString()
                Log.e("date",board.taskList[listPos].CardList[cardPos].date)
                FirestoreClass().uploadTaskList(this,board)
            }
        }
        binding.tvSelectMembers.setOnClickListener {
            addMemberCards()
        }
        showMembers()
    }
    private fun showMembers(){
        val adapter=ItemMemberAdapter(this,board.taskList[listPos].CardList[cardPos].members)
        binding.rvCardMembers.layoutManager=LinearLayoutManager(this)
        binding.rvCardMembers.adapter=adapter
    }
    private fun addMemberCards(){
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
    fun addMemberToCardsSuccess(user:User){
        board.taskList[listPos].CardList[cardPos].members.add(user)
        showMembers()
    }
    private fun getIntentVal(){
        if(intent.hasExtra(Constants.CARD_POS))
            cardPos=intent.getIntExtra(Constants.CARD_POS,-1)
        if(intent.hasExtra(Constants.LIST_POS))
            listPos=intent.getIntExtra(Constants.LIST_POS,-1)
        if(intent.hasExtra(Constants.BOARD_DETAILS))
            board=intent.getParcelableExtra<Board>(Constants.BOARD_DETAILS)!!
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.card_delete_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_card->{
                alertDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun alertDialog(){
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setMessage("Are you sure you want to delete this Card")
        dialog.setPositiveButton("YES"){d,_->
            board.taskList[listPos].CardList.removeAt(cardPos)
            showProgressDialog()
            FirestoreClass().uploadTaskList(this,board)
            d.dismiss()
        }
        dialog.setNegativeButton("NO"){d,_->
            d.dismiss()
        }
        dialog.show()
    }
    fun UpdateCardSuccess(){
        dismissDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
    private fun setActionBar(){
        setSupportActionBar(binding.toolbarCardDetailsActivity)
        val actionBar=supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarCardDetailsActivity.title = resources.getString(R.string.app_name)
        binding.toolbarCardDetailsActivity.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24)
        binding.toolbarCardDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}