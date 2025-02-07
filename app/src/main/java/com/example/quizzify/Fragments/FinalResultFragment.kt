package com.example.quizzify.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import app.futured.donut.DonutProgressView
import app.futured.donut.DonutSection
import com.example.quizzify.R


class FinalResultFragment : Fragment() {


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
        val ResView=view.findViewById<DonutProgressView>(R.id.donut_view)
        val totalQuestions=arguments?.getInt("TotalQuestions")
        val CorrectAnswers=arguments?.getInt("CorrectAnswers")
        val WrongAnswers=arguments?.getInt("WrongAnswers")
        val Unanswered=arguments?.getInt("Unanswered")

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
}