package com.example.quizzify.Dialogs

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.Fragments.CreateRoom
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.utils.Constants
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class CreateRoomDialog: DialogFragment() {

    @Inject
    lateinit var  FireStoreInstance: FireStoreInstance

    lateinit var  startfrom:String
    lateinit var validtill:String
     var duration:String="30"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        (requireActivity().application as QuizApplication).applicationComponent.injectCreateRoomDialog(this)
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
        return inflater.inflate(R.layout.create_room_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val RoomName=view.findViewById<EditText>(R.id.etRoomName)
        val PassCode=view.findViewById<EditText>(R.id.etPasscode)
        val StartDate=view.findViewById<EditText>(R.id.etStartDate)
        val EndDate=view.findViewById<EditText>(R.id.etEndDate)
        val Duration=view.findViewById<TextView>(R.id.tvDurationValue)
        val SeekBar=view.findViewById<SeekBar>(R.id.seekBarDuration)
        val CreateBtn=view.findViewById<Button>(R.id.btnCreate)
        StartDate.setOnClickListener {
            DatePicker(StartDate,"StartFrom")
        }
        EndDate.setOnClickListener {
            DatePicker(EndDate,"EndDate")
        }
        duration="30"
        Duration.text=duration+" Minutes"
        SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                duration=(p1+30).toString()
                Log.d("Duration",duration)
                Duration.text=duration+" Minutes"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        CreateBtn.setOnClickListener {
            if(RoomName.text.toString().isNotEmpty() && PassCode.text.toString().isNotEmpty() && startfrom.isNotEmpty() && validtill.isNotEmpty()){
                CreateNewRoom(RoomName.text.toString(),PassCode.text.toString())
            }
            else{
                Toast.makeText(requireContext(),"Fill All The Fields",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun DatePicker(view: EditText,type:String){
        val calendar= Calendar.getInstance()
        val year=(calendar.get(Calendar.YEAR)).toString()
        val month=(calendar.get(Calendar.MONTH)).toString().padStart(2,'0')
        val day=(calendar.get(Calendar.DAY_OF_MONTH)).toString().padStart(2,'0')
        val datePickerDialog= DatePickerDialog(requireContext())
        datePickerDialog.setOnDateSetListener { _, newyear, newmonth, dayOfMonth ->
            val new_month=(newmonth+1).toString().padStart(2,'0')
            val new_day=(dayOfMonth).toString().padStart(2,'0')
            val SelectedDate="$newyear$new_month$new_day"
            if(type=="StartFrom"){
                startfrom=newyear.toString()+(new_month).toString()+new_day.toString()
            }
            else{
                validtill=newyear.toString()+(new_month).toString()+new_day.toString()
            }
            view.setText(formatDate(SelectedDate))
        }
        datePickerDialog.show()
    }
    fun formatDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val date = inputFormat.parse(input)
        return outputFormat.format(date!!)
    }
    private fun CreateNewRoom(RoomName:String,PassCode:String){
        val questionsetuid= Constants.UID+System.currentTimeMillis().toString()
        val QuizSet=HashMap<String,Any>()
        QuizSet["StartFrom"]=startfrom
        QuizSet["ValidTill"]=validtill
        QuizSet["Duration"]=duration
        QuizSet["UserUid"]= Constants.UID
        QuizSet["QuizSetUid"]=questionsetuid
        QuizSet["Passcode"]=PassCode
        QuizSet["RoomName"]=RoomName
        QuizSet["SaveAllowed"]=true
        QuizSet["QuestionIds"]=emptyList<String>()
        val FireStoreDB=FireStoreInstance.getFireStore()
        val UIDDB=FireStoreInstance.getFireStore()
        UIDDB.collection("UIDs").document(Constants.UID).get().addOnSuccessListener {
            if (it.exists()) {
                UIDDB.collection("UIDs").document(Constants.UID)
                    .update("QuizSetIDs", FieldValue.arrayUnion(QuizSet))
            } else {
                val Data = HashMap<String, Any>()
                Data.put("QuizSetIDs", emptyList<Map<String, Any>>())
                UIDDB.collection("UIDs").document(Constants.UID).set(Data).addOnSuccessListener {
                    UIDDB.collection("UIDs").document(Constants.UID)
                        .update("QuizSetIDs", FieldValue.arrayUnion(QuizSet))
                }

            }

            FireStoreDB.collection("Quizset").document(questionsetuid).set(QuizSet)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Room Created Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Room Creation Failed", Toast.LENGTH_SHORT)
                        .show()
                    dismiss()
                }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Room Creation Failed",Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult("dialogDismissed", Bundle())
    }
}