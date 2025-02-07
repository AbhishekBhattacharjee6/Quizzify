package com.example.quizzify.Fragments

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.quizzify.Adapters.PreRegRoomAdapter
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.IndividualQuestionViewModelFactory
import com.example.quizzify.ViewModelFactories.PreRegViewModelFactory
import com.example.quizzify.ViewModelFactories.QuestionListViewModelFactory
import com.example.quizzify.ViewModels.IndividualQuestionViewModel
import com.example.quizzify.ViewModels.PreRegRoomViewModel
import com.example.quizzify.ViewModels.QuestionListViewModel
import com.example.quizzify.datamodels.QuestionModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import javax.inject.Inject


class PreRegisteredRooms : Fragment() {

    @Inject
    lateinit var PreRegRoomVMFactory:PreRegViewModelFactory

    lateinit var PreRegAdapter:PreRegRoomAdapter

    @Inject
    lateinit var QuestionListVMFactory: QuestionListViewModelFactory

    @Inject
    lateinit var IndividualQuestionVMFactory: IndividualQuestionViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pre_registered_rooms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val RegisteredRooms=view.findViewById<RecyclerView>(R.id.RegisteredRooms)
        val SearchView=view.findViewById<SearchView>(R.id.PrivateRoomSearch)
        (requireActivity().application as QuizApplication).applicationComponent.injectPreRegRooms(this)
        val PreRegVM:PreRegRoomViewModel by viewModels { PreRegRoomVMFactory }
        PreRegAdapter=PreRegRoomAdapter(this, emptyList())
        RegisteredRooms.layoutManager=object :LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false){
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        RegisteredRooms.adapter=PreRegAdapter
        PreRegVM.PreRegRoomList.observe(viewLifecycleOwner){
            Log.d("PreRegRoomList",it.toString())
            PreRegAdapter.updateList(it)
        }
        SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
               if (p0.isNullOrEmpty()){
                   Toast.makeText(requireContext(),"Enter a Valid Room Name",Toast.LENGTH_SHORT).show()
               }
                else{
                    val Data=Bundle();
                   Data.putString("RoomtoSearch",p0)
                   ReplaceFrag(SearchRoom(),Data)
               }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
        PreRegAdapter.setOnItemClickListener(object :PreRegRoomAdapter.itemListener{
            override fun onItemClick(position: Int,StartFrom:String,ValidTill:String,Duration:String,RoomName:String,CreatorUID:String,TotalQuestions:String,QuizSetID:String) {
                showDialog(StartFrom,ValidTill,Duration,RoomName,CreatorUID,TotalQuestions,QuizSetID)
            }
        })
    }
    fun showDialog(StartFrom:String,ValidTill:String,Duration:String,RoomName:String,CreatorUID:String,TotalQuestions:String,QuizSetID:String){
        val dialog=Dialog(requireContext())
        dialog.setContentView(R.layout.golive_dialog)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        val roomName=dialog.findViewById<TextView>(R.id.textView25)
        val creatorID=dialog.findViewById<TextView>(R.id.textView27)
        val duration=dialog.findViewById<TextView>(R.id.textView34)
        val startFrom=dialog.findViewById<TextView>(R.id.textView30)
        val validTill=dialog.findViewById<TextView>(R.id.textView32)
        val GoLiveBtn=dialog.findViewById<Button>(R.id.button9)
        roomName.text=RoomName
        creatorID.text=CreatorUID
        duration.text=Duration
        startFrom.text=StartFrom
        validTill.text=ValidTill
        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        var month=calendar.get(Calendar.MONTH)+1
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        var new_month=(month).toString().padStart(2,'0')
        var new_day=(day).toString().padStart(2,'0')
        val date=year.toString()+new_month+new_day
        Log.d("Date",date)
        Log.d("StartFrom",StartFrom)
        Log.d("ValidTill",ValidTill)
        if(date.toInt()>=StartFrom.toInt() && date.toInt()<=ValidTill.toInt()) {
            GoLiveBtn.text="Go Live"
            GoLiveBtn.setBackgroundResource(R.color.neon_green)
            GoLiveBtn.setOnClickListener {
                dialog.dismiss()
                val QuestionListVM: QuestionListViewModel by viewModels {
                    QuestionListVMFactory
                }
                val IndividualQuestionVM: IndividualQuestionViewModel by viewModels{
                    IndividualQuestionVMFactory
                }
                var Questions = mutableListOf<QuestionModel>()
                QuestionListVM.getQuestionList(QuizSetID.toString())
                QuestionListVM.questionList.observe(viewLifecycleOwner) {
                    if (it.isNotEmpty()) {
                        Log.d("QuestionList", it.toString())
                        val Circulardialog = Dialog(requireContext())
                        Circulardialog.setContentView(R.layout.progress_dialog_box)
                        Circulardialog.setCancelable(true)
                        val CircularProgressBar =
                            Circulardialog.findViewById<CircularProgressBar>(R.id.circularProgressBar)
                        var QuestionCount = 0;
                        val TotalQuestions = it.size.toString()
                        Circulardialog.show()
                        IndividualQuestionVM.getQuestions(it)
                        IndividualQuestionVM.questions.observe(viewLifecycleOwner) { questionsList ->
                            if (questionsList.size == it.size) {
                                Questions.addAll(questionsList)//
                                Log.d("AllQuestions", Questions.toString())// // Wait until all questions are fetched
                                Circulardialog.dismiss()
                                val Data=Bundle()
                                Data.putParcelableArrayList("QuestionList",ArrayList(Questions))
                                Data.putString("Duration",Duration)
                                ReplaceFrag(LiveContest(),Data)
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "No Questions Found", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        else if(date.toInt()>ValidTill.toInt()){
            GoLiveBtn.text="Room Expired"
            GoLiveBtn.setBackgroundResource(R.color.neon_red)
        }
        else{
            GoLiveBtn.text="Room Not Live Yet"
            GoLiveBtn.setBackgroundResource(R.color.neon_grey)
        }
        dialog.show()
    }
    fun ReplaceFrag(Frag:Fragment,Data:Bundle){
        Frag.arguments=Data
        val fragManager=parentFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        fragTransaction.addToBackStack(null)
        fragTransaction.replace(R.id.FrameLayout,Frag)
        fragTransaction.commit()
    }
}