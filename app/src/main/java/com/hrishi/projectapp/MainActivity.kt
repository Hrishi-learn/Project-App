package com.hrishi.projectapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.hrishi.projectapp.Utils.Constants
import com.hrishi.projectapp.databinding.ActivityMainBinding
import com.hrishi.projectapp.firebase.FirestoreClass
import com.hrishi.projectapp.models.Board
import com.hrishi.projectapp.models.User

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener{
    private var userName:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActionBar()
        val navView:NavigationView=findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)
        FirestoreClass().LoadUserData(this)
        val fabBtn:FloatingActionButton=findViewById(R.id.fab_add)
        fabBtn.setOnClickListener {
            val intent=Intent(this@MainActivity,CreateBoard::class.java)
            intent.putExtra("name",userName)
            startActivityForResult(intent,BOARD_RESULT)
        }
        showProgressDialog()
        FirestoreClass().getTheBoards(this)
    }
    companion object{
        private const val PROFILE_UPDATE_RESULT=10
        private const val BOARD_RESULT=8
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK){
            if(requestCode==BOARD_RESULT){
                showProgressDialog()
                FirestoreClass().getTheBoards(this)
            }
            else if(requestCode== PROFILE_UPDATE_RESULT){
                FirestoreClass().LoadUserData(this)
            }
        }
    }

    private fun setActionBar(){
        val toolbar:Toolbar = findViewById(R.id.toolbar_main_activity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24)
        toolbar.setNavigationOnClickListener {
            toggle()
        }
    }
    private fun toggle() {
        val drawerLayout:DrawerLayout=findViewById(R.id.drawer_layout)
        if(!drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_my_profile->{
                startActivityForResult(Intent(this,MyProfile::class.java), PROFILE_UPDATE_RESULT)
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent=Intent(this,Intro::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        return true
    }
    fun popUpBoards(boardList:ArrayList<Board>){
        val rvBoard:RecyclerView=findViewById(R.id.rv_boards_list)
        val tvMain:TextView=findViewById(R.id.tv_no_boards_available)
        if(boardList.isEmpty()){
            tvMain.visibility= View.VISIBLE
            rvBoard.visibility=View.GONE
        }else{
            tvMain.visibility=View.GONE
            rvBoard.visibility=View.VISIBLE
            val adapter=ItemsAdapter(this,boardList)
            rvBoard.layoutManager=LinearLayoutManager(this)
            rvBoard.adapter=adapter
            adapter.setOnItemClickListener(object :ItemsAdapter.ItemListener{
                override fun onItemClickListener(position: Int, model: Board) {
                    val intent=Intent(this@MainActivity,TaskList::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,model.documentId)
                    startActivity(intent)
                }
            })
        }
        dismissDialog()
    }

    fun showProfilePicAndUserName(data: User){
        val imgView:ImageView=findViewById(R.id.iv_user_image)
        userName=data.name
        Glide
            .with(this)
            .load(Uri.parse(data.image))
            .centerCrop()
            .into(imgView)
        val tvUser: TextView = findViewById(R.id.tv_username)
        tvUser.text=data.name
    }
}