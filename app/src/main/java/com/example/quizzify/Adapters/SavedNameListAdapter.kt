package com.example.quizzify.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Fragments.QuizQuestionFragment
import com.example.quizzify.R

class SavedNameListAdapter(val fragment:QuizQuestionFragment,var savedNameList:List<String>):RecyclerView.Adapter<SavedNameListAdapter.ViewHolder> (){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ListName=itemView.findViewById<TextView>(R.id.textView42)
        val ChechBox=itemView.findViewById<CheckBox>(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(fragment.requireContext()).inflate(R.layout.each_list,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return savedNameList.size
    }
    fun updateList(newList:List<String>){
        savedNameList=newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ListName.text=savedNameList[position].toString()
    }
}