package com.example.quizzify.BottomSheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.example.quizzify.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RecentContestBottomSheet:BottomSheetDialogFragment() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recent_contest_bottom_sheet, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contestName=arguments?.getString("ContestName")
        val contestID=arguments?.getString("ContestID")
        val totalQuestions=arguments?.getString("totalQuestions")
        val correctAnswers=arguments?.getString("CorrectAnswers")
        val wrongAnswers=arguments?.getString("WrongAnswers")
        val unanswered=arguments?.getString("Unanswered")
        val BottomSheet=view.findViewById<NestedScrollView>(R.id.bottomSheet)
        val ContestName=view.findViewById<TextView>(R.id.contestNameText)
        val ContestID=view.findViewById<TextView>(R.id.contestIdText)
        val closeBtn=view.findViewById<ImageView>(R.id.closeButton)
        val TotalQuestions=view.findViewById<TextView>(R.id.totalQuestionsText)
        val CorrectAnswers=view.findViewById<TextView>(R.id.correctAnswersText)
        val WrongAnswers=view.findViewById<TextView>(R.id.wrongAnswersText)
        val Unanswered=view.findViewById<TextView>(R.id.unattemptedText)
        val ProgressBar=view.findViewById<ProgressBar>(R.id.progressBar)
        bottomSheetBehavior = BottomSheetBehavior.from(BottomSheet)
        bottomSheetBehavior.isDraggable=false
        bottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
        val windowHeight = resources.displayMetrics.heightPixels
        BottomSheet.layoutParams.height = (windowHeight * 0.75).toInt()
        ContestName.text=contestName
        ContestID.text=contestID
        TotalQuestions.text=totalQuestions
        CorrectAnswers.text=correctAnswers
        WrongAnswers.text=wrongAnswers
        Unanswered.text=unanswered
        closeBtn.setOnClickListener {
            dismiss()
        }
        if (totalQuestions != null) {
            ProgressBar.max=totalQuestions.toInt()
        }
        if (correctAnswers != null) {
            ProgressBar.progress=correctAnswers.toInt()
        }
    }
    companion object{
        fun newInstance(ContestName:String,ContestID:String,totalQuestions:String,CorrectAnswers:String,WrongAnswers:String,Unanswered:String):RecentContestBottomSheet{
            val args=Bundle()
            args.putString("ContestName",ContestName)
            args.putString("ContestID",ContestID)
            args.putString("totalQuestions",totalQuestions)
            args.putString("CorrectAnswers",CorrectAnswers)
            args.putString("WrongAnswers",WrongAnswers)
            args.putString("Unanswered",Unanswered)
            val frag=RecentContestBottomSheet()
            frag.arguments=args
            return frag
        }
    }
}