package com.hrishi.projectapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hrishi.projectapp.databinding.ItemTaskBinding
import com.hrishi.projectapp.models.Task
import java.util.*
import kotlin.collections.ArrayList

class ItemTaskAdapter(val context: Context,var taskList:ArrayList<Task>):RecyclerView.Adapter<ItemTaskAdapter.ViewHolder>(){

    class ViewHolder(binding: ItemTaskBinding):RecyclerView.ViewHolder(binding.root){
        val tvAddTaskList=binding.tvAddTaskList
        val llTaskItem=binding.llTaskItem
        val tvTaskListTitle=binding.tvTaskListTitle
        val cvAddTaskListName=binding.cvAddTaskListName
        val ibCloseListName=binding.ibCloseListName
        val ibDoneListName=binding.ibDoneListName
        val etTaskListName=binding.etTaskListName
        val ibEditListName=binding.ibEditListName
        val ibDeleteList=binding.ibDeleteList
        val etEditTaskListName=binding.etEditTaskListName
        val cvEditTaskListName=binding.cvEditTaskListName
        val ibCloseEditableView=binding.ibCloseEditableView
        val ibDoneEditListName=binding.ibDoneEditListName
        val tvAddCard=binding.tvAddCard
        val cvAddCard=binding.cvAddCard
        val ibCloseCardName=binding.ibCloseCardName
        val ibDoneCardName=binding.ibDoneCardName
        val etCardName=binding.etCardName
        val rvCardList=binding.rvCardList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model=taskList[position]

        holder.tvTaskListTitle.text=model.title
        if(position==taskList.size-1){
            holder.tvAddTaskList.visibility=View.VISIBLE
            holder.llTaskItem.visibility=View.GONE
        }else{
            holder.tvAddTaskList.visibility=View.GONE
            holder.llTaskItem.visibility=View.VISIBLE
        }
        holder.tvAddTaskList.setOnClickListener {
            holder.tvAddTaskList.visibility=View.GONE
            holder.cvAddTaskListName.visibility=View.VISIBLE
        }
        holder.ibCloseListName.setOnClickListener{
            holder.tvAddTaskList.visibility=View.VISIBLE
            holder.cvAddTaskListName.visibility=View.INVISIBLE
        }
        holder.ibDoneListName.setOnClickListener {
            val title=holder.etTaskListName.text.toString()
            if(title.isNotEmpty()){
                if(context is TaskList){
                    context.createTaskList(title)
                }
            }else{
                Toast.makeText(context, "Please provide a valid list name", Toast.LENGTH_SHORT).show()
            }
        }
        holder.ibEditListName.setOnClickListener {
            holder.etEditTaskListName.setText(model.title)
            holder.cvEditTaskListName.visibility=View.VISIBLE
        }
        holder.ibCloseEditableView.setOnClickListener {
            holder.cvEditTaskListName.visibility=View.GONE
        }
        holder.ibDoneEditListName.setOnClickListener {
            val newTitle=holder.etEditTaskListName.text.toString()
            if(newTitle.isNotEmpty()){
                if(context is TaskList)
                    context.updateTaskList(newTitle,position,model)
            }else{
                Toast.makeText(context, "Please provide a valid list name", Toast.LENGTH_SHORT).show()
            }
        }
        holder.ibDeleteList.setOnClickListener {
            if(context is TaskList)
                    context.deleteTaskList(position)
        }
        holder.tvAddCard.setOnClickListener {
            holder.cvAddCard.visibility=View.VISIBLE
        }
        holder.ibCloseCardName.setOnClickListener {
            holder.cvAddCard.visibility=View.GONE
        }
        holder.ibDoneCardName.setOnClickListener {
            val cardName=holder.etCardName.text.toString()
            if(cardName.isNotEmpty()){
                if(context is TaskList)
                    context.uploadCard(position,cardName,model)
            }else{
                Toast.makeText(context, "Please provide a valid card name", Toast.LENGTH_SHORT).show()
            }
        }
        holder.rvCardList.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        val adapter=ItemCardAdapter(context,model.CardList)
        holder.rvCardList.adapter=adapter
        adapter.setOnItemClickListener(object:ItemCardAdapter.ItemListener{
            override fun onItemClickListener(cardPosition: Int) {
                if(context is TaskList)
                    context.cardDetails(cardPosition,position)
            }
        })
    }
    override fun getItemCount(): Int {
        return taskList.size
    }
}