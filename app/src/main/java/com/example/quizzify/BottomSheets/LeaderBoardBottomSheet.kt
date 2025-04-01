package com.example.quizzify.BottomSheets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quizzify.Adapters.LeaderBoardAdapter
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.LeaderBoardViewModelFactory
import com.example.quizzify.ViewModels.LeaderBoardViewModel
import com.example.quizzify.datamodels.LeaderBoardModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.imageview.ShapeableImageView
import javax.inject.Inject

class LeaderBoardBottomSheet:BottomSheetDialogFragment(){
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    @Inject
    lateinit var FireStore: FireStoreInstance

    @Inject
    lateinit var LeaderBoardVMFactory:LeaderBoardViewModelFactory

    lateinit var LeaderBoardAdapter:LeaderBoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as QuizApplication).applicationComponent.injectLeaderBoardBottomSheet(this)
       // setStyle(STYLE_NORMAL,R.style.BottomSheet_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val QuizID= arguments?.getString("QuestionID")
        val QuizSetName=arguments?.getString("QuizSetName")
        val RV=view.findViewById<RecyclerView>(R.id.leaderboardRecyclerView)
        val CloseBtn=view.findViewById<ImageButton>(R.id.closeButton)
        val NSView=view.findViewById<NestedScrollView>(R.id.bottomSheet)

        val ContestName=view.findViewById<TextView>(R.id.contestNameText)

        val FirstPlaceImg=view.findViewById<ShapeableImageView>(R.id.firstPlaceImage)
        val FirstPlaceName=view.findViewById<TextView>(R.id.firstPlaceName)
        val FirstPlaceScore=view.findViewById<TextView>(R.id.firstPlaceScore)
        val FirstPlaceLabel=view.findViewById<TextView>(R.id.FirstPlaceLabel)

        val SecondPlaceImg=view.findViewById<ShapeableImageView>(R.id.secondPlaceImage)
        val SecondPlaceName=view.findViewById<TextView>(R.id.secondPlaceName)
        val SecondPlaceScore=view.findViewById<TextView>(R.id.secondPlaceScore)
        val SecondPlaceLabel=view.findViewById<TextView>(R.id.SecondPlaceLabel)

        val ThirdPlaceImg=view.findViewById<ShapeableImageView>(R.id.thirdPlaceImage)
        val ThirdPlaceName=view.findViewById<TextView>(R.id.thirdPlaceName)
        val ThirdPlaceScore=view.findViewById<TextView>(R.id.thirdPlaceScore)
        val ThirdPlaceLabel=view.findViewById<TextView>(R.id.ThirdPlaceLabel)

        bottomSheetBehavior = BottomSheetBehavior.from(NSView)
        bottomSheetBehavior.isDraggable=false
        bottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }
        val windowHeight = resources.displayMetrics.heightPixels
        NSView.layoutParams.height = (windowHeight * 0.75).toInt()

        ContestName.text=QuizSetName
        CloseBtn.setOnClickListener {
            dismiss()
        }
        val LeaderBoardVM:LeaderBoardViewModel by viewModels {
            LeaderBoardVMFactory
        }
        Log.d("LeaderBoard",QuizID.toString())
        LeaderBoardVM.getQuizId(QuizID.toString())
        RV.layoutManager=LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        LeaderBoardAdapter= LeaderBoardAdapter(this, mutableListOf())
        RV.adapter=LeaderBoardAdapter
        LeaderBoardVM._LeaderBoard_List.observe(viewLifecycleOwner){
            Log.d("LeaderBoard",it.toString())
            val sortedList=sortLeaderboardByScoreDescending(it)
            val (topThree, rest) = splitLeaderboard(sortedList)
            FirstPlaceImg.visibility=View.GONE
            SecondPlaceImg.visibility=View.GONE
            ThirdPlaceImg.visibility=View.GONE
            FirstPlaceScore.visibility=View.GONE
            SecondPlaceScore.visibility=View.GONE
            ThirdPlaceScore.visibility=View.GONE
            FirstPlaceName.visibility=View.GONE
            SecondPlaceName.visibility=View.GONE
            ThirdPlaceName.visibility=View.GONE
            FirstPlaceLabel.visibility=View.GONE
            SecondPlaceLabel.visibility=View.GONE
            ThirdPlaceLabel.visibility=View.GONE
            if(topThree.size>=1){
                FirstPlaceImg.visibility=View.VISIBLE
                FirstPlaceName.visibility=View.VISIBLE
                FirstPlaceScore.visibility=View.VISIBLE
                FirstPlaceLabel.visibility=View.VISIBLE

                FirstPlaceName.text=topThree[0].Name
                FirstPlaceScore.text="${topThree[0].Score}/${topThree[0].TotalScore}"
                if(topThree[0].ImgUrl!="Empty") {
                    Glide.with(requireContext()).load(topThree[0].ImgUrl).into(FirstPlaceImg)
                }

            }
            if(topThree.size>=2){
                SecondPlaceImg.visibility=View.VISIBLE
                SecondPlaceName.visibility=View.VISIBLE
                SecondPlaceScore.visibility=View.VISIBLE
                SecondPlaceName.text=topThree[1].Name
                SecondPlaceLabel.visibility=View.VISIBLE
                SecondPlaceScore.text="${topThree[1].Score}/${topThree[1].TotalScore}"
                if(topThree[1].ImgUrl!="Empty") {
                    Glide.with(requireContext()).load(topThree[1].ImgUrl).into(SecondPlaceImg)
                }
            }
            if(topThree.size>=3){
                ThirdPlaceImg.visibility=View.VISIBLE
                ThirdPlaceName.visibility=View.VISIBLE
                ThirdPlaceScore.visibility=View.VISIBLE
                ThirdPlaceName.text=topThree[2].Name
                ThirdPlaceLabel.visibility=View.VISIBLE
                ThirdPlaceScore.text="${topThree[2].Score}/${topThree[2].TotalScore}"
                if(topThree[2].ImgUrl!="Empty") {
                    Glide.with(requireContext()).load(topThree[2].ImgUrl).into(ThirdPlaceImg)
                }
            }
        LeaderBoardAdapter.updateList(rest.toMutableList())
        }
    }
    companion object{
        fun newInstance(QuizID:String,QuizSetName:String):LeaderBoardBottomSheet{
            val args=Bundle()
            args.putString("QuestionID",QuizID)
            args.putString("QuizSetName",QuizSetName)
            val fragment=LeaderBoardBottomSheet()
            fragment.arguments=args
            return fragment
        }
    }
   private fun sortLeaderboardByScoreDescending(leaderboardList: List<LeaderBoardModel>): List<LeaderBoardModel> {
        return leaderboardList.sortedByDescending { it.Score }
    }
    fun splitLeaderboard(leaderboardList: List<LeaderBoardModel>): Pair<List<LeaderBoardModel>, List<LeaderBoardModel>> {
        val topThree = leaderboardList.take(3)
        val rest = leaderboardList.drop(3)
        return Pair(topThree, rest)
    }
}