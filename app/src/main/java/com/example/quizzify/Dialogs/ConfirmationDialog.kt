package com.example.quizzify.Dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.quizzify.R

class ConfirmationDialog :DialogFragment(){

    private lateinit var total_Questions:String
    private lateinit var correct_Answers:String
    private lateinit var wrong_Answers:String
    private lateinit var _unanswered:String
    private lateinit var quiz_SetID:String
    private lateinit var room_Name:String
    private lateinit var valid_Till:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        arguments.let {
            total_Questions=it?.getString(ARG_TotalQuestions).toString()
            correct_Answers=it?.getString(ARG_CorrectAnswers).toString()
            wrong_Answers=it?.getString(ARG_WrongAnswers).toString()
            _unanswered=it?.getString(ARG_Unanswered).toString()
            quiz_SetID=it?.getString(ARG_QuizSetID).toString()
            room_Name=it?.getString(ARG_RoomName).toString()
            valid_Till=it?.getString(ARG_ValidTill).toString()
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)// Fullscreen background
        }
        dialog?.apply {
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.confirmation_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val GeneralText=view.findViewById<TextView>(R.id.tvSubmitDetails)
        val ProgressBar=view.findViewById<ProgressBar>(R.id.progressBar)
        val AnswerCount=view.findViewById<TextView>(R.id.answerCount)
        val ContinueBtn=view.findViewById<Button>(R.id.btnContinue)
        val ConfirmBtn=view.findViewById<Button>(R.id.btnSubmit)
        val Percentage=view.findViewById<TextView>(R.id.percentageCount)

        ContinueBtn.setOnClickListener {
            dismiss()
        }
        ConfirmBtn.setOnClickListener {
            ConfirmationDialog().show(parentFragmentManager,"ConfirmationDialog")
            dismiss()
        }
        val totalAnswersGiven=correct_Answers.toInt()+wrong_Answers.toInt()
        GeneralText.text="You have given $totalAnswersGiven answers out of $total_Questions"
        ProgressBar.max=total_Questions.toInt()
        ProgressBar.progress=totalAnswersGiven
        AnswerCount.text="Answered: $totalAnswersGiven/$total_Questions"
        val percentage=(totalAnswersGiven.toDouble()/total_Questions.toDouble())*100
        Percentage.text="$percentage%"
    }
    companion object{
        private const val ARG_TotalQuestions="TotalQuestions"
        private const val ARG_CorrectAnswers="CorrectAnswers"
        private const val ARG_WrongAnswers="WrongAnswers"
        private const val ARG_Unanswered="Unanswered"
        private const val ARG_QuizSetID="QuizSetID"
        private const val ARG_RoomName="RoomName"
        private const val ARG_ValidTill="ValidTill"
        fun newInstance(
            total:String,
            correct:String,
            wrong:String,
            unans:String,
            quizSetID:String,
            roomName:String,
            validTill:String
        ):ConfirmationDialog{
            val fragment=ConfirmationDialog()
            val args=Bundle().apply {
                putString(ARG_TotalQuestions,total)
                putString(ARG_CorrectAnswers,correct)
                putString(ARG_WrongAnswers,wrong)
                putString(ARG_Unanswered,unans)
                putString(ARG_QuizSetID,quizSetID)
                putString(ARG_RoomName,roomName)
                putString(ARG_ValidTill,validTill)
            }
            fragment. arguments=args
            return fragment
        }
    }
}