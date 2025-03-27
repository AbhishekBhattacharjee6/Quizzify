package com.example.quizzify.Adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Fragments.EditQuestionFragment
import com.example.quizzify.Fragments.SavedCollectionQuestionList
import com.example.quizzify.R
import com.example.quizzify.datamodels.ARQuestionListModel
import com.example.quizzify.datamodels.OptionsModel
import com.google.android.material.card.MaterialCardView
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem

class SavedCollectionQuestionListAdapter (val frag:SavedCollectionQuestionList, var QuestionList:MutableList<ARQuestionListModel>):RecyclerView.Adapter<SavedCollectionQuestionListAdapter.MyViewHolder>(){
    private var stateofexpandedItem = MutableList(QuestionList.size) { false }
    private lateinit var OptionsAdapter:OptionsAdapter

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val Question=itemView.findViewById<TextView>(R.id.questionText)
        val QuestionNumber=itemView.findViewById<TextView>(R.id.questionNumberText)
        val EditBtn=itemView.findViewById<ImageButton>(R.id.editButton)
        val DelBtn=itemView.findViewById<ImageButton>(R.id.deleteButton)
        val ExpandBtn=itemView.findViewById<ImageButton>(R.id.expandButton)
        val QuestionCard=itemView.findViewById<MaterialCardView>(R.id.questionCard)
        val OptionRV=itemView.findViewById<RecyclerView>(R.id.optionsRecyclerView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavedCollectionQuestionListAdapter.MyViewHolder {
        val view= LayoutInflater.from(frag.requireContext()).inflate(R.layout.question_bank_item,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SavedCollectionQuestionListAdapter.MyViewHolder,
        position: Int
    ) {
        val OrginalPosition=QuestionList.indexOf(QuestionList[position])
        if(stateofexpandedItem[OrginalPosition]){
            holder.OptionRV.visibility = View.VISIBLE
            holder.ExpandBtn.animate().rotation(180f).setDuration(300).start()
        }
        else{
            holder.OptionRV.visibility = View.GONE
            holder.ExpandBtn.animate().rotation(0f).setDuration(300).start()
        }
        holder.QuestionNumber.text=(position+1).toString()
        val QuestionInfo=QuestionList[position]
        holder.Question.text=QuestionInfo.Question
        holder.DelBtn.setOnClickListener {
            frag.DeleteQuiz(QuestionList,position)
        }
        holder.OptionRV.layoutManager= LinearLayoutManager(frag.requireContext(), LinearLayoutManager.VERTICAL,false)
        val Options= listOf(
            OptionsModel(QuestionInfo.CorrectAnswer,true),
            OptionsModel(QuestionInfo.WrongAnswer1,false),
            OptionsModel(QuestionInfo.WrongAnswer2,false),
            OptionsModel(QuestionInfo.WrongAnswer3,false),

            )
        Options.shuffled()
        OptionsAdapter=OptionsAdapter(frag,Options.toMutableList())
        holder.OptionRV.adapter=OptionsAdapter
        holder.ExpandBtn.setOnClickListener {
            if(stateofexpandedItem[OrginalPosition]){
                val slideUp = AnimationUtils.loadAnimation(frag.requireContext(), R.anim.slide_up)
                slideUp.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        holder.OptionRV.visibility = View.GONE
                    }
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                holder.OptionRV.startAnimation(slideUp)
                holder.ExpandBtn.animate().rotation(0f).setDuration(300).start()
                stateofexpandedItem[OrginalPosition]=false
            }
            else{
                holder.OptionRV.visibility = View.VISIBLE
                Options.shuffled()
                OptionsAdapter.UpdateList(Options.toMutableList()) // Set the options
                holder.OptionRV.startAnimation(
                    AnimationUtils.loadAnimation(frag.requireContext(), R.anim.slide_down)
                )
                holder.ExpandBtn.animate().rotation(180f).setDuration(300).start()
                stateofexpandedItem[OrginalPosition]=true
            }
        }
        holder.DelBtn.setOnClickListener {
            frag.DeleteQuiz(QuestionList,position)
        }
        holder.EditBtn.visibility=View.GONE
    }

    override fun getItemCount(): Int {
     return QuestionList.size
    }

    fun UpdateList(newList: List<ARQuestionListModel>){
        QuestionList= newList.toMutableList()
        stateofexpandedItem = MutableList(QuestionList.size) { false }
        notifyDataSetChanged()
    }


}