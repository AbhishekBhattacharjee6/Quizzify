package com.example.quizzify.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.R
import com.example.quizzify.datamodels.QuizSetModel

class SearchRoomAdapter(val fragment:Fragment,var SearchResList:List<QuizSetModel>) :RecyclerView.Adapter<SearchRoomAdapter.MyViewHolder>(){
    private lateinit var itemListener:itemClickListener
    interface itemClickListener{
        fun onItemClick(position:Int,RoomName:String,RoomCreatorName:String,PassCode:String,StartFrom:String,ValidTill:String,RoomId:String,Duration: String)
    }
    fun setOnItemClickListener(listener:itemClickListener){
        itemListener=listener
    }
    class MyViewHolder(val itemView: View,private val itemlistener:itemClickListener):RecyclerView.ViewHolder(itemView){
        val RoomName=itemView.findViewById<TextView>(R.id.textView7)
        val RoomCreatorName=itemView.findViewById<TextView>(R.id.textView8)
        fun bind(RoomName:String,RoomCreatorName:String,PassCode:String,StartFrom:String,ValidTill:String,RoomId:String,Duration:String){
            itemView.setOnClickListener {
                itemlistener.onItemClick(adapterPosition,RoomName,RoomCreatorName,PassCode,StartFrom,ValidTill,RoomId,Duration)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(fragment.requireContext()).inflate(R.layout.search_room_view,parent,false)
        return MyViewHolder(view,itemListener)
    }

    override fun getItemCount(): Int {
       return SearchResList.size
    }

    fun updateList(newList:List<QuizSetModel>){
        SearchResList=newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.RoomName.text=SearchResList[position].RoomName
        holder.RoomCreatorName.text=SearchResList[position].CreatorUID
        holder.bind(SearchResList[position].RoomName,SearchResList[position].CreatorUID,SearchResList[position].PassCode,SearchResList[position].StartFrom,SearchResList[position].ValidTill,SearchResList[position].QuizSetId,SearchResList[position].Duration)
    }

}