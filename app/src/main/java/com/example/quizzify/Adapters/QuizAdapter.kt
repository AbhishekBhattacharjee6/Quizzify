package com.example.quizzify.Adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Fragments.FinalResultFragment
import com.example.quizzify.Fragments.QuizQuestionFragment
import com.example.quizzify.QuestionSetDataModels.Result
import com.example.quizzify.R
import java.util.Base64

class QuizAdapter(val frag:QuizQuestionFragment, var QuizList:List<Result>):RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    val totalQuestions=QuizList.size
    var correctAnswersGiven=0
    var wrongAnswersGiven=0

    var QuestionState: MutableList<Int> = MutableList(QuizList.size) { 0 }
    var UserAnswers:MutableList<Int> = MutableList(QuizList.size){-0}
    //0->Not Attempted yet,1->Attempted with correct answer,-1->Attempted but wrong answer

    class QuizViewHolder(val itemView: View):RecyclerView.ViewHolder(itemView){
        val Questionview=itemView.findViewById<TextView>(R.id.textView3)
        val option1=itemView.findViewById<TextView>(R.id.OptionA)
        val option2=itemView.findViewById<TextView>(R.id.OptionB)
        val option3=itemView.findViewById<TextView>(R.id.OptionC)
        val option4=itemView.findViewById<TextView>(R.id.OptionD)
        val SubmitBtn=itemView.findViewById<Button>(R.id.button2)
        val PrevBtn=itemView.findViewById<Button>(R.id.button3)
        val NextBtn=itemView.findViewById<Button>(R.id.button4)
        val EndTestButton=itemView.findViewById<Button>(R.id.endtest)
        val bookmark=itemView.findViewById<ImageButton>(R.id.imageButton6)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val inflater=LayoutInflater.from(frag.requireContext()).inflate( R.layout.each_question,parent,false)
        return QuizViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return QuizList.size
    }
    fun updateList(newList:List<Result>){
        this.QuizList=newList
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        var correctOption: Int = 0
        var Attempted = false
        var SelectedAnswer: String = "null"
        var SelectedOption: String = "null"
        var allOptions = mutableListOf<String>()
        allOptions.add(QuizList[position].correct_answer)
        allOptions.addAll(QuizList[position].incorrect_answers)
        allOptions.shuffle()
        for (option in allOptions) {
            if (option == QuizList[position].correct_answer) {
                correctOption = allOptions.indexOf(option)
            }
        }
        val decodedQuestion = decodeQuestion(QuizList[position].question)
        holder.Questionview.text = decodedQuestion
        holder.option1.text = decodeQuestion(allOptions[0])
        holder.option2.text = decodeQuestion(allOptions[1])
        holder.option3.text = decodeQuestion(allOptions[2])
        holder.option4.text = decodeQuestion(allOptions[3])
        holder.NextBtn.setOnClickListener {
            frag.movetonextItem(holder.adapterPosition, QuizList.size)
        }
        holder.PrevBtn.setOnClickListener {
            frag.movetoprevItem(holder.adapterPosition)
        }
        if (QuestionState[position] == 1) {
            MarkCorrectOption(holder, correctOption)
            holder.option1.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.option2.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.option3.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.option4.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
        }
        if (QuestionState[position] == -1) {
            MarkCorrectOption(holder, correctOption)
            MarkWrongAnswer(holder, UserAnswers[position].toString())
            holder.option1.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.option2.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.option3.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
            holder.option4.setOnClickListener {
                Toast.makeText(frag.requireContext(),"Already Attempted",Toast.LENGTH_SHORT).show()
            }
        }
        if (QuestionState[position] == 0){
            holder.option1.setOnClickListener {
                RemoveBgColor(SelectedOption, holder)
                SelectedOption = "0"
                SelectedAnswer = allOptions[0]
                InsertBgColor(SelectedOption, holder)
            }
        holder.option2.setOnClickListener {
            RemoveBgColor(SelectedOption, holder)
            SelectedOption = "1"
            SelectedAnswer = allOptions[1]
            InsertBgColor(SelectedOption, holder)
        }
        holder.option3.setOnClickListener {
            RemoveBgColor(SelectedOption, holder)
            SelectedOption = "2"
            SelectedAnswer = allOptions[2]
            InsertBgColor(SelectedOption, holder)
        }
        holder.option4.setOnClickListener {
            RemoveBgColor(SelectedOption, holder)
            SelectedOption = "3"
            SelectedAnswer = allOptions[3]
            InsertBgColor(SelectedOption, holder)
        }
        //Submit Button Functionality
        holder.SubmitBtn.setOnClickListener {
            if (!Attempted) {
                if (SelectedAnswer == "null") {
                    Toast.makeText(
                        frag.requireContext(),
                        "Please select an option",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (SelectedAnswer == allOptions[correctOption]) {
                    Attempted = true
                    correctAnswersGiven++
                    QuestionState[position] = 1
                    RemoveBgColor(SelectedOption, holder)
                    MarkCorrectOption(holder, correctOption)
                } else {
                    Attempted = true
                    wrongAnswersGiven++
                    QuestionState[position] = -1
                    UserAnswers[position] = SelectedOption.toInt()
                    RemoveBgColor(SelectedOption, holder)
                    MarkCorrectOption(holder, correctOption)
                    MarkWrongAnswer(holder, SelectedOption)
                }
            } else {
                Toast.makeText(frag.requireContext(), "Already Attempted", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
        holder.EndTestButton.setOnClickListener {
            val Data=Bundle()
            Data.putString("Total",totalQuestions.toString())
            Data.putString("Correct",correctAnswersGiven.toString())
            Data.putString("WrongAnswers",wrongAnswersGiven.toString())
            Data.putString("UnAnswered",(totalQuestions-correctAnswersGiven-wrongAnswersGiven).toString())
            frag.ReplaceFragment(FinalResultFragment(),Data)
        }
        holder.bookmark.setOnClickListener {
            frag.saveListDialog()
        }

    }
    private fun RemoveBgColor(option:String, holder: QuizViewHolder){
        when(option){
            "0"->{
               holder.option1.setBackgroundResource(R.color.white)
            }
            "1"->{
                holder.option2.setBackgroundResource(R.color.white)
            }
            "2"->{
                holder.option3.setBackgroundResource(R.color.white)
            }
            "3"->{
                holder.option4.setBackgroundResource(R.color.white)
            }
            else->{
                Log.d("Color","No Color")
            }
        }
    }
    private fun InsertBgColor(option:String, holder: QuizViewHolder){
        when(option){
            "0"->{
                holder.option1.setBackgroundResource(R.color.golden_yellow)
            }
            "1"->{
                holder.option2.setBackgroundResource(R.color.golden_yellow)
            }
            "2"->{
                holder.option3.setBackgroundResource(R.color.golden_yellow)
            }
            "3"->{
                holder.option4.setBackgroundResource(R.color.golden_yellow)
            }
            else->{
                Log.d("Color","No Color")
            }
        }
    }
    fun MarkCorrectOption(holder: QuizViewHolder,CorrectOption:Int){
        when(CorrectOption){
            0->{
                holder.option1.setBackgroundResource(R.color.neon_green)
            }
            1-> {
                holder.option2.setBackgroundResource(R.color.neon_green)
            }
            2->{
                holder.option3.setBackgroundResource(R.color.neon_green)
            }
            3->{
                holder.option4.setBackgroundResource(R.color.neon_green)
            }
            else->{
                Log.d("Color","No Correct answer")
            }
        }
    }
    fun MarkWrongAnswer(holder: QuizViewHolder,SelectedOption:String){
        when(SelectedOption){
            "0"->{
                holder.option1.setBackgroundResource(R.color.neon_red)
            }
            "1"->{
                holder.option2.setBackgroundResource(R.color.neon_red)
            }
            "2"->{
                holder.option3.setBackgroundResource(R.color.neon_red)
            }
            "3"->{
                holder.option4.setBackgroundResource(R.color.neon_red)
            }
            else->{
                Log.d("Color","No Color")
            }
        }
    }
    private fun decodeQuestion(encodedQuestion: String): String {
       val decodedBytes=Base64.getDecoder().decode(encodedQuestion)
       return String(decodedBytes)
    }

}