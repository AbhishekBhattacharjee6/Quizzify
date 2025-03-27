package com.example.quizzify.Dialogs

import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.datamodels.QuizSetModel
import com.example.quizzify.utils.Constants
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class PreRegisterDialog:DialogFragment() {


    @Inject
    lateinit var FireStore: FireStoreInstance

    private var RoomName: String? = null
    private var RoomCreatorName: String? = null
    private var PassCode: String? = null
    private var StartFrom: String? = null
    private var ValidTill: String? = null
    private var RoomId: String? = null
    private var Duration: String? = null
    private var QuizSetID: String? = null
    private var Attempted: Boolean = false
    private var SaveAllowed: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        (requireActivity().application as QuizApplication).applicationComponent.injectPreRegisterDialog(this)
        arguments?.let {
            RoomName = it.getString(ARG_ROOM_NAME)
            RoomCreatorName = it.getString(ARG_ROOM_CREATOR_NAME)
            PassCode = it.getString(ARG_PASS_CODE)
            StartFrom = it.getString(ARG_START_FROM)
            ValidTill = it.getString(ARG_VALID_TILL)
            RoomId = it.getString(ARG_ROOM_ID)
            Duration = it.getString(ARG_DURATION)
            QuizSetID = it.getString(ARG_QUIZ_SET_ID)
            Attempted = it.getBoolean(ARG_ATTEMPTED)
            SaveAllowed = it.getBoolean(ARG_SAVE_ALLOWED)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)// Fullscreen background
        }
        dialog?.apply {
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.preregister_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val UID_Ref=FireStore.getFireStore().collection("UIDs").document(Constants.UID)
        val roomName=view.findViewById<TextView>(R.id.roomName)
        val roomCreatorName=view.findViewById<TextView>(R.id.creatorName)
        val passCode=view.findViewById<TextInputEditText>(R.id.passcodeInput)
        val startFrom=view.findViewById<TextView>(R.id.startTime)
        val validTill=view.findViewById<TextView>(R.id.validTill)
        val duraTion=view.findViewById<TextView>(R.id.duration)
        val VerifyButton=view.findViewById<Button>(R.id.closeButton)
        val Alert=view.findViewById<LinearLayout>(R.id.warningMessage)
        val warningText=view.findViewById<TextView>(R.id.warningText)
        roomName.text=RoomName
        roomCreatorName.text=RoomCreatorName
        startFrom.text=formatDate(StartFrom!!)
        validTill.text=formatDate(ValidTill!!)
        duraTion.text=Duration+" Minutes"
        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)+1
        val new_month=(month).toString().padStart(2,'0')
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val new_day=(day).toString().padStart(2,'0')
        val date=year.toString()+new_month.toString()+new_day.toString()
        Log.i("CurrentDate",date.toString())
        val QuizID_Ref=FireStore.getFireStore().collection("Quizset").document(QuizSetID!!).get().addOnSuccessListener {
            if (it.exists()){
                val QuestionCount=it.get("QuestionIds") as List<String>
                if(QuestionCount.isEmpty()){
                    VerifyButton.text="Invalid Room"
                    VerifyButton.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.neon_grey))
                    VerifyButton.setOnClickListener {
                        Toast.makeText(requireContext(),"Invalid Room", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    if(date.toInt()< StartFrom!!.toInt()) {
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
                                    val newPreRegRoom= QuizSetModel(Duration!!,PassCode!!,RoomId!!,RoomName!!,StartFrom!!,RoomCreatorName!!,ValidTill!!,Attempted,SaveAllowed)
                                    val preRegRoom=newPreRegRoom.toMap()
                                    UID_Ref.update("PreRegisteredRooms", FieldValue.arrayUnion(preRegRoom))
                                        .addOnSuccessListener {
                                            dismiss()
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
                        VerifyButton.setOnClickListener {
                            dismiss()
                        }
                    }
                }
            }
        }
    }
    companion object {
        private const val ARG_ROOM_NAME = "room_name"
        private const val ARG_ROOM_CREATOR_NAME = "room_creator_name"
        private const val ARG_PASS_CODE = "pass_code"
        private const val ARG_START_FROM = "start_from"
        private const val ARG_VALID_TILL = "valid_till"
        private const val ARG_ROOM_ID = "room_id"
        private const val ARG_DURATION = "duration"
        private const val ARG_QUIZ_SET_ID = "quiz_set_id"
        private const val ARG_ATTEMPTED = "attempted"
        private const val ARG_SAVE_ALLOWED = "save_allowed"

        fun newInstance(
            roomName: String,
            roomCreatorName: String,
            passCode: String,
            startFrom: String,
            validTill: String,
            roomId: String,
            duration: String,
            quizSetID: String,
            attempted: Boolean,
            saveAllowed: Boolean
        ): PreRegisterDialog {
            val fragment = PreRegisterDialog()
            val args = Bundle().apply {
                putString(ARG_ROOM_NAME, roomName)
                putString(ARG_ROOM_CREATOR_NAME, roomCreatorName)
                putString(ARG_PASS_CODE, passCode)
                putString(ARG_START_FROM, startFrom)
                putString(ARG_VALID_TILL, validTill)
                putString(ARG_ROOM_ID, roomId)
                putString(ARG_DURATION, duration)
                putString(ARG_QUIZ_SET_ID, quizSetID)
                putBoolean(ARG_ATTEMPTED, attempted)
                putBoolean(ARG_SAVE_ALLOWED, saveAllowed)
            }
            fragment.arguments = args
            return fragment
        }
    }
    fun formatDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }
}