package com.hrishi.projectapp.Utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

object Constants {
    const val USER="Users"
    const val BOARD="BOARDS"
    const val ASSIGNED_TO="assignedTo"
    const val DOCUMENT_ID="document_id"
    const val TASK_LIST="taskList"
    const val BOARD_DETAILS="board_details"
    const val ID="id"
    const val REQUEST_MEMBER_CODE=101
    const val CARD_DETAILS_CODE=102
    const val CARD_POS="card_position"
    const val LIST_POS="list_position"
    fun showPermissionDialog(activity:Activity) {
        val builder= AlertDialog.Builder(activity)
        builder.setMessage("Need permission to access photos")
        builder.setCancelable(false)
        builder.setPositiveButton("Go to settings"){ dialog, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
            }catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

}