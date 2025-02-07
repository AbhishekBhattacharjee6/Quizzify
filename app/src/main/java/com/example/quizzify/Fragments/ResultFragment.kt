package com.example.quizzify.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.quizzify.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar


class ResultFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val totalProgress= arguments?.getString("Total")
        val userProgress= arguments?.getString("Correct")
        val circularProgressBar=view.findViewById<CircularProgressBar>(R.id.circularProgressBar)
        val LeaderBoard=view.findViewById<Button>(R.id.LeaderBoard)
        circularProgressBar.apply {
            setProgressWithAnimation(userProgress!!.toFloat(),1000)
            progressMax=totalProgress!!.toFloat()

            progressBarColor=ContextCompat.getColor(context,R.color.neon_green)
            progressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            backgroundProgressBarColorStart= ContextCompat.getColor(context,R.color.neon_red)
           // backgroundProgressBarColorEnd= ContextCompat.getColor(context,R.color.neon_green)
            backgroundProgressBarColorDirection = CircularProgressBar.GradientDirection.TOP_TO_BOTTOM

            progressBarWidth=7f
            backgroundProgressBarWidth=3f
        }
        LeaderBoard.setOnClickListener {
            showLeaderBoardDialog()
        }
    }
    fun showLeaderBoardDialog(){

    }
}