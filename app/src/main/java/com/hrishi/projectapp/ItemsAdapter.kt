package com.hrishi.projectapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hrishi.projectapp.databinding.BoardItemsLayoutBinding
import com.hrishi.projectapp.models.Board

class ItemsAdapter(val context: Context, private val boardList:ArrayList<Board>):RecyclerView.Adapter<ItemsAdapter.ViewHolder>(){
    private lateinit var mListener:ItemListener
    class ViewHolder(binding: BoardItemsLayoutBinding):RecyclerView.ViewHolder(binding.root){
        val img=binding.ivBoardImage
        val boardName=binding.tvName
        val createdBy=binding.tvCreatedBy
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        return ViewHolder(BoardItemsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=boardList[position]
        Glide.with(context).load(item.image).centerCrop().into(holder.img)
        holder.boardName.text=item.name
        holder.createdBy.text=item.createdBy

        holder.itemView.setOnClickListener {
            mListener.onItemClickListener(position,item)
        }
    }
    fun setOnItemClickListener(listener:ItemListener){
        mListener=listener
    }
    interface ItemListener{
        fun onItemClickListener(position:Int,model:Board)
    }

    override fun getItemCount(): Int {
        return boardList.size
    }

}