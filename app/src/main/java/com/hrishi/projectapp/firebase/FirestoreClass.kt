package com.hrishi.projectapp.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.hrishi.projectapp.*
import com.hrishi.projectapp.Utils.Constants
import com.hrishi.projectapp.models.Board
import com.hrishi.projectapp.models.User

class FirestoreClass:BaseActivity(){
    private val mFirestore=FirebaseFirestore.getInstance()
    fun register(activity: SignUp,userInfo:User){
        mFirestore.collection(Constants.USER).document(getCurrentUserId())
            .set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccessfully()
            }
    }
    fun getTheBoards(activity: MainActivity){
        mFirestore.collection(Constants.BOARD).
        whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId()).get().
        addOnSuccessListener {document->
            val boardList=ArrayList<Board>()
            for(it in document){
                val board=it.toObject(Board::class.java)
                board.documentId=it.id
                boardList.add(board)
            }
            activity.popUpBoards(boardList)
        }.addOnFailureListener {
            Log.e("Boards query","${it.message}")
        }
    }
    fun LoadUserData(activity: Activity){
        mFirestore.collection(Constants.USER).document(getCurrentUserId())
            .get().addOnSuccessListener {
                val data=it.toObject(User::class.java)
                if(data!=null){
                    when(activity){
                        is SignIn->{
                            activity.onSuccessSignIn(data)
                        }
                        is MainActivity->{
                            activity.showProfilePicAndUserName(data)
                        }
                        is MyProfile->{
                            activity.popUpProfileDetails(data)
                        }
                    }
                }
            }.addOnFailureListener {
                Log.e("SignIn Error","${it.message}")
            }
    }
    fun getCurrentUserId():String{
        val currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserId=""
        if(currentUser!=null){
            currentUserId=currentUser.uid
        }
        return currentUserId
    }
    fun updateUserProfileDate(activity:MyProfile,userHashMap: HashMap<String,Any>){
        FirebaseFirestore.getInstance().collection("Users").document(getCurrentUserId())
            .update(userHashMap).addOnSuccessListener {
                Toast.makeText(activity, "Successfully updated the profile", Toast.LENGTH_SHORT).show()
                activity.successfulUpdate()
            }
            .addOnFailureListener {
                Log.e("Update error","${it.message}")
                Toast.makeText(activity, "Failed to update the Profile", Toast.LENGTH_SHORT).show()
                activity.dismissDialog()
            }
    }
    fun uploadBoardData(activity: CreateBoard,board:Board){
        FirebaseFirestore.getInstance().collection(Constants.BOARD).document().
        set(board, SetOptions.merge()).addOnSuccessListener {
            activity.boardCreatedSuccessfully()
        }.addOnFailureListener {
            Log.e("Board upload","${it.message}")
            activity.dismissDialog()
            Toast.makeText(activity, "Failed to create the board", Toast.LENGTH_SHORT).show()
        }
    }
    fun getBoardData(activity:TaskList,documentId: String) {
        FirebaseFirestore.getInstance().collection(Constants.BOARD).document(documentId).
        get().addOnSuccessListener {
            val board=it.toObject(Board::class.java)
            board!!.documentId=it.id
            activity.showBoardDetails(board!!)
        }.addOnFailureListener {
            activity.dismissDialog()
            Log.e("Board error","${it.message}")
        }
    }
    fun uploadTaskList(activity: Activity,mBoard:Board){
        val taskList=Constants.TASK_LIST
        val hashMap=HashMap<String,Any>()
        hashMap[taskList]=mBoard.taskList
        mFirestore.collection(Constants.BOARD).document(mBoard.documentId)
            .update(hashMap).addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList updated successfully.")
                if(activity is TaskList)
                    activity.onSuccessUpdatedList()
                if(activity is CardActivity){
                    activity.UpdateCardSuccess()
                }
            }
            .addOnFailureListener {
                if(activity is TaskList)
                    activity.dismissDialog()
                if(activity is CardActivity)
                    activity.dismissDialog()
                Log.e("TaskList upload error","${it.message}")
            }
    }
    fun getMemberList(activity: member,assigned:ArrayList<String>){
        mFirestore.collection(Constants.USER).whereIn(Constants.ID,assigned).get()
            .addOnSuccessListener {
                val userList=ArrayList<User>()
                for(i in it.documents){
                    val user=i.toObject(User::class.java)!!
                    userList.add(user)
                }
                activity.updateMembers(userList)
            }
            .addOnFailureListener {
                Log.e("member upload error","${it.message}")
                Toast.makeText(this, "Could not update the members", Toast.LENGTH_SHORT).show()
            }
    }
    fun addMember(activity: Activity,email:String){
        mFirestore.collection(Constants.USER).whereEqualTo("email",email).get()
            .addOnSuccessListener {
                if(it.documents.size==0){

                }else{
                    val user:User=it.documents[0].toObject(User::class.java)!!
                    if(activity is member)
                        activity.addMembers(user)
                    if(activity is CardActivity)
                        activity.addMemberToCardsSuccess(user)
                }
            }
            .addOnFailureListener {
                Log.e("add member exception","${it.message}")
            }
    }
    fun assignedMembersToBoards(activity: member,board:Board,user:User){
        var hashMap=HashMap<String,Any>()
        hashMap[Constants.ASSIGNED_TO]=board.assignedTo
        mFirestore.collection(Constants.BOARD).document(board.documentId)
            .update(hashMap).addOnSuccessListener {
                activity.assignToBoardSuccess(user)
            }
            .addOnFailureListener {
                Log.e("update board error","${it.message}")
            }
    }
}