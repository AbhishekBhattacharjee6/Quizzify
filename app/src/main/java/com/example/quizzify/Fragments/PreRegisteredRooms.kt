package com.example.quizzify.Fragments

import android.app.Dialog
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
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.quizzify.Adapters.LeaderBoardAdapter
import com.example.quizzify.Adapters.LeaderBoardPreRegAdapter
import com.example.quizzify.Adapters.PreRegRoomAdapter
import com.example.quizzify.Dialogs.QuestionLoaderDialog
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.SimpletonObjects.SaveListQuestionTrack
import com.example.quizzify.ViewModelFactories.IndividualQuestionViewModelFactory
import com.example.quizzify.ViewModelFactories.LeaderBoardViewModelFactory
import com.example.quizzify.ViewModelFactories.PreRegViewModelFactory
import com.example.quizzify.ViewModelFactories.QuestionListViewModelFactory
import com.example.quizzify.ViewModels.GlobalFragViewModel
import com.example.quizzify.ViewModels.IndividualQuestionViewModel
import com.example.quizzify.ViewModels.LeaderBoardViewModel
import com.example.quizzify.ViewModels.PreRegRoomViewModel
import com.example.quizzify.ViewModels.QuestionListViewModel
import com.example.quizzify.datamodels.QuestionModel
import com.example.quizzify.datamodels.QuizSetModel
import com.example.quizzify.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import okhttp3.internal.Util
import javax.inject.Inject


class PreRegisteredRooms : Fragment() {

    @Inject
    lateinit var PreRegRoomVMFactory: PreRegViewModelFactory

    private val PreRegVM: PreRegRoomViewModel by viewModels { PreRegRoomVMFactory }

    lateinit var PreRegAdapter: PreRegRoomAdapter

    @Inject
    lateinit var QuestionListVMFactory: QuestionListViewModelFactory

    @Inject
    lateinit var FireStore: FireStoreInstance

    @Inject
    lateinit var LeaderBoardVMFactory: LeaderBoardViewModelFactory

    lateinit var LeaderBoardAdapter: LeaderBoardPreRegAdapter

    @Inject
    lateinit var IndividualQuestionVMFactory: IndividualQuestionViewModelFactory

    private lateinit var GlobalFragVM: GlobalFragViewModel


    lateinit var dialog: BottomSheetDialog

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
        val RegisteredRooms = view.findViewById<RecyclerView>(R.id.RegisteredRooms)
        val SearchView = view.findViewById<SearchView>(R.id.PrivateRoomSearch)
        (requireActivity().application as QuizApplication).applicationComponent.injectPreRegRooms(
            this
        )

        GlobalFragVM= ViewModelProvider(this)[GlobalFragViewModel::class.java]

