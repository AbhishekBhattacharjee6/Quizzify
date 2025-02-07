package com.example.quizzify.Adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Fragments.FinalResultFragment
import com.example.quizzify.Fragments.LiveContest
import com.example.quizzify.R
import com.example.quizzify.datamodels.QuestionModel

class LiveContestAdapter(val frag:LiveContest, var QuestionList:List<QuestionModel>):RecyclerView.Adapter<LiveContestAdapter.ViewHolder>() {

    var QuestionState: MutableList<Int> = MutableList(QuestionList.size) { 0 }
    var UserAnswers:MutableList<Int> = MutableList(QuestionList.size){0}

    val totalQuestions=QuestionList.size
    var correctAnswersGiven=0
    var wrongAnswersGiven=0

    class ViewHolder(val itemView:View):RecyclerView.ViewHolder(itemView){
        val Question=itemView.findViewById<TextView>(R.id.textView20)
        val Option1=itemView.findViewById<TextView>(R.id.textView21)
        val Option2=itemView.findViewById<TextView>(R.id.textView22)
        val Option3=itemView.findViewById<TextView>(R.id.textView23)
        val Option4=itemView.findViewById<TextView>(R.id.textView24)
        val SubmitBtn=itemView.findViewById<Button>(R.id.button8)
        val prevBtn=itemView.findViewById<Button>(R.id.imageButton4)
        val nextBtn=itemView.findViewById<Button>(R.id.imageButton5)
        val endTest=itemView.findViewById<Button>(R.id.endbtn)
        val save=itemView.findViewById<ImageView>(R.id.bkmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=LayoutInflater.from(frag.requireContext()).inflate(R.layout.live_test_ui,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return QuestionList.size
    }
    fun updateList(newList:List<QuestionModel>){
        this.QuestionList=newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(QuestionList.isEmpty())
            return
        val QuestionInfo=QuestionList[position]
        holder.Question.text=QuestionInfo.Question
        var CorrectAnswerIndex=-1;
        var SelectedIndex=-1;
        var Attempted=false
        var allOptions= mutableListOf<String>()
        allOptions.add(QuestionInfo.WrongAnswer1)
        allOptions.add(QuestionInfo.WrongAnswer2)
        allOptions.add(QuestionInfo.WrongAnswer3)
        allOptions.add(QuestionInfo.CorrectAnswer)
        allOptions.shuffle()
        for(i in allOptions){
            if(i==QuestionInfo.CorrectAnswer){
                CorrectAnswerIndex=allOptions.indexOf(i)
                break
            }
        }
        holder.Option1.text=allOptions[0]
        holder.Option2.text=allOptions[1]
        holder.Option3.text=allOptions[2]
        holder.Option4.text=allOptions[3]
        holder.nextBtn.setOnClickListener {
            frag.movetoNextQuestion(holder.adapterPosition,QuestionList.size)
        }
        holder.prevBtn.setOnClickListener {
            frag.movetoPrevQuestion(holder.adapterPosition,QuestionList.size)
        }
        holder.endTest.setOnClickListener {
            frag.EndTest(totalQuestions,correctAnswersGiven,wrongAnswersGiven)
        }
        if (QuestionState[position] == 1) {
            markCorrectOption(holder, CorrectAnswerIndex)
            holder.Option1.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.Option2.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.Option3.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.Option4.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.SubmitBtn.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
        }
        if (QuestionState[position] == -1) {
            markCorrectOption(holder, CorrectAnswerIndex)
            wrongCorrectOption(holder, UserAnswers[position])
            holder.Option1.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.Option2.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.Option3.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.Option4.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.SubmitBtn.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
        }
        holder.Option1.setOnClickListener {
            if(QuestionState[position]==0) {
                RemoveBgColor(holder, SelectedIndex)
                SelectedIndex = 0
                InsertColor(holder, SelectedIndex)
            }
            else{
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
        }
        holder.Option2.setOnClickListener {
            if(QuestionState[position]==0) {
                RemoveBgColor(holder, SelectedIndex)
                SelectedIndex = 1
                InsertColor(holder, SelectedIndex)
            }
            else{
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
        }
        holder.Option3.setOnClickListener {
            if(QuestionState[position]==0) {
                RemoveBgColor(holder, SelectedIndex)
                SelectedIndex = 2
                InsertColor(holder, SelectedIndex)
            }
            else{
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
        }
        holder.Option4.setOnClickListener {
            if(QuestionState[position]==0) {
                RemoveBgColor(holder, SelectedIndex)
                SelectedIndex = 3
                InsertColor(holder, SelectedIndex)
            }
            else{
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
        }
        if(QuestionState[position]==0) {

            holder.SubmitBtn.setOnClickListener {
                    if (SelectedIndex == -1) {
                        Toast.makeText(
                            frag.requireContext(),
                            "Please select an option",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (SelectedIndex == CorrectAnswerIndex) {
                        frag.QuestionsAttempted++
                        frag.QuestionsCorrect++
                        correctAnswersGiven++
                        QuestionState[position] = 1
                        markCorrectOption(holder, SelectedIndex)
                    } else {
                        frag.QuestionsAttempted++
                        wrongAnswersGiven++
                        Attempted = true
                        UserAnswers[position] = SelectedIndex
                        QuestionState[position] = -1
                        wrongCorrectOption(holder, SelectedIndex)
                        markCorrectOption(holder, CorrectAnswerIndex)
                    }
            }
        }

    }
    fun RemoveBgColor(holder:ViewHolder,Option:Int){
        when(Option){
            0->{
                holder.Option1.setBackgroundResource(R.color.white)
            }
            1->{
                holder.Option2.setBackgroundResource(R.color.white)
            }
            2->{
                holder.Option3.setBackgroundResource(R.color.white)
            }
            3->{
                holder.Option4.setBackgroundResource(R.color.white)
            }
            else->{
                Log.d("Remove Color","InvalidOption")
            }
        }
    }
    fun InsertColor(holder:ViewHolder,Option:Int){
        when(Option){
            0->{
                holder.Option1.setBackgroundResource(R.color.golden_yellow)
            }
            1->{
                holder.Option2.setBackgroundResource(R.color.golden_yellow)
            }
            2->{
                holder.Option3.setBackgroundResource(R.color.golden_yellow)
            }
            3->{
                holder.Option4.setBackgroundResource(R.color.golden_yellow)
            }
            else->{
                Log.d("Insert Color","Invalid Position")
            }
        }
    }
    fun markCorrectOption(holder:ViewHolder,CorrectOption:Int){
        when(CorrectOption){
            0->{
                holder.Option1.setBackgroundResource(R.color.neon_green)
            }
            1->{
                holder.Option2.setBackgroundResource(R.color.neon_green)
            }
            2->{
                holder.Option3.setBackgroundResource(R.color.neon_green)
            }
            3->{
                holder.Option4.setBackgroundResource(R.color.neon_green)
            }
        }
    }
    fun wrongCorrectOption(holder:ViewHolder,WrongOption:Int){
        when(WrongOption){
            0->{
                holder.Option1.setBackgroundResource(R.color.neon_red)
            }
            1->{
                holder.Option2.setBackgroundResource(R.color.neon_red)
            }
            2->{
                holder.Option3.setBackgroundResource(R.color.neon_red)
            }
            3->{
                holder.Option4.setBackgroundResource(R.color.neon_red)
            }
        }
    }
}