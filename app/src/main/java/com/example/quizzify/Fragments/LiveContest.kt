package com.example.quizzify.Fragments

import android.app.Activity
import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.ListShowAdapter
import com.example.quizzify.Adapters.LiveTestAdapter
import com.example.quizzify.BottomNavBar.CustomBottomNavigation
import com.example.quizzify.Dialogs.ConfirmationDialog
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.SimpletonObjects.SaveListQuestionTrack
import com.example.quizzify.ViewModelFactories.IndividualQuestionViewModelFactory
import com.example.quizzify.ViewModelFactories.ListShowViewModelFactories
import com.example.quizzify.ViewModelFactories.QuestionListViewModelFactory
import com.example.quizzify.ViewModels.CountDownTimerViewModel
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.example.quizzify.ViewModels.ListShowViewModel
import com.example.quizzify.datamodels.QuestionModel
import com.example.quizzify.datamodels.SavedCollectionModel
import com.example.quizzify.datamodels.ShuffledOptionsModel
import com.example.quizzify.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import javax.inject.Inject
import kotlin.properties.Delegates


class LiveContest : Fragment() {

    @Inject
    lateinit var QuestionListVMFactory:QuestionListViewModelFactory

    @Inject
    lateinit var IndividualQuestionVMFactory:IndividualQuestionViewModelFactory

    @Inject
    lateinit var ListShowVMFactory:ListShowViewModelFactories

    lateinit var LiveTestAdapter:LiveTestAdapter

    lateinit var LiveQuizRV:RecyclerView

    lateinit var LSAdapter:ListShowAdapter

    private lateinit var GlobalFragVM: GlobalFragViewModel

    lateinit var bun_dle:Bundle



    var TotalQuestionCount:Int=0
    var QuestionsAttempted:Int=0
    var QuestionsCorrect:Int=0

    var SaveAllowed by Delegates.notNull<Boolean>()

    lateinit var QuizSetID:String
    lateinit var RoomName:String
    lateinit var ValidTill:String

    @Inject
    lateinit var FireStore:FireStoreInstance

    var originalFrameHeight: Int = 0

    var QuestionTracker:Int=0
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


        GlobalFragVM= ViewModelProvider(this)[GlobalFragViewModel::class.java]


        setFragmentResultListener("ConfirmationDialog") { _, _ ->
            ReplaceandEmptyFrag(TestResults(),bun_dle)
        }


        QuizSetID= arguments?.getString("QuizSetID").toString()
        RoomName= arguments?.getString("RoomName").toString()
        ValidTill= arguments?.getString("ValidTill").toString()

        val Duration= arguments?.getString("Duration")
        val Questions=arguments?.getParcelableArrayList<QuestionModel>("QuestionList")
        SaveAllowed= arguments?.getBoolean("SaveAllowed")!!
        LiveQuizRV=view.findViewById<RecyclerView>(R.id.LiveQuizRV)
        LiveQuizRV.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        (requireActivity().application as QuizApplication).applicationComponent.injectliveContest(this)
        Log.d("QuizSetId",QuizSetID.toString())
        val Timer:CountDownTimerViewModel by viewModels()
        if(Questions!=null){
            TotalQuestionCount=Questions.size
            //LiveContestAdapter=LiveContestAdapter(this,Questions)
           // LiveQuizRV.adapter=LiveContestAdapter
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
                val ShuffledOptions=ShuffledOptionsModel(OptionsList[0],OptionsList[1],OptionsList[2],OptionsList[3])
                Options.add(ShuffledOptions)
            }
            LiveTestAdapter=LiveTestAdapter(this,QuestionList,QuestionIDList,CorrectIndexList,Options,SaveAllowed)
            LiveQuizRV.adapter=LiveTestAdapter
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
            Toast.makeText(requireContext(),"No more Questions Left",Toast.LENGTH_SHORT).show()
        }
    }
    fun movetoPrevQuestion(currentQuestion: Int,totalQuestions: Int){
        if(currentQuestion!=0){
            QuestionTracker--
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
            SaveListQuestionTrack.clear()
            Toast.makeText(requireContext(),"Test Ended",Toast.LENGTH_SHORT).show()
            val Unanswered=totalQuestions-CorrectAnswers-WrongAnswers
            val bundle=Bundle()
            bundle.putInt("TotalQuestions",totalQuestions)
            bundle.putInt("CorrectAnswers",CorrectAnswers)
            bundle.putInt("WrongAnswers",WrongAnswers)
            bundle.putInt("Unanswered",Unanswered)
            bundle.putString("QuizSetID",QuizSetID)
            bundle.putString("RoomName",RoomName)
            bundle.putString("ValidTill",ValidTill)
            dialog.dismiss()
            ReplaceandEmptyFrag(TestResults(),bundle)
        }
        No.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    fun Confirmation_Dialog(totalQuestions:Int,CorrectAnswers:Int,WrongAnswers:Int){
        val Unanswered=totalQuestions-CorrectAnswers-WrongAnswers
        bun_dle=Bundle()
        bun_dle.putInt("TotalQuestions",totalQuestions)
        bun_dle.putInt("CorrectAnswers",CorrectAnswers)
        bun_dle.putInt("WrongAnswers",WrongAnswers)
        bun_dle.putInt("Unanswered",Unanswered)
        bun_dle.putString("QuizSetID",QuizSetID)
        bun_dle.putString("RoomName",RoomName)
        bun_dle.putString("ValidTill",ValidTill)
        val dialog=ConfirmationDialog.newInstance(totalQuestions.toString(),CorrectAnswers.toString(),WrongAnswers.toString(),Unanswered.toString(),QuizSetID,RoomName,ValidTill)
        dialog.show(parentFragmentManager,"Confirmation_Dialog")
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
        GlobalFragVM.currentFragment=frag
        requireActivity().supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        Data.putString("QuizSetID",QuizSetID)
        Data.putInt("Height",originalFrameHeight)
        frag.arguments=Data
        frag.arguments = Data

        transaction.replace(R.id.FrameLayout, frag)
        transaction.commit()
    }

    fun AddQuestion(savedNameList:MutableList<SavedCollectionModel>,QuestionID:String,Position:Int){
        val newList=savedNameList
        val currentModule=newList[Position]
        currentModule.QuestionIDs = currentModule.QuestionIDs.toMutableList().apply {
            add(QuestionID)
        }
        newList[Position] = currentModule
        val updatedSavedList = newList.map { it.toMap() }
        val UID_Ref=FireStore.getFireStore().collection("UIDs").document(Constants.UID)
        UID_Ref.update("SavedLists", updatedSavedList)
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"Question Added Succesfully",Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "savedlist field updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating savedlist field", e)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        //requireActivity().findViewById<BottomNavigationView>(R.id.BottomNavBar).visibility = View.VISIBLE

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