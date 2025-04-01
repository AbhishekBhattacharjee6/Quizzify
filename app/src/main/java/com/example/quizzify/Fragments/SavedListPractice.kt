package com.example.quizzify.Fragments

import android.app.Activity
import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.SavedListPracticeAdapter
import com.example.quizzify.Adapters.SavedNameListAdapter
import com.example.quizzify.BottomNavBar.CustomBottomNavigation
import com.example.quizzify.Dialogs.ConfirmationDialog
import com.example.quizzify.R
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.example.quizzify.datamodels.QuestionModel
import com.example.quizzify.datamodels.ShuffledOptionsModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class SavedListPractice : Fragment() {

    lateinit var LiveQuizRV:RecyclerView

    var QuestionsAttempted:Int=0
    var QuestionsCorrect:Int=0

    private lateinit var GlobalFragVM: GlobalFragViewModel


    var originalFrameHeight: Int = 0

    var QuestionTracker:Int=0

    lateinit var bun_dle:Bundle


    lateinit var SavedListPracticeAdapter:SavedListPracticeAdapter
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

        requireActivity().findViewById<CustomBottomNavigation>(R.id.bottomNavigation).visibility = View.GONE
        val frameLayout = requireActivity().findViewById<FrameLayout>(R.id.FrameLayout)
        if (originalFrameHeight == 0) {
            originalFrameHeight = frameLayout.layoutParams.height
        }
        val layoutParams = frameLayout.layoutParams
        layoutParams.height = getAppUsableScreenHeight(requireActivity()) // Full app height including where bottom nav was
        frameLayout.layoutParams = layoutParams

        setFragmentResultListener("ConfirmationDialog") { _, _ ->
            ReplaceandEmptyFrag(SavedPracticeFinalResultFragment(),bun_dle)
        }
        GlobalFragVM= ViewModelProvider(this)[GlobalFragViewModel::class.java]

        val Questions= arguments?.getParcelableArrayList<QuestionModel>("QuestionList")
        LiveQuizRV=view.findViewById<RecyclerView>(R.id.LiveQuizRV)
        LiveQuizRV.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        if(Questions!=null){
            val QuestionList= mutableListOf<String>()
            val CorrectIndexList= mutableListOf<Int>()
            val Options= mutableListOf<ShuffledOptionsModel>()
            val QuestionIDList= mutableListOf<String>()
            for(i in Questions){
                QuestionList.add(i.Question)
                QuestionIDList.add(i.QuestionID)
                val OptionsList= mutableListOf<String>(
                    i.CorrectAnswer,
                    i.WrongAnswer1,
                    i.WrongAnswer2,
                    i.WrongAnswer3
                )
                OptionsList.shuffle()
                val CorrectIndex=OptionsList.indexOf(i.CorrectAnswer)
                CorrectIndexList.add(CorrectIndex)
                val ShuffledOptions=
                    ShuffledOptionsModel(OptionsList[0],OptionsList[1],OptionsList[2],OptionsList[3])
                Options.add(ShuffledOptions)
            }
            SavedListPracticeAdapter=SavedListPracticeAdapter(this,QuestionList,QuestionIDList,CorrectIndexList,Options)
            LiveQuizRV.adapter=SavedListPracticeAdapter
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing (This disables the back press)
            }
        })
    }

    fun movetoNextQuestion(currentQuestion:Int,totalQuestions:Int){
        if(currentQuestion<totalQuestions-1){
            QuestionTracker++
            LiveQuizRV.scrollToPosition(currentQuestion+1)
        }
        else{
            Toast.makeText(requireContext(),"No more Questions Left", Toast.LENGTH_SHORT).show()
        }
    }
    fun movetoPrevQuestion(currentQuestion: Int,totalQuestions: Int){
        if(currentQuestion!=0){
            QuestionTracker--
            LiveQuizRV.scrollToPosition(currentQuestion-1)
        }
        else{
            Toast.makeText(requireContext(),"No more Questions Left", Toast.LENGTH_SHORT).show()
        }
    }
    fun ReplaceandEmptyFrag(frag: Fragment,Data: Bundle){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        val fragments = requireActivity().supportFragmentManager.fragments
        for (fragment in fragments) {
            transaction.remove(fragment)
        }

        requireActivity().supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        frag.arguments = Data
        Data.putInt("Height",originalFrameHeight)
        frag.arguments=Data

        GlobalFragVM.currentFragment=frag
        transaction.replace(R.id.FrameLayout, frag)
        transaction.commit()
    }
    fun EndTest(totalQuestions:Int,CorrectAnswers:Int,WrongAnswers:Int){
        val dialog= Dialog(requireContext())
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
    fun Confirmation_Dialog(totalQuestions:Int,CorrectAnswers:Int,WrongAnswers:Int){
        val Unanswered=totalQuestions-CorrectAnswers-WrongAnswers
        val bun_dle=Bundle()
        bun_dle.putInt("TotalQuestions",totalQuestions)
        bun_dle.putInt("CorrectAnswers",CorrectAnswers)
        bun_dle.putInt("WrongAnswers",WrongAnswers)
        bun_dle.putInt("Unanswered",Unanswered)
        val dialog= ConfirmationDialog.newInstance(totalQuestions.toString(),CorrectAnswers.toString(),WrongAnswers.toString(),Unanswered.toString(),"","","")
        dialog.show(parentFragmentManager,"Confirmation_Dialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        //requireActivity().findViewById<BottomNavigationView>(R.id.BottomNavBar).visibility = View.VISIBLE

        // Restore FrameLayout size
      //  val frameLayout = requireActivity().findViewById<FrameLayout>(R.id.FrameLayout)
       // val layoutParams = frameLayout.layoutParams
      //  layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT  // Restore original size
       // frameLayout.layoutParams = layoutParams
    }
    fun getAppUsableScreenHeight(activity: Activity): Int {
        val rect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        return rect.height() // Returns the height excluding status bar
    }
}