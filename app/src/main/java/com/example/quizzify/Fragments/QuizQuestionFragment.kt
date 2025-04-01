package com.example.quizzify.Fragments

import android.app.Activity
import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.PracticeAdapter
import com.example.quizzify.Adapters.SavedNameListAdapter
import com.example.quizzify.BottomNavBar.CustomBottomNavigation
import com.example.quizzify.Dialogs.ConfirmationDialog
import com.example.quizzify.Dialogs.QuestionLoaderDialog
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.QuizViewModelFactory
import com.example.quizzify.ViewModelFactories.SavedListNamesVMFactory
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.example.quizzify.ViewModels.QuizViewModel
import com.example.quizzify.datamodels.ShuffledOptionsModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import javax.inject.Inject


class QuizQuestionFragment : Fragment() {



    lateinit var SLAdapter:SavedNameListAdapter

    private lateinit var GlobalFragVM: GlobalFragViewModel


    private lateinit var QuizRV:RecyclerView
    lateinit var dialog: BottomSheetDialog

    @Inject
    lateinit var QuizVMFactory:QuizViewModelFactory

    @Inject
   lateinit var SLNVMFactory:SavedListNamesVMFactory

   lateinit var PracticeAdapter:PracticeAdapter

    var originalFrameHeight: Int = 0

   var QuestionTracker:Int=0

    var TotalQuestion:Int=0
    var Correct_Answers:Int=0
    var Wrong_Answers:Int=0
    var _Unasnwered:Int=0

    lateinit var bundle:Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_question, container, false)
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

        GlobalFragVM= ViewModelProvider(this)[GlobalFragViewModel::class.java]

        val CategoryNum= arguments?.getInt("CategoryNum")!!.toInt()
        val QuestionNum= arguments?.getInt("QuestionCount")!!.toInt()
        val Difficulty= arguments?.getString("Difficulty").toString()
        QuizRV=view.findViewById<RecyclerView>(R.id.QuizRV)



        setFragmentResultListener("ConfirmationDialog") { _, _ ->
            ReplaceandEmptyFrag(PracticeFinalResultFragment(),bundle)
        }
       // QuizAdapter= QuizAdapter(this, emptyList())
       // PracticeAdapter= PracticeAdapter(this, emptyList())
        QuizRV.layoutManager=object:LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        //QuizRV.adapter=PracticeAdapter
        (requireActivity().application as QuizApplication).applicationComponent.injectQuiz(this)
        val QuizVM:QuizViewModel by viewModels {
            QuizVMFactory
        }
        QuizVM.getParams(QuestionNum,CategoryNum,Difficulty)
        QuizVM.questionset.observe(viewLifecycleOwner){
            Log.d("TAG", "onViewCreated: $it")
            if(it.size==QuestionNum) {
                var QuestionList= mutableListOf<String>()
                val loaderDialog = QuestionLoaderDialog()
                loaderDialog.show(childFragmentManager, "QuestionLoaderDialog")
                val startTime = System.currentTimeMillis()
                var ShuffledAnswerList= mutableListOf<ShuffledOptionsModel>()
                var CorrectAnswerIndex= mutableListOf<Int>()
                for(i in it){
                    QuestionList.add(i.question)
                    val Options= mutableListOf<String>()
                    Options.add(i.correct_answer)
                    Options.add(i.incorrect_answers[0])
                    Options.add(i.incorrect_answers[1])
                    Options.add(i.incorrect_answers[2])
                    Options.shuffle()
                    for(index in Options){
                       if(index==i.correct_answer){
                           CorrectAnswerIndex.add(Options.indexOf(index))
                           break
                       }
                    }
                    ShuffledAnswerList.add(ShuffledOptionsModel(Options[0],Options[1],Options[2],Options[3]))
                }
                val elapsedTime = System.currentTimeMillis() - startTime
                val remainingTime = 3000 - elapsedTime
                Handler(Looper.getMainLooper()).postDelayed({
                    loaderDialog.dismissLoader()
                    PracticeAdapter=PracticeAdapter(this,QuestionList,CorrectAnswerIndex,ShuffledAnswerList)
                    QuizRV.adapter=PracticeAdapter
                }, maxOf(remainingTime, 0))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing (This disables the back press)
            }
        })
    }
    fun movetonextItem(currentItem:Int,totalItems:Int){
        if(currentItem<totalItems-1){
            QuestionTracker++
            Log.d("TAG", "movetonextItem: $QuestionTracker")
            QuizRV.scrollToPosition(currentItem+1)
        }
        else{
            Toast.makeText(requireContext(),"No questions left",Toast.LENGTH_SHORT).show()
        }
    }
  fun movetoprevItem(currentItem: Int){
      if(currentItem!=0){
          QuestionTracker--
          Log.d("TAG", "movetoprevItem: $QuestionTracker")
          QuizRV.scrollToPosition(currentItem-1)
      }
      else{
          Toast.makeText(requireContext(),"No questions left",Toast.LENGTH_SHORT).show()
      }
  }
   // fun ReplaceFragment(frag:Fragment,Data:Bundle){
     //   frag.arguments=Data
       // val fragManager=parentFragmentManager
      //  val fragTransaction=fragManager.beginTransaction()
     //   fragTransaction.replace(R.id.FrameLayout,frag,"FinalResult")
      //  fragTransaction.commit()
   // }
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
            ReplaceandEmptyFrag(PracticeFinalResultFragment(),bundle)
        }
        No.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    fun Confirmation_Dialog(totalQuestions:Int,CorrectAnswers:Int,WrongAnswers:Int){
        val Unanswered=totalQuestions-CorrectAnswers-WrongAnswers
        TotalQuestion=totalQuestions
        Correct_Answers=CorrectAnswers
        Wrong_Answers=WrongAnswers
        _Unasnwered=Unanswered
        bundle=Bundle()
        bundle.putInt("TotalQuestions",TotalQuestion)
        bundle.putInt("CorrectAnswers",Correct_Answers)
        bundle.putInt("WrongAnswers",Wrong_Answers)
        bundle.putInt("Unanswered",_Unasnwered)
        val dialog= ConfirmationDialog.newInstance(totalQuestions.toString(),CorrectAnswers.toString(),WrongAnswers.toString(),Unanswered.toString(),"","","")
        dialog.show(parentFragmentManager,"Confirmation__Dialog")
    }

    fun ReplaceandEmptyFrag(frag: Fragment,Data: Bundle){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()


        val fragments = requireActivity().supportFragmentManager.fragments
        for (fragment in fragments) {
            transaction.remove(fragment)
        }

        requireActivity().supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        Data.putInt("Height",originalFrameHeight)
        frag.arguments=Data
        GlobalFragVM.currentFragment=frag
        transaction.replace(R.id.FrameLayout, frag)
        transaction.commit()
    }
    override fun onDestroy() {
        super.onDestroy()
       // requireActivity().findViewById<BottomNavigationView>(R.id.BottomNavBar).visibility = View.VISIBLE

        // Restore FrameLayout size
       // val frameLayout = requireActivity().findViewById<FrameLayout>(R.id.FrameLayout)
       // val layoutParams = frameLayout.layoutParams
       // layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT  // Restore original size
       // frameLayout.layoutParams = layoutParams
    }
    fun getAppUsableScreenHeight(activity: Activity): Int {
        val rect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        return rect.height() // Returns the height excluding status bar
    }
}