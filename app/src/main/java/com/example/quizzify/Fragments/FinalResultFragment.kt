package com.example.quizzify.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import app.futured.donut.DonutProgressView
import app.futured.donut.DonutSection
import com.example.quizzify.BottomNavBar.CustomBottomNavigation
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.datamodels.LeaderBoardModel
import com.example.quizzify.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FieldValue
import javax.inject.Inject


class FinalResultFragment : Fragment() {

    @Inject
    lateinit var FireStore:FireStoreInstance


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_final_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<CustomBottomNavigation>(R.id.bottomNavigation).visibility = View.GONE

        // Expand FrameLayout to full screen
        val frameLayout = requireActivity().findViewById<FrameLayout>(R.id.FrameLayout)
        val layoutParams = frameLayout.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        frameLayout.layoutParams = layoutParams
        (requireActivity().application as QuizApplication).applicationComponent.injectFinalResult(this)
        val ResView=view.findViewById<DonutProgressView>(R.id.donut_view)
        val TV=view.findViewById<TextView>(R.id.textView26)
        val totalQuestions=arguments?.getInt("TotalQuestions")
        val CorrectAnswers=arguments?.getInt("CorrectAnswers")
        val WrongAnswers=arguments?.getInt("WrongAnswers")
        val Unanswered=arguments?.getInt("Unanswered")

        val QuizSetID=arguments?.getString("QuizSetID")

        if (CorrectAnswers != null && totalQuestions!=null) {
            LeaderBoardEntry(QuizSetID.toString(),Constants.UID,Constants.Name,CorrectAnswers,totalQuestions)
        }

        var CorrectSection=((CorrectAnswers?.toFloat())!!/(totalQuestions)!!).toFloat()
        var WrongSection=((WrongAnswers?.toFloat())!!/(totalQuestions)!!).toFloat()
        var UnansweredSection=((Unanswered?.toFloat())!!/(totalQuestions)!!).toFloat()


        val CorrectedFormatSection=String.format("%.1f",CorrectSection)
        val WrongFormatSection=String.format("%.1f",WrongSection)
        val UnansweredFormatSection=String.format("%.1f",UnansweredSection)

        CorrectSection=CorrectedFormatSection.toFloat()
        WrongSection=WrongFormatSection.toFloat()
        UnansweredSection=UnansweredFormatSection.toFloat()

        val Section1=DonutSection(
            name="Correct",
            color = ContextCompat.getColor(requireContext(), R.color.neon_green),
            amount=CorrectSection
        )
        val Section2=DonutSection(
            name="Wrong",
            color = ContextCompat.getColor(requireContext(), R.color.neon_red),
            amount=WrongSection
        )
        val Section3=DonutSection(
            name="Unanswered",
            color = ContextCompat.getColor(requireContext(), R.color.neon_grey),
            amount=UnansweredSection
        )
        ResView.cap=1f
        ResView.submitData(listOf(Section1,Section2,Section3))

        TV.setText("$CorrectAnswers/$totalQuestions")
        TV.setOnClickListener {
            Toast.makeText(requireContext(),"Answered $CorrectAnswers out of $totalQuestions",Toast.LENGTH_SHORT).show()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back press logic here
                EmptyStackandNavigate(LiveQuizFragment())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
        Log.d("DonutProgress", "Submitting data: Correct=$CorrectSection, Wrong=$WrongSection, Unanswered=$UnansweredSection")
    }
    fun EmptyStackandNavigate(frag:Fragment){
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        val fragments = requireActivity().supportFragmentManager.fragments
        for (fragment in fragments) {
            transaction.remove(fragment)
        }

        requireActivity().supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        transaction.replace(R.id.FrameLayout, frag)

        transaction.commit()


    }
    fun LeaderBoardEntry(QuizSetID:String,userUID:String,userName:String,userScore:Int,totalScore:Int){
        val QuizID_Ref=FireStore.getFireStore().collection("Quizset").document(QuizSetID)
        val leaderboardEntry = LeaderBoardModel(
            UID = userUID,
            Name = userName,
            Score = userScore,
            TotalScore = totalScore,
            ImgUrl = ""
        )
        QuizID_Ref.update("LeaderBoard", FieldValue.arrayUnion(leaderboardEntry)).addOnSuccessListener {
            Log.d("LeaderBoard","Entry Added")
        }.addOnFailureListener {
            Log.d("LeaderBoard","Entry Failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().findViewById<CustomBottomNavigation>(R.id.bottomNavigation).visibility = View.VISIBLE

        // Restore FrameLayout size
        val frameLayout = requireActivity().findViewById<FrameLayout>(R.id.FrameLayout)
        val layoutParams = frameLayout.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT  // Restore original size
        frameLayout.layoutParams = layoutParams
    }

}