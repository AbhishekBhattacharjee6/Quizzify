package com.example.quizzify.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Fragments.ActiveRoomQuestionList
import com.example.quizzify.R
import com.example.quizzify.datamodels.OptionsModel

class OptionsAdapter(var frag: Fragment, var OptionsList:MutableList<OptionsModel>):
    RecyclerView.Adapter<OptionsAdapter.OptionsViewHolder>(){
    class OptionsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val Checkbox=itemView.findViewById<CheckBox>(R.id.optionCheckbox)
        val OptionText=itemView.findViewById<TextView>(R.id.optionText)
        val CorrectAnswer=itemView.findViewById<TextView>(R.id.correctAnswerChip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionsViewHolder {
        val view= LayoutInflater.from(frag.requireContext()).inflate(R.layout.question_option_item,parent,false)
        return OptionsViewHolder(view)
    }

    override fun getItemCount(): Int {
       return OptionsList.size
    }
    fun UpdateList(newList:MutableList<OptionsModel>){
        OptionsList=newList
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: OptionsViewHolder, position: Int) {
        holder.OptionText.text=OptionsList[position].Option
        if(OptionsList[position].Correct){
            holder.CorrectAnswer.visibility=View.VISIBLE
            holder.Checkbox.isChecked=true
        }
        else{
            holder.CorrectAnswer.visibility=View.GONE
            holder.Checkbox.isChecked=false
        }
    }
}