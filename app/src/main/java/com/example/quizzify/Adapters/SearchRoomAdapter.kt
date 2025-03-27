package com.example.quizzify.Adapters

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.R
import com.example.quizzify.datamodels.QuizSetModel
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Locale

class SearchRoomAdapter(val fragment:Fragment,var SearchResList:List<QuizSetModel>) :RecyclerView.Adapter<SearchRoomAdapter.MyViewHolder>(){
    private lateinit var itemListener:itemClickListener
    interface itemClickListener{
        fun onItemClick(position:Int,RoomName:String,RoomCreatorName:String,PassCode:String,StartFrom:String,ValidTill:String,RoomId:String,Duration: String,QuizSetID: String,Attempted:Boolean,SaveAllowed:Boolean)
    }
    fun setOnItemClickListener(listener:itemClickListener){
        itemListener=listener
    }
    class MyViewHolder(val itemView: View,private val itemlistener:itemClickListener):RecyclerView.ViewHolder(itemView){
        val RoomName=itemView.findViewById<TextView>(R.id.roomNameText)
        val RoomCreatorName=itemView.findViewById<TextView>(R.id.authorNameText)
        val JoinBtn=itemView.findViewById<MaterialButton>(R.id.joinButton)
        val StartDate=itemView.findViewById<TextView>(R.id.startDateText)
        val EndDate=itemView.findViewById<TextView>(R.id.endDateText)
        val Duration=itemView.findViewById<TextView>(R.id.durationText)
        val StatusChip=itemView.findViewById<TextView>(R.id.statusChip)
        fun bind(RoomName:String,RoomCreatorName:String,PassCode:String,StartFrom:String,ValidTill:String,RoomId:String,Duration:String,QuizSetID:String,Attempted:Boolean,SaveAllowed:Boolean){
            JoinBtn.setOnClickListener {
                itemlistener.onItemClick(adapterPosition,RoomName,RoomCreatorName,PassCode,StartFrom,ValidTill,RoomId,Duration,QuizSetID,Attempted,SaveAllowed)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(fragment.requireContext()).inflate(R.layout.search_item,parent,false)
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
        holder.Duration.text=" "+SearchResList[position].Duration+" Minutes"
        holder.StartDate.text=formatDate(SearchResList[position].StartFrom)
        holder.EndDate.text=formatDate(SearchResList[position].ValidTill)
        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        var month=calendar.get(Calendar.MONTH)+1
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        var new_month=(month).toString().padStart(2,'0')
        var new_day=(day).toString().padStart(2,'0')
        val date=year.toString()+new_month+new_day
        if(date<SearchResList[position].StartFrom){
            holder.StatusChip.text="UPCOMING"
        }
        else if(date>SearchResList[position].ValidTill){
            holder.StatusChip.text="EXPIRED"
        }
        else{
            holder.StatusChip.text="LIVE"
        }
        holder.bind(SearchResList[position].RoomName,SearchResList[position].CreatorUID,SearchResList[position].PassCode,SearchResList[position].StartFrom,SearchResList[position].ValidTill,SearchResList[position].QuizSetId,SearchResList[position].Duration,SearchResList[position].QuizSetId,SearchResList[position].Attempted,SearchResList[position].SaveAllowed)
    }
    fun formatDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }

}