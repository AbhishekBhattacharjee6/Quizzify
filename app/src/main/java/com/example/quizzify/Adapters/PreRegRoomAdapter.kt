package com.example.quizzify.Adapters

import android.icu.util.Calendar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.quizzify.R
import com.example.quizzify.datamodels.QuizSetModel

class PreRegRoomAdapter(val frag:Fragment,var PreRegRoomList:List<QuizSetModel>):RecyclerView.Adapter<PreRegRoomAdapter.ViewHolder>() {
    private lateinit var ItemListener:itemListener
    interface itemListener{
        fun onItemClick(position: Int,StartFrom:String,ValidTill:String,Duration:String,RoomName:String,CreatorUID:String,TotalQuestions:String,QuizSetID:String)
    }
    fun setOnItemClickListener(listener:itemListener){
        ItemListener=listener
    }
    class ViewHolder(val itemView:View,private val itemListener:itemListener):RecyclerView.ViewHolder(itemView){
        val roomName=itemView.findViewById<TextView>(R.id.textView18)
        val roomCreatorName=itemView.findViewById<TextView>(R.id.textView19)
        val QuizLive=itemView.findViewById<LottieAnimationView>(R.id.QuizLive)
        fun bind(StartFrom:String,ValidTill:String,Duration:String,RoomName:String,CreatorUID:String,TotalQuestions: String,QuizSetID: String){
            itemView.setOnClickListener {
                itemListener.onItemClick(adapterPosition,StartFrom,ValidTill,Duration,RoomName,CreatorUID,TotalQuestions,QuizSetID)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(frag.requireContext()).inflate(R.layout.pre_reg_room_view,parent,false)
        return ViewHolder(view,ItemListener)
    }

    override fun getItemCount(): Int {
        Log.d("Size",PreRegRoomList.size.toString())
        return PreRegRoomList.size
    }

    fun updateList(newList:List<QuizSetModel>){
        PreRegRoomList=newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val PreRegRoomInfo=PreRegRoomList[position]
        holder.roomName.text= PreRegRoomList[position].RoomName
        holder.roomCreatorName.text=PreRegRoomList[position].CreatorUID
        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)+1
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val date=year.toString()+month.toString()+day.toString()
        if(date>=PreRegRoomList[position].StartFrom && date<=PreRegRoomList[position].ValidTill){
            holder.QuizLive.visibility=View.VISIBLE
        }
        else{
            holder.QuizLive.visibility=View.GONE
        }
        holder.bind(PreRegRoomInfo.StartFrom,PreRegRoomInfo.ValidTill,PreRegRoomInfo.Duration,PreRegRoomInfo.RoomName,PreRegRoomInfo.CreatorUID,PreRegRoomList.size.toString(),PreRegRoomInfo.QuizSetId)
    }
}