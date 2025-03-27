package com.example.quizzify.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Fragments.QuizQuestionFragment
import com.example.quizzify.QuestionSetDataModels.Result
import com.example.quizzify.R
import com.example.quizzify.datamodels.ShuffledOptionsModel
import java.util.Base64

class PracticeAdapter(val frag: QuizQuestionFragment, var QuestionList:List<String>,var CorrectIndexList:List<Int>,var Options:List<ShuffledOptionsModel>):RecyclerView.Adapter<PracticeAdapter.ViewHolder>(){
    var QuestionState: MutableList<Int> = MutableList(QuestionList.size) { 0 }
    var UserAnswers:MutableList<Int> = MutableList(QuestionList.size){0}

    var Bookmark:MutableList<Boolean> =MutableList(QuestionList.size){true}

    val totalQuestions=QuestionList.size
    var correctAnswersGiven=0
    var wrongAnswersGiven=0
    var answersAttempted=0
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val BookMark=itemView.findViewById<ImageView>(R.id.bookmarkIcon)
        val EndTest=itemView.findViewById<Button>(R.id.endTestButton)
        val QuestionCounter=itemView.findViewById<TextView>(R.id.questionCounter)
        val Progress=itemView.findViewById<ProgressBar>(R.id.progressBar)
        val ProgressBarText=itemView.findViewById<TextView>(R.id.progressPercent)
        val Question=itemView.findViewById<TextView>(R.id.QuestionText)
        val OptionA=itemView.findViewById<RadioButton>(R.id.optionA)
        val OptionB=itemView.findViewById<RadioButton>(R.id.optionB)
        val OptionC=itemView.findViewById<RadioButton>(R.id.optionC)
        val OptionD=itemView.findViewById<RadioButton>(R.id.optionD)
        val Submit=itemView.findViewById<Button>(R.id.submitButton)
        val Next=itemView.findViewById<Button>(R.id.nextButton)
        val Prev=itemView.findViewById<Button>(R.id.prevButton)
        val Timer=itemView.findViewById<TextView>(R.id.TimerText)
        val OptionACV=itemView.findViewById<CardView>(R.id.OptionACV)
        val OptionBCV=itemView.findViewById<CardView>(R.id.OptionBCV)
        val OptionCCV=itemView.findViewById<CardView>(R.id.OptionCCV)
        val OptionDCV=itemView.findViewById<CardView>(R.id.OptionDCV)
        val TimerContainer=itemView.findViewById<LinearLayout>(R.id.timerContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(frag.requireContext()).inflate(R.layout.new_live_test_ui,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return QuestionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.BookMark.setOnClickListener {
            Toast.makeText(frag.requireContext(),"Not Available",Toast.LENGTH_SHORT).show()
        }
        Log.d("Correct","Correct"+position+QuestionState[position].toString())
        holder.TimerContainer.visibility=View.GONE
        for(i in 0..3){
            RemoveSelection(holder,i)
        }
        holder.Question.text=decodeQuestion(QuestionList[position])
        holder.QuestionCounter.text="Question ${frag.QuestionTracker+1} out of ${QuestionList.size}"
        var CorrectAnswerIndex=-1;
        var SelectedIndex=-1;

        holder.OptionA.text=decodeQuestion(Options[position].Option0)
        holder.OptionB.text=decodeQuestion(Options[position].Option1)
        holder.OptionC.text=decodeQuestion(Options[position].Option2)
        holder.OptionD.text=decodeQuestion(Options[position].Option3)
        holder.Progress.max=QuestionList.size
        holder.Progress.min=0
        holder.Progress.progress=answersAttempted
        holder.ProgressBarText.text=ProgressCounter(answersAttempted,QuestionList.size).toString()+" %"
        holder.Next.setOnClickListener {
            frag.movetonextItem(position,QuestionList.size)
        }
        holder.Prev.setOnClickListener {
            frag.movetoprevItem(position)
        }
        holder.EndTest.setOnClickListener {
            //frag.EndTest(totalQuestions,correctAnswersGiven,wrongAnswersGiven)
            frag.Confirmation_Dialog(totalQuestions,correctAnswersGiven,wrongAnswersGiven)
        }
        if(QuestionState[frag.QuestionTracker]==1){
            //Log.d("Correct","Correct"+position+QuestionState[position].toString())
            markCorrectOption(holder,CorrectIndexList[frag.QuestionTracker])
        }
        else if(QuestionState[frag.QuestionTracker]==-1){
            markCorrectOption(holder,CorrectIndexList[frag.QuestionTracker])
            markWrongOption(holder,UserAnswers[frag.QuestionTracker])
            Log.d("Correct","Correct"+position+QuestionState[position].toString())
        }
        holder.OptionA.setOnClickListener {
            if(QuestionState[frag.QuestionTracker]==0) {
                RemoveSelection(holder, SelectedIndex)
                SelectedIndex = 0
                markSelected(holder, 0)
            }
            else{
                Toast.makeText(frag.requireContext(),"Already Attempted", Toast.LENGTH_SHORT).show()
            }
        }
        holder.OptionB.setOnClickListener {
            if(QuestionState[frag.QuestionTracker]==0) {
                RemoveSelection(holder, SelectedIndex)
                SelectedIndex = 1
                markSelected(holder, 1)
            }
            else{
                Toast.makeText(frag.requireContext(),"Already Attempted", Toast.LENGTH_SHORT).show()
            }
        }
        holder.OptionC.setOnClickListener {
            if(QuestionState[frag.QuestionTracker]==0) {
                RemoveSelection(holder, SelectedIndex)
                SelectedIndex = 2
                markSelected(holder, 2)
            }
            else{
                Toast.makeText(frag.requireContext(),"Already Attempted", Toast.LENGTH_SHORT).show()
            }
        }
        holder.OptionD.setOnClickListener {
            if(QuestionState[frag.QuestionTracker]==0) {
                RemoveSelection(holder, SelectedIndex)
                SelectedIndex = 3
                markSelected(holder, 3)
            }
            else{
                Toast.makeText(frag.requireContext(),"Already Attempted", Toast.LENGTH_SHORT).show()
            }
        }
        holder.Submit.setOnClickListener {
            if(QuestionState[frag.QuestionTracker]==0) {
                if (SelectedIndex == -1) {
                    Toast.makeText(
                        frag.requireContext(),
                        "Please select an option",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (SelectedIndex == CorrectIndexList[frag.QuestionTracker]) {
                    correctAnswersGiven++
                    QuestionState[frag.QuestionTracker] = 1
                    answersAttempted++
                    markCorrectOption(holder, SelectedIndex)
                    holder.Progress.progress = answersAttempted
                    holder.ProgressBarText.text=ProgressCounter(answersAttempted,QuestionList.size).toString()+" %"
                } else {
                    wrongAnswersGiven++
                    answersAttempted++
                    UserAnswers[frag.QuestionTracker] = SelectedIndex
                    QuestionState[frag.QuestionTracker] = -1
                    markWrongOption(holder, SelectedIndex)
                    markCorrectOption(holder, CorrectIndexList[frag.QuestionTracker])
                    holder.Progress.progress = answersAttempted
                    holder.ProgressBarText.text=ProgressCounter(answersAttempted,QuestionList.size).toString()+" %"
                }
            }
            else{
                Toast.makeText(frag.requireContext(),"Already Attempted", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun markCorrectOption(holder: ViewHolder, CorrectOption:Int){
        when(CorrectOption){
            0->{
                holder.OptionA.setBackgroundResource(R.drawable.option_correct)
                holder.OptionA.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_correct,0)
                holder.OptionACV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionACV.elevation=10f
            }
            1->{
                holder.OptionB.setBackgroundResource(R.drawable.option_correct)
                holder.OptionB.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_correct,0)
                holder.OptionBCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionBCV.elevation=10f
            }
            2->{
                holder.OptionC.setBackgroundResource(R.drawable.option_correct)
                holder.OptionC.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_correct,0)
                holder.OptionCCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionCCV.elevation=10f
            }
            3->{
                holder.OptionD.setBackgroundResource(R.drawable.option_correct)
                holder.OptionD.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_correct,0)
                holder.OptionDCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionDCV.elevation=10f
            }
        }
    }
    private fun markWrongOption(holder: ViewHolder, WrongOption:Int){
        when(WrongOption){
            0->{
                holder.OptionA.setBackgroundResource(R.drawable.option_wrong)
                holder.OptionA.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_wrong,0)
                holder.OptionACV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionACV.elevation=10f
            }
            1->{
                holder.OptionB.setBackgroundResource(R.drawable.option_wrong)
                holder.OptionB.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_wrong,0)
                holder.OptionBCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionBCV.elevation=10f
            }
            2->{
                holder.OptionC.setBackgroundResource(R.drawable.option_wrong)
                holder.OptionC.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_wrong,0)
                holder.OptionCCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionCCV.elevation=10f
            }
            3->{
                holder.OptionD.setBackgroundResource(R.drawable.option_wrong)
                holder.OptionD.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_wrong,0)
                holder.OptionDCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionDCV.elevation=10f
            }
        }
    }
    private fun markSelected(holder: ViewHolder, SelectedOption:Int){
        when(SelectedOption){
            0->{
                holder.OptionA.setBackgroundResource(R.drawable.option_selected_yellow_bg)
                holder.OptionA.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_selected_yellow,0)
                holder.OptionACV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionACV.elevation=10f
            }
            1->{
                holder.OptionB.setBackgroundResource(R.drawable.option_selected_yellow_bg)
                holder.OptionB.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_selected_yellow,0)
                holder.OptionBCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionBCV.elevation=10f
            }
            2->{
                holder.OptionC.setBackgroundResource(R.drawable.option_selected_yellow_bg)
                holder.OptionC.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_selected_yellow,0)
                holder.OptionCCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionCCV.elevation=10f
            }
            3->{
                holder.OptionD.setBackgroundResource(R.drawable.option_selected_yellow_bg)
                holder.OptionD.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.custom_radio_selected_yellow,0)
                holder.OptionDCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.transparent))
                holder.OptionDCV.elevation=10f
            }
        }
    }
    private fun RemoveSelection(holder: ViewHolder, SelectedOption: Int){
        when(SelectedOption){
            0->{
                holder.OptionA.setBackgroundResource(R.drawable.option_selector)
                holder.OptionA.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                holder.OptionACV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.white))
                holder.OptionACV.elevation=0f

            }
            1->{
                holder.OptionB.setBackgroundResource(R.drawable.option_selector)
                holder.OptionB.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                holder.OptionBCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.white))
                holder.OptionBCV.elevation=0f
            }
            2->{
                holder.OptionC.setBackgroundResource(R.drawable.option_selector)
                holder.OptionC.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                holder.OptionCCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.white))
                holder.OptionCCV.elevation=0f
            }
            3->{
                holder.OptionD.setBackgroundResource(R.drawable.option_selector)
                holder.OptionD.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
                holder.OptionDCV.setCardBackgroundColor(ContextCompat.getColor(frag.requireContext(),R.color.white))
                holder.OptionDCV.elevation=0f
            }
        }
    }
    private fun ProgressCounter(AnswersAttempted:Int,TotalQuestion:Int):Int{
        return ((AnswersAttempted.toDouble()/TotalQuestion.toDouble())*100).toInt()
    }

    private fun decodeQuestion(encodedQuestion: String): String {
        val decodedBytes= Base64.getDecoder().decode(encodedQuestion)
        return String(decodedBytes)
    }
}