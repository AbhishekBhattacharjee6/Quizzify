package com.example.quizzify.Adapters

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.BottomSheets.RecentContestBottomSheet
import com.example.quizzify.Fragments.ProfileFragment
import com.example.quizzify.R
import com.example.quizzify.datamodels.RecentContestDataModel


class RecentContestAdapter(val frag:ProfileFragment, var contestList:MutableList<RecentContestDataModel>): RecyclerView.Adapter<RecentContestAdapter.ViewHolder>(){
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ContestName=itemView.findViewById<TextView>(R.id.contestNameTextView)
        val ContestDetails=itemView.findViewById<TextView>(R.id.contestDetailsTextView)
        val Rank=itemView.findViewById<TextView>(R.id.positionTextView)
        val Score=itemView.findViewById<TextView>(R.id.scoreTextView)
        val Expand=itemView.findViewById<ImageView>(R.id.chevronImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view= LayoutInflater.from(frag.requireContext()).inflate(R.layout.item_recent_contest,parent,false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return contestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ContestDetails.text=contestList[position].RoomName
        holder.Score.text=contestList[position].Correct.toString()+"/"+contestList[position].Total.toString()
        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)+1
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val new_month=(month).toString().padStart(2,'0')
        val new_day=(day).toString().padStart(2,'0')
        val date=year.toString()+new_month.toString()+new_day.toString()
        if(date.toInt()<=contestList[position].Validity.toInt()){
            holder.ContestDetails.text=(contestList[position].Validity.toInt()-date.toInt()).toString()+" Days to go"
        }
        else{
            holder.ContestDetails.text=(date.toInt()-contestList[position].Validity.toInt()).toString()+" Days ago"
        }
        holder.Rank.text="#0"
        val Unanswered=contestList[position].Total-(contestList[position].Correct+contestList[position].Wrong)
        holder.Expand.setOnClickListener {
            val RecentSheet=RecentContestBottomSheet.newInstance(contestList[position].RoomName,contestList[position].RoomID.take(8),contestList[position].Total.toString(),contestList[position].Correct.toString(),contestList[position].Wrong.toString(),Unanswered.toString())
            RecentSheet.show(frag.parentFragmentManager,"RecentContestBottomSheet")
        }
    }
    fun UpdateRV(newList:MutableList<RecentContestDataModel>){
        contestList=newList
        notifyDataSetChanged()
    }
}