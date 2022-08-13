package com.hrishi.projectapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hrishi.projectapp.databinding.ItemMemberBinding

class ItemMemberAdapter(val context: Context, var memberList: ArrayList<com.hrishi.projectapp.models.User>):RecyclerView.Adapter<ItemMemberAdapter.ViewHolder>(){
    class ViewHolder(binding:ItemMemberBinding):RecyclerView.ViewHolder(binding.root){
        val ivMemberImage=binding.ivMemberImage
        val tvMemberName=binding.tvMemberName
        val tvMemberEmail=binding.tvMemberEmail
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member=memberList[position]
        holder.tvMemberName.text=member.name
        holder.tvMemberEmail.text=member.email
        Glide
            .with(context)
            .load(member.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.ivMemberImage);
    }
    override fun getItemCount(): Int {
        return memberList.size
    }
}