package com.example.quizzify.Fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.RecentContestAdapter
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.SignIn
import com.example.quizzify.ViewModelFactories.BasicInfoViewModelFactory
import com.example.quizzify.ViewModelFactories.RecentContestViewModelFactory
import com.example.quizzify.ViewModels.BasicInfoViewModel
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.example.quizzify.ViewModels.RecentContestViewModel
import com.example.quizzify.datamodels.DaysActiveDataModel
import com.example.quizzify.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import org.w3c.dom.Text
import java.time.YearMonth
import javax.inject.Inject


class ProfileFragment : Fragment() {

    private lateinit var QuestionAttempted: TextView
    private lateinit var CorrectAnswer:TextView
    private lateinit var Accuracy:TextView
    private lateinit var ContestParticipated:TextView
    private lateinit var Achievements:TextView
    private lateinit var LongestStreak:TextView
    private lateinit var Rank:TextView
    private lateinit var Grid: GridLayout
    private lateinit var PastContests:RecyclerView
    private lateinit var AchievementList:RecyclerView
    private lateinit var Level:TextView
    private lateinit var ProfileImg:ShapeableImageView
    private lateinit var Name:TextView
    private lateinit var UID:TextView
    private lateinit var SignOutButton:MaterialButton

    @Inject
    lateinit var FireStore:FireStoreInstance

    @Inject
    lateinit var BasicInfoVMFactory:BasicInfoViewModelFactory

    @Inject
    lateinit var RecentContest:RecentContestViewModelFactory

    lateinit var RCAdapter:RecentContestAdapter



    val BasicInfoVM: BasicInfoViewModel by viewModels{
        BasicInfoVMFactory
    }

    val RecentContestVM:RecentContestViewModel by viewModels{
        RecentContest
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity().application as QuizApplication).applicationComponent.injectProfileFrag(this)
        QuestionAttempted=view.findViewById(R.id.questionsAttemptedTextView)
        CorrectAnswer=view.findViewById(R.id.correctAnswersTextView)
        Accuracy=view.findViewById(R.id.accuracyTextView)
        ContestParticipated=view.findViewById(R.id.contestsParticipatedTextView)
        Achievements=view.findViewById(R.id.achievementsCountTextView)
        LongestStreak=view.findViewById(R.id.streakTextView)
        Rank=view.findViewById(R.id.rankTextView)
        Grid=view.findViewById(R.id.calendarGridLayout)
        PastContests=view.findViewById(R.id.recentContestsRecyclerView)
        AchievementList=view.findViewById(R.id.achievementsRecyclerView)
        Level=view.findViewById(R.id.levelBadge)
        ProfileImg=view.findViewById(R.id.profileImageView)
        Name=view.findViewById(R.id.nameTextView)
        UID=view.findViewById(R.id.usernameTextView)
        SignOutButton=view.findViewById(R.id.signUpButton)
        AchievementList.visibility=View.GONE


