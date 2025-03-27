package com.example.quizzify.Adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.quizzify.BottomSheets.LeaderBoardBottomSheet
import com.example.quizzify.Fragments.EditQuestionFragment
import com.example.quizzify.Fragments.PreRegisteredRooms
import com.example.quizzify.R
import com.example.quizzify.datamodels.QuizSetModel
import com.google.firebase.firestore.FirebaseFirestore
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import kotlinx.coroutines.tasks.await

class PreRegRoomAdapter(val frag:PreRegisteredRooms,var PreRegRoomList:MutableList<QuizSetModel>):RecyclerView.Adapter<PreRegRoomAdapter.ViewHolder>() {
    private lateinit var ItemListener:itemListener
    interface itemListener{
        fun onItemClick(position: Int,StartFrom:String,ValidTill:String,Duration:String,RoomName:String,CreatorUID:String,TotalQuestions:String,QuizSetID:String,Attempted:Boolean,SaveAllowed:Boolean)
    }
    fun setOnItemClickListener(listener:itemListener){
        ItemListener=listener
    }
    class ViewHolder(val itemView:View,private val itemListener:itemListener):RecyclerView.ViewHolder(itemView){
        val roomName=itemView.findViewById<TextView>(R.id.tvRoomName)
        val roomCreatorName=itemView.findViewById<TextView>(R.id.tvCreator)
        val JoinBtn=itemView.findViewById<Button>(R.id.btnJoin)
        val LeaderBoard=itemView.findViewById<ImageButton>(R.id.btnLeaderboard)
        val StartTime=itemView.findViewById<TextView>(R.id.tvStartTime)
        val Participants=itemView.findViewById<TextView>(R.id.tvParticipants)
        val StatusDot=itemView.findViewById<View>(R.id.statusDot)
        val StatusText=itemView.findViewById<TextView>(R.id.tvStatus)
        val Room=itemView.findViewById<ConstraintLayout>(R.id.roomCard)
        val DelBtn=itemView.findViewById<ImageButton>(R.id.btnDelete)
        fun bind(StartFrom:String,ValidTill:String,Duration:String,RoomName:String,CreatorUID:String,TotalQuestions: String,QuizSetID: String,Attempted:Boolean,SaveAllowed:Boolean){
//            JoinBtn.setOnClickListener {
//                //itemListener.onItemClick(adapterPosition,StartFrom,ValidTill,Duration,RoomName,CreatorUID,TotalQuestions,QuizSetID,Attempted,SaveAllowed)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(frag.requireContext()).inflate(R.layout.contest_room_item,parent,false)
        return ViewHolder(view,ItemListener)
    }

    override fun getItemCount(): Int {
        Log.d("Size",PreRegRoomList.size.toString())
        return PreRegRoomList.size
    }

    fun updateList(newList:MutableList<QuizSetModel>){
        PreRegRoomList=newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val PreRegRoomInfo = PreRegRoomList[position]
        holder.roomName.text = PreRegRoomList[position].RoomName
        holder.roomCreatorName.text = "Created by " + PreRegRoomList[position].CreatorUID
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val new_month = (month).toString().padStart(2, '0')
        val new_day = (day).toString().padStart(2, '0')
        val date = year.toString() + new_month.toString() + new_day.toString()
        holder.Participants.text = " 50 Participants"
        if (date >= PreRegRoomList[position].StartFrom && date <= PreRegRoomList[position].ValidTill) {
            holder.Room.setBackgroundResource(R.drawable.new_room_gradient_bg)
            holder.StartTime.text = "Room Is Live"
            holder.StatusDot.setBackgroundResource(R.drawable.status_dot_background)
            holder.StatusText.text = "LIVE"
        } else if (date < PreRegRoomList[position].StartFrom) {
            holder.Room.setBackgroundResource(R.drawable.bg_card_gradient_blue)
            holder.StartTime.text =
                "Starts in " + (PreRegRoomInfo.StartFrom.toInt() - date.toInt()) + " days"
            holder.StatusDot.setBackgroundResource(R.drawable.status_dot_background_2)
            holder.StatusText.text = "UPCOMING"
        } else {
            holder.Room.setBackgroundResource(R.drawable.bg_card_gradient_grey)
            holder.StatusDot.setBackgroundResource(R.drawable.status_dot_background_3)
            holder.StartTime.text = "Room Has Expired"
            holder.StatusText.text = "EXPIRED"
        }

        if (date >= PreRegRoomList[position].StartFrom && date <= PreRegRoomList[position].ValidTill) {
            holder.JoinBtn.text = "JOIN NOW"
            holder.JoinBtn.isClickable = true
        } else if (date < PreRegRoomList[position].StartFrom) {
            holder.JoinBtn.text = "UPCOMING"
            holder.JoinBtn.isClickable = false
        } else {
            holder.JoinBtn.text = "EXPIRED"
            holder.JoinBtn.isClickable = false
        }
        holder.DelBtn.setOnClickListener {
            frag.DeletePreRegRoom(position, PreRegRoomList)
        }
        holder.LeaderBoard.setOnClickListener {
            val Dialog =
                LeaderBoardBottomSheet.newInstance(
                    PreRegRoomList[position].QuizSetId,
                    PreRegRoomList[position].RoomName
                )
            Dialog.show(frag.childFragmentManager, "LeaderBoard")
        }
        holder.JoinBtn.setOnClickListener {
            if (date >= PreRegRoomList[position].StartFrom && date <= PreRegRoomList[position].ValidTill) {
                FirebaseFirestore.getInstance().collection("Quizset")
                    .document(PreRegRoomList[position].QuizSetId).get().addOnSuccessListener {
                    if (it.exists()) {
                        frag.GoLive(
                            PreRegRoomInfo.StartFrom,
                            PreRegRoomInfo.ValidTill,
                            PreRegRoomInfo.Duration,
                            PreRegRoomInfo.RoomName,
                            PreRegRoomInfo.CreatorUID,
                            PreRegRoomList.size.toString(),
                            PreRegRoomInfo.QuizSetId,
                            PreRegRoomInfo.Attempted,
                            PreRegRoomInfo.SaveAllowed
                        )
                    } else
                        Toast.makeText(
                            frag.requireContext(),
                            "Contest Doesn't Exist",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
            holder.bind(
                PreRegRoomInfo.StartFrom,
                PreRegRoomInfo.ValidTill,
                PreRegRoomInfo.Duration,
                PreRegRoomInfo.RoomName,
                PreRegRoomInfo.CreatorUID,
                PreRegRoomList.size.toString(),
                PreRegRoomInfo.QuizSetId,
                PreRegRoomInfo.Attempted,
                PreRegRoomInfo.SaveAllowed
            )
        }
    }
}