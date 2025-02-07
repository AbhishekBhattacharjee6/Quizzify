package com.example.quizzify.Adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Fragments.ActiveRoomQuestionList
import com.example.quizzify.Fragments.EditQuestionFragment
import com.example.quizzify.R
import com.example.quizzify.datamodels.ARQuestionListModel

class QuestionListAdapter(val frag:ActiveRoomQuestionList, var QuestionList:MutableList<ARQuestionListModel>):RecyclerView.Adapter<QuestionListAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val Question=itemView.findViewById<TextView>(R.id.textView41)
        val MoreOptions=itemView.findViewById<ImageView>(R.id.MoreOptions)
        val CorrectAnswer=itemView.findViewById<TextView>(R.id.optionA)
        val Option1=itemView.findViewById<TextView>(R.id.optionB)
        val Option2=itemView.findViewById<TextView>(R.id.optionC)
        val Option3=itemView.findViewById<TextView>(R.id.optionD)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view=LayoutInflater.from(frag.requireContext()).inflate(R.layout.question_list_rv,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return QuestionList.size
    }

    fun UpdateList(newList: List<ARQuestionListModel>){
        QuestionList= newList.toMutableList()
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val QuestionInfo=QuestionList[position]
        holder.Question.text=QuestionInfo.Question
        holder.CorrectAnswer.text=QuestionInfo.CorrectAnswer
        holder.Option1.text=QuestionInfo.WrongAnswer1
        holder.Option2.text=QuestionInfo.WrongAnswer2
        holder.Option3.text=QuestionInfo.WrongAnswer3
    }

}