        PreRegAdapter = PreRegRoomAdapter(this, mutableListOf())
        RegisteredRooms.layoutManager =
            object : LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) {
                override fun canScrollHorizontally(): Boolean {
                    return false
                }
            }
        RegisteredRooms.adapter = PreRegAdapter
        PreRegVM.PreRegRoomList.observe(viewLifecycleOwner) {
            Log.d("PreRegRoomList", it.toString())
            PreRegAdapter.updateList(it.toMutableList())
        }
        SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Enter a Valid Room Name", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val Data = Bundle();
                    Data.putString("RoomtoSearch", p0)
                    ReplaceFrag(SearchRoom(), Data)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
        SearchView.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if(hasFocus){
                SearchView.setBackgroundResource(R.drawable.focused_customized_search_view)
            }
            else{
                SearchView.setBackgroundResource(R.drawable.customized_search_view)
            }
        }
        PreRegAdapter.setOnItemClickListener(object : PreRegRoomAdapter.itemListener {
            override fun onItemClick(
                position: Int,
                StartFrom: String,
                ValidTill: String,
                Duration: String,
                RoomName: String,
                CreatorUID: String,
                TotalQuestions: String,
                QuizSetID: String,
                Attempted: Boolean,
                SaveAllowed: Boolean
            ) {
                GoLive(
                    StartFrom,
                    ValidTill,
                    Duration,
                    RoomName,
                    CreatorUID,
                    TotalQuestions,
                    QuizSetID,
                    Attempted,
                    SaveAllowed
                )
            }
        })
    }

    fun ReplaceFrag(Frag: Fragment, Data: Bundle) {
        Frag.arguments = Data
        val fragManager = parentFragmentManager
        GlobalFragVM.currentFragment=Frag
        val fragTransaction = fragManager.beginTransaction()
        fragTransaction.addToBackStack(null)
        fragTransaction.replace(R.id.FrameLayout, Frag)
        fragTransaction.commit()
    }

    fun EmptyUpandReplace(frag: Fragment, data: Bundle) {
        frag.arguments = data
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        GlobalFragVM.currentFragment=frag

        val fragments = requireActivity().supportFragmentManager.fragments
        for (fragment in fragments) {
            transaction.remove(fragment)
        }

        requireActivity().supportFragmentManager.popBackStackImmediate(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        transaction.replace(R.id.FrameLayout, frag)

        transaction.commitNow()
    }

    fun DeletePreRegRoom(position: Int, PreRegRooms: List<QuizSetModel>) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.endquizdialog)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        val Title = dialog.findViewById<TextView>(R.id.textView36)
        val Yes = dialog.findViewById<Button>(R.id.button11)
        val No = dialog.findViewById<Button>(R.id.button10)
        Title.text = "Are you sure you want to delete this room?"
        No.setOnClickListener {
            dialog.dismiss()
        }
        Yes.setOnClickListener {
            val UID_Ref = FireStore.getFireStore().collection("UIDs").document(Constants.UID)
            UID_Ref.get().addOnSuccessListener {
                if (it.exists()) {
                    val rooms = it.get("PreRegisteredRooms") as? MutableList<Map<String, Any>>
                    if (!rooms.isNullOrEmpty() && rooms.size > position) {
                        rooms.removeAt(position)
                        UID_Ref.update("PreRegisteredRooms", rooms).addOnSuccessListener {
                            val newPreRegList = PreRegRooms.toMutableList()
                            newPreRegList.removeAt(position)
                            PreRegAdapter.updateList(newPreRegList)
                            dialog.dismiss()
                            Toast.makeText(requireContext(), "Room Deleted", Toast.LENGTH_SHORT)
                                .show()
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Something Went Wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        dialog.show()
    }

    fun RefreshRecyclerView() {
        PreRegVM.Refresh()
    }

    fun GoLive(
        StartFrom: String,
        ValidTill: String,
        Duration: String,
        RoomName: String,
        CreatorUID: String,
        TotalQuestions: String,
        QuizSetID: String,
        Attempted: Boolean,
        SaveAllowed: Boolean
    ) {
        val QuestionListVM: QuestionListViewModel by viewModels {
            QuestionListVMFactory
        }
        val IndividualQuestionVM: IndividualQuestionViewModel by viewModels {
            IndividualQuestionVMFactory
        }
        val Questions = mutableListOf<QuestionModel>()

        // Show the loader dialog at the beginning
        val loaderDialog = QuestionLoaderDialog()
        loaderDialog.show(childFragmentManager, "QuestionLoaderDialog")

        // Track the start time for a minimum 3-second delay
        val startTime = System.currentTimeMillis()

        QuestionListVM.getQuestionList(QuizSetID)
        QuestionListVM.questionList.observe(viewLifecycleOwner) { questionList ->
            if (questionList.isNotEmpty()) {
                Log.d("QuestionList", questionList.toString())

                IndividualQuestionVM.getQuestions(questionList)
                IndividualQuestionVM.questions.observe(viewLifecycleOwner) { questionsList ->
                    if (questionsList.size == questionList.size) {

                        val UID_Ref = FireStore.getFireStore()
                            .collection("UIDs")
                            .document(Constants.UID)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    val savedListRaw = documentSnapshot.get("SavedLists")
                                    val savedListSize = (savedListRaw as? List<Map<String, Any>>
                                        ?: emptyList()).size

                                    SaveListQuestionTrack.clear()
                                    SaveListQuestionTrack.initialize(
                                        savedListSize,
                                        questionsList.size
                                    )

                                    Questions.addAll(questionsList)
                                    Log.d("AllQuestions", Questions.toString())

                                    val Data = Bundle().apply {
                                        putParcelableArrayList("QuestionList", ArrayList(Questions))
                                        putBoolean("SaveAllowed", SaveAllowed)
                                        putString("Duration", Duration)
                                        putString("QuizSetID", QuizSetID)
                                        putString("RoomName", RoomName)
                                        putString("ValidTill", ValidTill)
                                    }

                                    // Ensure a minimum 3-second wait before dismissing the dialog
                                    val elapsedTime = System.currentTimeMillis() - startTime
                                    val remainingTime = 3000 - elapsedTime

                                    Handler(Looper.getMainLooper()).postDelayed({
                                        loaderDialog.dismissLoader()
                                        EmptyUpandReplace(LiveContest(), Data)
                                    }, maxOf(remainingTime, 0))  // Ensures a minimum 3s wait
                                }
                            }
                    }
                }
            }
        }
    }
}