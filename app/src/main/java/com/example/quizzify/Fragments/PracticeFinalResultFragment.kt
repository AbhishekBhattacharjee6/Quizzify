package com.example.quizzify.Fragments

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.icu.util.Calendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import app.futured.donut.DonutProgressView
import app.futured.donut.DonutSection
import com.example.quizzify.BottomNavBar.CustomBottomNavigation
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.example.quizzify.datamodels.DaysActiveDataModel
import com.example.quizzify.datamodels.RecentContestDataModel
import com.example.quizzify.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PracticeFinalResultFragment : Fragment() {

    @Inject
    lateinit var FireStore: FireStoreInstance

    private lateinit var GlobalFragVM: GlobalFragViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.test_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity().application as QuizApplication).applicationComponent.injectPracticeFinalResult(this)
        val HeaderText=view.findViewById<TextView>(R.id.HeaderText)
        val progressBackground=view.findViewById<CircularProgressIndicator>(R.id.progressBackground)
        val progressUnanswered=view.findViewById<CircularProgressIndicator>(R.id.progressUnanswered)
        val progressCorrect=view.findViewById<CircularProgressIndicator>(R.id.progressCorrect)
        val progressWrong=view.findViewById<CircularProgressIndicator>(R.id.progressWrong)
        val CorrectAnswerTV=view.findViewById<TextView>(R.id.tvCorrectCount)
        val TotalQuestionsTV=view.findViewById<TextView>(R.id.tvtotalCount)
        val CorrectLegend=view.findViewById<TextView>(R.id.tvCorrectLegend)
        val WrongLegend=view.findViewById<TextView>(R.id.tvWrongLegend)
        val UnansweredLegend=view.findViewById<TextView>(R.id.tvSkippedLegend)
        val TimeSpent=view.findViewById<TextView>(R.id.tvTimeSpent)
        val Accuracy=view.findViewById<TextView>(R.id.tvAccuracy)
        val Ranking=view.findViewById<TextView>(R.id.tvRank)
        val TotalParticipants=view.findViewById<TextView>(R.id.tvTotalParticipants)
        val LeaderBoardBtn=view.findViewById<Button>(R.id.btnLeaderboard)
        val ShareBtn=view.findViewById<Button>(R.id.btnShare)
        val Confetti=view.findViewById<KonfettiView>(R.id.konfettiView)
        val TryAgainBtn=view.findViewById<Button>(R.id.btnTryAgain)
        val Review=view.findViewById<Button>(R.id.btnReviewAnswers)

        HeaderText.text="Practice Complete!"

        GlobalFragVM= ViewModelProvider(this)[GlobalFragViewModel::class.java]


        LeaderBoardBtn.visibility=View.GONE
        ShareBtn.visibility=View.GONE
        TryAgainBtn.visibility=View.GONE
        Review.visibility=View.GONE

        val total=arguments?.getInt("TotalQuestions")!!
        val correct=arguments?.getInt("CorrectAnswers")!!
        val wrong=arguments?.getInt("WrongAnswers")!!
        val unanswered=arguments?.getInt("Unanswered")!!
        val Height=arguments?.getInt("Height")

        UpdateBasicStats((correct+wrong).toInt(),correct.toInt())
        MarkDay((correct+wrong))

        CorrectAnswerTV.text=correct.toString()
        TotalQuestionsTV.text="out of "+total.toString()
        CorrectLegend.text=correct.toString()
        WrongLegend.text=wrong.toString()
        UnansweredLegend.text=unanswered.toString()
        Accuracy.text=(calculateAccuracy(correct,total))

        val correctPercent = (correct.toFloat() / total * 100).toInt()
        val wrongPercent = (wrong.toFloat() / total * 100).toInt()
        val unansweredPercent = (unanswered.toFloat() / total * 100).toInt()

        progressUnanswered.progress = 0
        progressWrong.progress = 0
        progressCorrect.progress = 0

        progressCorrect.setProgressCompat(correctPercent,true)

        Handler(Looper.getMainLooper()).postDelayed({
            progressWrong.setProgressCompat(wrongPercent, true)
        }, 300)

        Handler(Looper.getMainLooper()).postDelayed({
            progressUnanswered.setProgressCompat(unansweredPercent, true)

            // Show confetti after all animations complete
            Handler(Looper.getMainLooper()).postDelayed({
                if((calculateAccuracy(correct,total)).toFloat().toInt()>55){
                    showConfetti(Confetti)
                }
            }, 500)
        }, 600)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back press logic here
                EmptyStackandNavigate(LiveQuizFragment(), Height!!)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
    }
    fun calculateAccuracy(correct: Int, total: Int): String {
        if (total == 0) return "0.00" // Avoid division by zero
        val percentage = (correct.toDouble() / total.toDouble()) * 100
        val formatted = String.format("%.2f", percentage)
        return formatted
    }
    private fun showConfetti(konfettiView: KonfettiView){
        val leftCornerParty = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 60, // More focused spread
            angle = 135, // Angle pointing down-right
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map { Color.parseColor("#$it") },
            emitter = Emitter(duration = 2, TimeUnit.SECONDS).max(200),
            position = Position.Relative(0.0, 0.0) // Top-left corner
        )

