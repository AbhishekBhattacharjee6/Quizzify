package com.example.quizzify.Fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.LiveContestAdapter
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.IndividualQuestionViewModelFactory
import com.example.quizzify.ViewModelFactories.QuestionListViewModelFactory
import com.example.quizzify.ViewModels.CountDownTimerViewModel
import com.example.quizzify.ViewModels.IndividualQuestionViewModel
import com.example.quizzify.ViewModels.QuestionListViewModel
import com.example.quizzify.datamodels.QuestionModel
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import javax.inject.Inject


class LiveContest : Fragment() {

    @Inject
    lateinit var QuestionListVMFactory:QuestionListViewModelFactory

    @Inject
    lateinit var IndividualQuestionVMFactory:IndividualQuestionViewModelFactory

    lateinit var LiveContestAdapter:LiveContestAdapter

    lateinit var LiveQuizRV:RecyclerView

    var TotalQuestionCount:Int=0
    var QuestionsAttempted:Int=0
    var QuestionsCorrect:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live_contest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val QuizSetId= arguments?.getString("QuizSetId")
        val Duration= arguments?.getString("Duration")
        val Questions=arguments?.getParcelableArrayList<QuestionModel>("QuestionList")
        LiveQuizRV=view.findViewById<RecyclerView>(R.id.LiveQuizRV)
        LiveQuizRV.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        (requireActivity().application as QuizApplication).applicationComponent.injectliveContest(this)
        Log.d("QuizSetId",QuizSetId.toString())
        val Timer:CountDownTimerViewModel by viewModels()
        if(Questions!=null){
            TotalQuestionCount=Questions.size
            LiveContestAdapter=LiveContestAdapter(this,Questions)
            LiveQuizRV.adapter=LiveContestAdapter
        }

    }
    fun movetoNextQuestion(currentQuestion:Int,totalQuestions:Int){
        if(currentQuestion<totalQuestions-1){
            LiveQuizRV.scrollToPosition(currentQuestion+1)
        }
        else{
            Toast.makeText(requireContext(),"No more Questions Left",Toast.LENGTH_SHORT).show()
        }
    }
    fun movetoPrevQuestion(currentQuestion: Int,totalQuestions: Int){
        if(currentQuestion!=0){
            LiveQuizRV.scrollToPosition(currentQuestion-1)
        }
        else{
            Toast.makeText(requireContext(),"No more Questions Left",Toast.LENGTH_SHORT).show()
        }
    }
    fun EndTest(totalQuestions:Int,CorrectAnswers:Int,WrongAnswers:Int){
        val dialog=Dialog(requireContext())
        dialog.setContentView(R.layout.endquizdialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        val Yes=dialog.findViewById<Button>(R.id.button11)
        val No=dialog.findViewById<Button>(R.id.button10)
        Yes.setOnClickListener {
            Toast.makeText(requireContext(),"Test Ended",Toast.LENGTH_SHORT).show()
            val Unanswered=totalQuestions-CorrectAnswers-WrongAnswers
            val bundle=Bundle()
            bundle.putInt("TotalQuestions",totalQuestions)
            bundle.putInt("CorrectAnswers",CorrectAnswers)
            bundle.putInt("WrongAnswers",WrongAnswers)
            bundle.putInt("Unanswered",Unanswered)
            dialog.dismiss()
            ReplaceandEmptyFrag(FinalResultFragment(),bundle)
        }
        No.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun ReplaceFragment(frag:Fragment,Data:Bundle){
        frag.arguments=Data
        val fragManager=parentFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        fragTransaction.replace(R.id.FrameLayout,frag,"FinalResult")
        fragTransaction.commit()
    }

    fun ReplaceandEmptyFrag(frag: Fragment,Data: Bundle){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        val fragments = requireActivity().supportFragmentManager.fragments
        for (fragment in fragments) {
            transaction.remove(fragment)
        }

        requireActivity().supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)


        frag.arguments = Data

        transaction.replace(R.id.FrameLayout, frag)

        transaction.commit()

    }

}