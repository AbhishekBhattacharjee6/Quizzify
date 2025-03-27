package com.example.quizzify.Fragments

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.SearchRoomAdapter
import com.example.quizzify.Dialogs.PreRegisterDialog
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.SearchRoomViewModelFactory
import com.example.quizzify.ViewModels.SearchRoomViewModel
import com.example.quizzify.datamodels.QuizSetModel
import com.example.quizzify.utils.Constants
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


class SearchRoom : Fragment() {

    @Inject
    lateinit var SearchRoomVMFactory:SearchRoomViewModelFactory

    lateinit var SearchRoomAdapter:SearchRoomAdapter

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
        return inflater.inflate(R.layout.fragment_search_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val RoomtoSearch= arguments?.getString("RoomtoSearch")
        Log.d("RoomtoSearch",RoomtoSearch.toString())
        val SearchRV=view.findViewById<RecyclerView>(R.id.SearchRoomRV)
        (requireActivity().application as QuizApplication).applicationComponent.injectSearchRoom(this)
        val SearchRoomVM:SearchRoomViewModel by viewModels{
            SearchRoomVMFactory
        }
        SearchRoomVM.getSearchResults(RoomtoSearch.toString())
        SearchRV.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        SearchRoomAdapter=SearchRoomAdapter(this, emptyList())
        SearchRV.adapter=SearchRoomAdapter
        SearchRoomVM.SearchResult.observe(viewLifecycleOwner){
            SearchRoomAdapter.updateList(it)
        }
        SearchRoomAdapter.setOnItemClickListener(object :SearchRoomAdapter.itemClickListener{
            override fun onItemClick(
                position: Int,
                RoomName: String,
                RoomCreatorName: String,
                PassCode: String,
                StartFrom: String,
                ValidTill: String,
                RoomId: String,
                Duration:String,
                QuizSetID:String,
                Attempted:Boolean,
                SaveAllowed:Boolean
            ) {
                //showPreRegDialog(RoomName,RoomCreatorName,PassCode,StartFrom,ValidTill,RoomId,Duration,QuizSetID,Attempted,SaveAllowed)
                val dialog = PreRegisterDialog.newInstance(RoomName,RoomCreatorName,PassCode,StartFrom,ValidTill,RoomId,Duration,QuizSetID,Attempted,SaveAllowed)
                dialog.show(childFragmentManager, "PreRegisterDialog")
            }


        })
    }
    fun showPreRegDialog(RoomName: String,
                         RoomCreatorName: String,
                         PassCode: String,
                         StartFrom: String,
                         ValidTill: String,
                         RoomId: String,
                         Duration:String,
                         QuizSetID:String,
                         Attempted:Boolean,
                         SaveAllowed:Boolean
                         ){
        val UID_Ref=FireStore.getFireStore().collection("UIDs").document(Constants.UID)
        val dialog=Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.preregister_dialog)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        val roomName=dialog.findViewById<TextView>(R.id.roomName)
        val roomCreatorName=dialog.findViewById<TextView>(R.id.creatorName)
        val passCode=dialog.findViewById<TextInputEditText>(R.id.passcodeInput)
        val startFrom=dialog.findViewById<TextView>(R.id.startTime)
        val validTill=dialog.findViewById<TextView>(R.id.validTill)
        val duraTion=dialog.findViewById<TextView>(R.id.duration)
        val VerifyButton=dialog.findViewById<Button>(R.id.closeButton)
        val Alert=dialog.findViewById<LinearLayout>(R.id.warningMessage)
        val warningText=dialog.findViewById<TextView>(R.id.warningText)
        roomName.text=RoomName
        roomCreatorName.text=RoomCreatorName
        startFrom.text=formatDate(StartFrom)
        validTill.text=formatDate(ValidTill)
        duraTion.text=Duration+" Minutes"
        val calendar=Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)+1
        val new_month=(month).toString().padStart(2,'0')
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val new_day=(day).toString().padStart(2,'0')
        val date=year.toString()+new_month.toString()+new_day.toString()
        Log.i("CurrentDate",date.toString())
        val QuizID_Ref=FireStore.getFireStore().collection("Quizset").document(QuizSetID).get().addOnSuccessListener {
            if (it.exists()){
                val QuestionCount=it.get("QuestionIds") as List<String>
                if(QuestionCount.isEmpty()){
                    VerifyButton.text="Invalid Room"
                    VerifyButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.neon_grey))
                    VerifyButton.setOnClickListener {
                        Toast.makeText(requireContext(),"Invalid Room",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    if(date.toInt()<StartFrom.toInt()) {
                        VerifyButton.text="PreRegister"
                        VerifyButton.setOnClickListener {
                            val Pass = passCode.text.toString()
                            if (Pass.isEmpty()) {
                               warningText.text="Enter a Valid Passcode"
                                Alert.visibility=View.VISIBLE
                            } else {
                                if (Pass != PassCode) {
                                   warningText.text="Invalid Passcode"
                                    Alert.visibility=View.VISIBLE
                                } else {
                                    val Attempted=false
                                    val newPreRegRoom=QuizSetModel(Duration,PassCode,RoomId,RoomName,StartFrom,RoomCreatorName,ValidTill,Attempted,SaveAllowed)
                                    val preRegRoom=newPreRegRoom.toMap()
                                    UID_Ref.update("PreRegisteredRooms", FieldValue.arrayUnion(preRegRoom))
                                        .addOnSuccessListener {
                                            dialog.dismiss()
                                            Toast.makeText(
                                                requireContext(),
                                                "Successfully Registered",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                requireContext(),
                                                "Something went wrong",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            }
                        }
                    }
                    else{
                        passCode.isClickable=false
                        warningText.text="The pre-registration period for this room has ended"
                        Alert.visibility=View.VISIBLE
                        VerifyButton.text="CLOSE"
                    }
                }
            }
        }
        dialog.show()
    }
    fun formatDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }

}