        SetUpBasicInfo()
        RecentContests()
        GetDayData()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount == 0) {
                    requireActivity().finishAffinity() // 🔥 Ensures complete exit
                } else {
                    fragmentManager.popBackStack() // ✅ Properly handles back navigation
                }
            }
        })
        SignOutButton.setOnClickListener {
            signOutGoogle(requireContext())
        }
    }
   private fun SetUpBasicInfo(){
       BasicInfoVM.getBasicInfo()
       BasicInfoVM.BasicInfo.observe(viewLifecycleOwner){
           if(it!=null && it.isNotEmpty()){
               val item=it[0]
               Log.d("ProfileFragment",item.toString())
               QuestionAttempted.text=item.Questions_Attempted.toString()
               CorrectAnswer.text=item.Correct_Answers.toString()
               Name.text=item.Name
               UID.text="@"+item.UID
               ContestParticipated.text=item.Contest_Participated.toString()
               Achievements.text=item.Achievements.toString()
               Level.text=item.Level.toString()
               Rank.text="#Rank "+item.Rank.toString()
               Accuracy.text=((item.Correct_Answers.toFloat() / item.Questions_Attempted.toFloat()) * 100).toString() + "%"
           }
       }
   }
    private fun RecentContests(){
        RecentContestVM.getRecentContest()
        RCAdapter=RecentContestAdapter(this, mutableListOf())
        PastContests.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        PastContests.adapter=RCAdapter
        RecentContestVM._RecentContest.observe(viewLifecycleOwner){
            if(it.isNotEmpty())
                RCAdapter.UpdateRV(it.toMutableList())
        }
    }
     fun SetDays(callback:(List<DaysActiveDataModel>)->Unit) {
         val UID_Ref = FireStore.getFireStore().collection("UIDInfo").document(Constants.UID).get()
             .addOnSuccessListener {
                 if (it.exists()) {
                     val Days = it.get("DaysActive") as? MutableList<HashMap<String, Any>>
                         ?: mutableListOf()
                     val ActiveDays = Days.map { map ->
                         DaysActiveDataModel(
                             Day = map["Day"].toString(),
                             QuestionsSolved = map["QuestionsSolved"].toString().toInt()
                         )
                     }
                     callback(ActiveDays)
                 } else {
                     callback(emptyList())
                 }
             }.addOnFailureListener {
             callback(emptyList())
         }

     }
    private fun GetDayData(){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val daysInMonth = YearMonth.of(year, month + 1).lengthOfMonth()

        calendar.set(year, month, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        SetDays { dayList ->
            Log.d("CallbackData", "Received data: $dayList") // ✅ Debugging received data
            setUpgridLayout(daysInMonth, firstDayOfWeek, dayList)
        }
    }
   private fun setUpgridLayout(daysInMonth: Int, firstDayOfWeek: Int, DayList: List<DaysActiveDataModel>){
        Grid.removeAllViews()
        for (i in 0 until firstDayOfWeek) {
            val emptyCell = View(requireContext()).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = resources.getDimensionPixelSize(R.dimen.calendar_cell_size)
                    columnSpec = GridLayout.spec(i % 7, 1f)
                    rowSpec = GridLayout.spec(i / 7, 1f)
                }
               setBackgroundResource(R.drawable.calendar_cell_level0)
            }
            Grid.addView(emptyCell)
        }
        for (day in 1..daysInMonth) {
            // Find matching entry in the list; if not found, default to 0
            val activityCount = DayList.find { it.Day == day.toString() }?.QuestionsSolved ?: 0

            val cellView = View(requireContext()).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = resources.getDimensionPixelSize(R.dimen.calendar_cell_size)
                    columnSpec = GridLayout.spec((day + firstDayOfWeek - 1) % 7, 1f)
                    rowSpec = GridLayout.spec((day + firstDayOfWeek - 1) / 7, 1f)
                    setMargins(2, 2, 2, 2)
                }
                background = getDrawableForActivityLevel(activityCount)
                contentDescription = "Day $day: $activityCount questions solved"
            }

            Grid.addView(cellView)
        }

    }
    private fun getDrawableForActivityLevel(activityCount: Int): Drawable? {
        return when {
            activityCount > 50 -> ContextCompat.getDrawable(requireContext(), R.drawable.calendar_cell_level4)
            activityCount > 30 -> ContextCompat.getDrawable(requireContext(), R.drawable.calendar_cell_level3)
            activityCount > 10 -> ContextCompat.getDrawable(requireContext(), R.drawable.calendar_cell_level2)
            activityCount >= 1 -> ContextCompat.getDrawable(requireContext(), R.drawable.calendar_cell_level1)
            else -> ContextCompat.getDrawable(requireContext(), R.drawable.calendar_cell_level0)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount == 0) {
                    requireActivity().finishAffinity() // 🔥 Ensures complete exit
                } else {
                    fragmentManager.popBackStack() // ✅ Properly handles back navigation
                }
            }
        })
    }
    private fun signOutGoogle(context:Context){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInClient.signOut().addOnCompleteListener {
            if(it.isSuccessful){
                Constants.UID="Abhishek123"
                cleartheStack(context as AppCompatActivity)
                val intent=Intent(context, SignIn::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                context.finish()
            }
        }
    }
    private fun cleartheStack(activity:AppCompatActivity){
        val fragManager=activity.supportFragmentManager
        fragManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

}