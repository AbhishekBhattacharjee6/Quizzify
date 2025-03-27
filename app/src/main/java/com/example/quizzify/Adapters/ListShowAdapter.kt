package com.example.quizzify.Adapters

import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.BottomSheets.SavedListBottomSheet
import com.example.quizzify.Fragments.LiveContest
import com.example.quizzify.R
import com.example.quizzify.SimpletonObjects.SaveListQuestionTrack
import com.example.quizzify.datamodels.SavedCollectionModel

class ListShowAdapter(val frag:SavedListBottomSheet,var savedNameList:MutableList<SavedCollectionModel>,val QuestionID:String,val QuestionNumber:Int):RecyclerView.Adapter<ListShowAdapter.ListViewHolder>() {

    class ListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val ListName=itemView.findViewById<TextView>(R.id.listNameText)
        val isPrivate=itemView.findViewById<ImageView>(R.id.lockIcon)
        val AddBtn=itemView.findViewById<ImageView>(R.id.addButton)
        val AuthorText=itemView.findViewById<TextView>(R.id.authorText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view= LayoutInflater.from(frag.requireContext()).inflate(R.layout.sl_each,parent,false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return savedNameList.size
    }
    fun updateList(newList:List<SavedCollectionModel>){
        savedNameList=newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentPos=savedNameList.indexOf(savedNameList[position])
        if(SaveListQuestionTrack.matrix?.get(currentPos)?.get(QuestionNumber)==1){
            holder.AddBtn.setImageResource(R.drawable.ic_check_circle)
            holder.AddBtn.isClickable=false
        }
        else{
            holder.AddBtn.setImageResource(R.drawable.ic_add)
        }
        val currentItem=savedNameList[position]
        holder.ListName.text=currentItem.TitleName
        holder.AuthorText.text="by "+currentItem.Author+" • "+currentItem.QuestionIDs.size.toString()+" Questions"
        if(currentItem.isPrivate){
            holder.isPrivate.setImageResource(R.drawable.ic_lock)
        }
        else{
            holder.isPrivate.setImageResource(R.drawable.ic_globe)
        }
        holder.AddBtn.setOnClickListener {
            if(SaveListQuestionTrack.matrix?.get(currentPos)?.get(QuestionNumber)==0){
                frag.AddQuestion(savedNameList,QuestionID,savedNameList[position])
                SaveListQuestionTrack.matrix?.get(currentPos)?.set(QuestionNumber,1)
                holder.AddBtn.setImageResource(R.drawable.ic_check_circle)
                holder.AddBtn.isClickable=false
            }
        }
    }
}