package com.example.quizzify.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quizzify.BottomSheets.LeaderBoardBottomSheet
import com.example.quizzify.Fragments.ActiveRooms
import com.example.quizzify.R
import com.example.quizzify.datamodels.LeaderBoardModel
import com.google.android.material.imageview.ShapeableImageView
import de.hdodenhof.circleimageview.CircleImageView

class LeaderBoardAdapter(val frag:LeaderBoardBottomSheet, val LeaderBoardList:MutableList<LeaderBoardModel>):RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder>() {
    class LeaderBoardViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val Rank=itemView.findViewById<TextView>(R.id.rankText)
        val Name=itemView.findViewById<TextView>(R.id.nameText)
        val Image=itemView.findViewById<ShapeableImageView>(R.id.userImage)
        val Score=itemView.findViewById<TextView>(R.id.scoreText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardViewHolder {
       val inflater= LayoutInflater.from(frag.requireContext()).inflate(R.layout.item_leaderboard,parent,false)
        return LeaderBoardViewHolder(inflater)
    }

    override fun getItemCount(): Int {
       return LeaderBoardList.size
    }

    override fun onBindViewHolder(holder: LeaderBoardViewHolder, position: Int) {
        val OriginalPosition=LeaderBoardList.indexOf(LeaderBoardList[position])
        holder.Name.text=LeaderBoardList[position].Name
        holder.Score.text="${LeaderBoardList[position].Score}/${LeaderBoardList[position].TotalScore}"
        holder.Rank.text="${OriginalPosition+4}"
        if(LeaderBoardList[position].ImgUrl!="") {
            Glide.with(frag.requireContext()).load(LeaderBoardList[position].ImgUrl)
                .into(holder.Image)
        }
    }
    fun updateList(newList:MutableList<LeaderBoardModel>){
        LeaderBoardList.clear()
        LeaderBoardList.addAll(newList)
        notifyDataSetChanged()
    }

}