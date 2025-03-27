package com.example.quizzify.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quizzify.Fragments.PreRegisteredRooms
import com.example.quizzify.R
import com.example.quizzify.datamodels.LeaderBoardModel
import de.hdodenhof.circleimageview.CircleImageView

class LeaderBoardPreRegAdapter(val frag:PreRegisteredRooms,val LeaderBoardList:MutableList<LeaderBoardModel>):RecyclerView.Adapter<LeaderBoardPreRegAdapter.ViewHolder> (){
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val position=itemView.findViewById<TextView>(R.id.textView46)
        val name=itemView.findViewById<TextView>(R.id.textView50)
        val score=itemView.findViewById<TextView>(R.id.textView51)
        val profilimg=itemView.findViewById<CircleImageView>(R.id.profile_image)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeaderBoardPreRegAdapter.ViewHolder {
        val inflater= LayoutInflater.from(frag.requireContext()).inflate(R.layout.ld_each_item,parent,false)
        return LeaderBoardPreRegAdapter.ViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: LeaderBoardPreRegAdapter.ViewHolder, position: Int) {
        LeaderBoardList.sortByDescending { it.Score }  // Sorts in-place
        holder.position.text=(position+1).toString()
        holder.name.text=LeaderBoardList[position].Name
        val Score="${LeaderBoardList[position].Score}/${LeaderBoardList[position].TotalScore}"
        holder.score.text=Score
        if(LeaderBoardList[position].ImgUrl!="Empty")
            Glide.with(frag.requireContext()).load(LeaderBoardList[position].ImgUrl).into(holder.profilimg)
    }

    override fun getItemCount(): Int {
        return LeaderBoardList.size
    }

    fun updateList(newList:MutableList<LeaderBoardModel>){
        LeaderBoardList.clear()
        LeaderBoardList.addAll(newList)
        notifyDataSetChanged()
    }


}