// Add another burst from the other side after a delay
        val rightCornerParty = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 60, // More focused spread
            angle = 45, // Angle pointing down-left
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def).map { Color.parseColor("#$it") },
            emitter = Emitter(duration = 2, TimeUnit.SECONDS).max(200),
            position = Position.Relative(1.0, 0.0) // Top-right corner
        )

        konfettiView.start(leftCornerParty)
        konfettiView.start(rightCornerParty)

// Add additional bursts after a delay for a more continuous effect
        Handler(Looper.getMainLooper()).postDelayed({
            konfettiView.start(leftCornerParty)
            konfettiView.start(rightCornerParty)
        }, 1500)
    }
    private fun EmptyStackandNavigate(frag:Fragment,originalFrameHeight:Int){
        val frameLayout = requireActivity().findViewById<FrameLayout>(R.id.FrameLayout)
        val layoutParams = frameLayout.layoutParams
        layoutParams.height = originalFrameHeight
        frameLayout.layoutParams = layoutParams
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        GlobalFragVM.currentFragment=frag

        val fragments = requireActivity().supportFragmentManager.fragments
        for (fragment in fragments) {
            transaction.remove(fragment)
        }

        requireActivity().supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        transaction.replace(R.id.FrameLayout, frag)

        transaction.commit()


    }
    override fun onDestroy() {
        super.onDestroy()
        requireActivity().findViewById<CustomBottomNavigation>(R.id.bottomNavigation).visibility = View.VISIBLE
       requireActivity().findViewById<CustomBottomNavigation>(R.id.bottomNavigation).selectTab(CustomBottomNavigation.TAB_LIVE_CONTEST)

        // Restore FrameLayout size
        //val frameLayout = requireActivity().findViewById<FrameLayout>(R.id.FrameLayout)
        // val layoutParams = frameLayout.layoutParams
        // layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT  // Restore original size
        // frameLayout.layoutParams = layoutParams
        // requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
    fun getAppUsableScreenHeight(activity: Activity): Int {
        val rect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        return rect.height() // Returns the height excluding status bar
    }
    private fun MarkDay(totalAnswered: Int) {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString() // Convert to String for consistency

        val UID_Ref = FireStore.getFireStore().collection("UIDInfo").document(Constants.UID)

        FireStore.getFireStore().runTransaction { transaction ->
            val snapshot = transaction.get(UID_Ref)

            val DayList = snapshot.get("DaysActive") as? MutableList<HashMap<String, Any>> ?: mutableListOf()

            val LastDay = DayList.lastOrNull()?.let { map ->
                DaysActiveDataModel(
                    Day = map["Day"] as? String ?: "",
                    QuestionsSolved = (map["QuestionsSolved"] as? Long)?.toInt() ?: 0
                )
            }

            if (LastDay != null && LastDay.Day == day) {
                // Update existing day
                LastDay.QuestionsSolved += totalAnswered
                DayList[DayList.size - 1] = LastDay.toMap() // Modify the last element directly
            } else {
                // Add a new entry for today
                val NewDay = DaysActiveDataModel(day, totalAnswered)
                DayList.add(NewDay.toMap())
            }

            // Update Firestore atomically inside the transaction
            transaction.update(UID_Ref, "DaysActive", DayList)
        }.addOnSuccessListener {
            Log.d("DaysActive", "Updated successfully")
        }.addOnFailureListener {
            Log.d("DaysActive", "Update failed")
        }
    }

    private fun UpdateBasicStats(AttemptedQuestion:Int,Correct_Answers:Int){
        val db=FireStore.getFireStore()
        val Ref=db.collection("UIDInfo").document(Constants.UID)
        Ref.get().addOnSuccessListener {
            var _QuestionAttempted=it.get("Questions Attempted").toString().toInt()
            var _CorrectAnswers=it.get("Correct Answers").toString().toInt()
            var _ContestParticipated=it.get("Contest Participated").toString().toInt()
            _QuestionAttempted+=AttemptedQuestion
            _CorrectAnswers+=Correct_Answers
            _ContestParticipated+=1
            Ref.update("Questions Attempted",_QuestionAttempted)
            Ref.update("Correct Answers",_CorrectAnswers)
            Ref.update("Contest Participated",_ContestParticipated)
        }.addOnFailureListener {
            Log.d("BasicStats","Failed")
        }
    }
}