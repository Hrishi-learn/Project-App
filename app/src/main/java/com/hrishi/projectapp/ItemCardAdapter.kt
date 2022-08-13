package com.hrishi.projectapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hrishi.projectapp.databinding.ItemcardBinding
import com.hrishi.projectapp.models.Board
import com.hrishi.projectapp.models.Card

class ItemCardAdapter(val context: Context,var cardList:ArrayList<Card>):RecyclerView.Adapter<ItemCardAdapter.ViewHolder>(){
    private lateinit var mListener:ItemListener
    class ViewHolder(binding:ItemcardBinding):RecyclerView.ViewHolder(binding.root){
        val tvCardName=binding.tvCardName
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemcardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card=cardList[position]
        holder.tvCardName.text=card.name
        holder.itemView.setOnClickListener {
            mListener.onItemClickListener(position)
        }
    }
    override fun getItemCount(): Int {
        return cardList.size
    }
    fun setOnItemClickListener(listener:ItemListener){
        mListener=listener
    }
    interface ItemListener{
        fun onItemClickListener(position:Int)
    }
}