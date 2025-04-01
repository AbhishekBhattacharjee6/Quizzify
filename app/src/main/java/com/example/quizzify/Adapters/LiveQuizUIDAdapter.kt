package com.example.quizzify.Adapters

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.BottomSheets.LeaderBoardBottomSheet
import com.example.quizzify.Fragments.ActiveRoomQuestionList
import com.example.quizzify.Fragments.ActiveRooms
import com.example.quizzify.R
import com.example.quizzify.datamodels.RoomSetModel
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Locale

class LiveQuizUIDAdapter(val frag:ActiveRooms,var QuizSetList:List<RoomSetModel>):RecyclerView.Adapter<LiveQuizUIDAdapter.ViewHolder>(){


    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val QuizSetName=itemView.findViewById<TextView>(R.id.tvContestName)
        val EditBtn=itemView.findViewById<Button>(R.id.btnEditContest)
        val DeleteBtn=itemView.findViewById<Button>(R.id.btnDelete)
        val LeaderBoard=itemView.findViewById<Button>(R.id.btnLeaderboard)
        val Date=itemView.findViewById<TextView>(R.id.tvDate)
        val Duration=itemView.findViewById<TextView>(R.id.tvDuration)
        val RoomId=itemView.findViewById<TextView>(R.id.tvRoomId)
        val Status=itemView.findViewById<Chip>(R.id.statusChip)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(frag.requireContext()).inflate(R.layout.contest_card_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return QuizSetList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        var month=calendar.get(Calendar.MONTH)+1
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        var new_month=(month).toString().padStart(2,'0')
        var new_day=(day).toString().padStart(2,'0')
        val date=year.toString()+new_month+new_day
        holder.QuizSetName.text=QuizSetList[position].RoomName
        holder.RoomId.text=QuizSetList[position].QuizSetUid.take(15)
       holder.Duration.text=" "+QuizSetList[position].Duration+" Minutes"
        val StartDate=formatDate(QuizSetList[position].StartFrom)
        val EndDate=formatDate(QuizSetList[position].ValidTill)
        holder.Date.text=" "+StartDate+" - "+EndDate

        holder.EditBtn.setOnClickListener {
            if (date.toInt()<QuizSetList[position].StartFrom.toInt()){
                val Data= Bundle()
                Data.putString("QuizSetId",QuizSetList[position].QuizSetUid)
                frag.ReplaceFrag(ActiveRoomQuestionList(),Data)
            }
        }
        holder.DeleteBtn.setOnClickListener {
            if(date.toInt()<QuizSetList[position].StartFrom.toInt())
            frag.DeleteQuizSet(QuizSetList[position].QuizSetUid,QuizSetList,position)
            else
                Toast.makeText(frag.requireContext(),"Will be Deleted after Contest End Date",Toast.LENGTH_SHORT).show()
        }
        holder.LeaderBoard.setOnClickListener{
            if(date.toInt()>=QuizSetList[position].StartFrom.toInt() && date.toInt()<=QuizSetList[position].ValidTill.toInt()) {
                val Dialog =
                    LeaderBoardBottomSheet.newInstance(
                        QuizSetList[position].QuizSetUid,
                        QuizSetList[position].RoomName
                    )
                Dialog.show(frag.childFragmentManager, "LeaderBoard")
            }
            else{
                Toast.makeText(frag.requireContext(),"Room yet to go LIVE",Toast.LENGTH_SHORT).show()
            }
        }
        if(date.toInt()>=QuizSetList[position].StartFrom.toInt() && date.toInt()<=QuizSetList[position].ValidTill.toInt()){
            holder.Status.text="LIVE"
        }
        else if(date.toInt()<QuizSetList[position].StartFrom.toInt()){
            holder.Status.text="UPCOMING"
        }
        else{
            holder.Status.text="EXPIRED"
        }
        holder.LeaderBoard.setOnClickListener {
            val Dialog=LeaderBoardBottomSheet.newInstance(QuizSetList[position].QuizSetUid,QuizSetList[position].RoomName)
            Dialog.show(frag.childFragmentManager,"LeaderBoard")
        }
    }

    fun updateList(newList:List<RoomSetModel>){
        QuizSetList=newList
        notifyDataSetChanged()
    }

    fun formatDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }
}