package com.example.quizzify.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.R

class LiveQuizUIDAdapter(val frag:Fragment,var QuizSetList:List<String>):RecyclerView.Adapter<LiveQuizUIDAdapter.ViewHolder>(){

   private lateinit var itemListener:QuizSetListener
    interface QuizSetListener{
        fun onItemClick(position: Int,QuizSetId:String)
    }

    fun onItemClickListener(listener:QuizSetListener){
        itemListener=listener
    }

    class ViewHolder(itemView:View,private val itemListener:QuizSetListener):RecyclerView.ViewHolder(itemView){
        val QuizSetName=itemView.findViewById<TextView>(R.id.QuizSetName)
        val EditBtn=itemView.findViewById<TextView>(R.id.EditBtn)
        fun bind(QuizSetId:String){
            EditBtn.setOnClickListener{
                itemListener.onItemClick(adapterPosition,QuizSetId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(frag.requireContext()).inflate(R.layout.uid_rv_view,parent,false)
        return ViewHolder(view,itemListener)
    }

    override fun getItemCount(): Int {
        return QuizSetList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.QuizSetName.text=QuizSetList[position].toString()
        holder.bind(QuizSetList[position])
    }

    fun updateList(newList:List<String>){
        QuizSetList=newList
        notifyDataSetChanged()
    }
}