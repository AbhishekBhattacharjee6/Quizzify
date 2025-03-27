package com.example.quizzify.Adapters

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Fragments.CategoryFragment
import com.example.quizzify.Fragments.QuizQuestionFragment
import com.example.quizzify.R
import com.example.quizzify.datamodels.CategoryQuestionCountModel
import com.example.quizzify.datamodels.TriviaCategory
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider

class CategoryAdapter(val fragment:CategoryFragment, var categoryList:List<TriviaCategory>,val QuestionCountList:List<CategoryQuestionCountModel>,val ImgList:List<Int>): RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private lateinit var itemListener:itemClickListener
    private var originalList:List<TriviaCategory> = categoryList
    private var stateofexpandedItem = MutableList(categoryList.size) { false }
    private var stateofDifficultyChip=MutableList(categoryList.size){false}
    var SelectedDifficulty="Empty"
    var SelectedQuestion=1
    interface itemClickListener{
        fun onItemClick(position: Int,CategoryNum:Int)
    }
    fun setOnItemClickListener(listener: itemClickListener){
        itemListener=listener
    }
    class CategoryViewHolder(itemView: View,private val itemlistener: itemClickListener): RecyclerView.ViewHolder(itemView){
        val categoryName=itemView.findViewById<TextView>(R.id.categoryName)
        val categoryImg=itemView.findViewById<ImageView>(R.id.categoryImage)
        val QuestionsAvailable=itemView.findViewById<TextView>(R.id.questionCount)
        val ExpandableLayout=itemView.findViewById<LinearLayout>(R.id.headerLayout)
        val EasyButton=itemView.findViewById<MaterialButton>(R.id.easyButton)
        val MediumButton=itemView.findViewById<MaterialButton>(R.id.mediumButton)
        val HardButton=itemView.findViewById<MaterialButton>(R.id.hardButton)
        val QuestionCountSlider=itemView.findViewById<Slider>(R.id.questionCountSlider)
        val PracticeButton=itemView.findViewById<MaterialButton>(R.id.practiceButton)
        val QuestionCount=itemView.findViewById<TextView>(R.id.questionCountText)
        val ExpandableContent=itemView.findViewById<LinearLayout>(R.id.expandableContent)
            fun bind(CategoryNum:Int){
                itemView.setOnClickListener{
                    itemlistener.onItemClick(adapterPosition,CategoryNum)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view= LayoutInflater.from(fragment.requireContext()).inflate(R.layout.item_category,parent,false)
        return CategoryViewHolder(view,itemListener)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    fun updateList(newList:List<TriviaCategory>){
        this.categoryList=newList
        this.originalList=newList
        stateofexpandedItem= MutableList(categoryList.size) { false }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        RemoveColor(holder,"easy")
        RemoveColor(holder,"medium")
        RemoveColor(holder,"hard")
        var SelectedDifficulty="Empty"
        var SelectedQuestion=1
        if(QuestionCountList[position].expanded){
            holder.ExpandableContent.visibility=View.VISIBLE
        }
        else{
            holder.ExpandableContent.visibility=View.GONE
        }
        holder.categoryImg.setImageResource(ImgList[position])
        holder.categoryName.text=categoryList[position].name
        holder.QuestionsAvailable.text=QuestionCountList[position].total.toString()+" Questions"
        holder.QuestionCountSlider.value=(1).toFloat()
        holder.bind(categoryList[position].id)
        holder.EasyButton.setOnClickListener {
            UpdateColor(0,holder)
            RemoveColor(holder,SelectedDifficulty)
            SelectedDifficulty="easy"
            holder.QuestionsAvailable.text=QuestionCountList[position].easy.toString()+" Questions"
            holder.QuestionCountSlider.valueTo=QuestionCountList[position].easy.toFloat()
            SelectedQuestion=1
            holder.QuestionCountSlider.value=(1).toFloat()
            holder.QuestionCount.text=(1).toString()+" Question"
            Log.d("Difficulty","Easy")
        }
        holder.MediumButton.setOnClickListener{
            UpdateColor(1,holder)
            RemoveColor(holder,SelectedDifficulty)
            SelectedDifficulty="medium"
            holder.QuestionsAvailable.text=QuestionCountList[position].medium.toString()+" Questions"
            holder.QuestionCountSlider.valueTo=QuestionCountList[position].medium.toFloat()
            holder.QuestionCount.text=(1).toString()+" Question"
            SelectedQuestion=1
            holder.QuestionCountSlider.value=(1).toFloat()
            Log.d("Difficulty","Medium")
        }
        holder.HardButton.setOnClickListener{
            UpdateColor(2,holder)
            RemoveColor(holder,SelectedDifficulty)
            SelectedDifficulty="hard"
            holder.QuestionsAvailable.text=QuestionCountList[position].hard.toString()+" Questions"
            holder.QuestionCountSlider.valueTo=QuestionCountList[position].hard.toFloat()
            holder.QuestionCountSlider.value=(1).toFloat()
            SelectedQuestion=1
            holder.QuestionCount.text=(1).toString()+" Question"
            Log.d("Difficulty","Hard")
        }
        holder.ExpandableLayout.setOnClickListener {
            if(QuestionCountList[position].expanded){
                val slideUp = AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.slide_up)
                slideUp.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        holder.ExpandableContent.visibility = View.GONE
                    }
                    override fun onAnimationStart(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
                holder.ExpandableContent.startAnimation(slideUp)
                holder.QuestionsAvailable.text=QuestionCountList[position].total.toString()+" Questions"
                holder.QuestionCountSlider.value=(1).toFloat()
                SelectedDifficulty="Empty"
                SelectedQuestion=1
                QuestionCountList[position].expanded=false
                holder.QuestionCount.text=""
                holder.EasyButton.setBackgroundColor(Color.WHITE)
                holder.MediumButton.setBackgroundColor(Color.WHITE)
                holder.HardButton.setBackgroundColor(Color.WHITE)
                holder.EasyButton.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.easy_text_normal))
                holder.MediumButton.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.medium_text_normal))
               holder.HardButton.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.hard_text_normal))
            }
            else{
                holder.ExpandableContent.visibility = View.VISIBLE // Set the options
                holder.ExpandableContent.startAnimation(
                    AnimationUtils.loadAnimation(fragment.requireContext(), R.anim.slide_down)
                )
                QuestionCountList[position].expanded=true
            }
        }
        holder.QuestionCountSlider.addOnChangeListener { slider, value, fromUser ->
            SelectedQuestion=value.toInt()
            holder.QuestionCount.text=value.toInt().toString()+" Question"
            Log.d("QuestionCount",value.toInt().toString())
        }
        holder.PracticeButton.setOnClickListener {
            if(SelectedDifficulty=="Empty"){
                Toast.makeText(fragment.requireContext(),"Select a Difficulty",Toast.LENGTH_SHORT).show()
            }
            else{
                val Data=Bundle()
                Data.putInt("CategoryNum",categoryList[position].id)
                Data.putString("Difficulty",SelectedDifficulty)
                Data.putInt("QuestionCount",SelectedQuestion)
                fragment.EmptyUpandReplace(QuizQuestionFragment(),Data)
            }
        }
    }
    fun UpdateColor(difficulty:Int,holder:CategoryViewHolder){
        when(difficulty){
            0->{
                holder.EasyButton.setBackgroundColor(ContextCompat.getColor(fragment.requireContext(), R.color.easy_selected))
                holder.EasyButton.setTextColor(Color.WHITE)
            }
            1->{
                holder.MediumButton.setBackgroundColor(ContextCompat.getColor(fragment.requireContext(), R.color.medium_selected))
                holder.MediumButton.setTextColor(Color.WHITE)
            }
            2->{
                holder.HardButton.setBackgroundColor(ContextCompat.getColor(fragment.requireContext(), R.color.hard_selected))
                holder.HardButton.setTextColor(Color.WHITE)
            }
        }

    }
    fun RemoveColor(holder: CategoryViewHolder,difficulty: String){
        when(difficulty){
            "easy"->{
                holder.EasyButton.setBackgroundColor(Color.WHITE)
                holder.EasyButton.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.easy_text_normal))
            }
            "medium"->{
                holder.MediumButton.setBackgroundColor(Color.WHITE)
                holder.MediumButton.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.medium_text_normal))
            }
            "hard"->{
                holder.HardButton.setBackgroundColor(Color.WHITE)
                holder.HardButton.setTextColor(ContextCompat.getColor(fragment.requireContext(), R.color.hard_text_normal))
            }
        }
    }
}