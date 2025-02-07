package com.example.quizzify.Fragments

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quizzify.Adapters.LiveQuizUIDAdapter
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.ViewModelFactories.LiveQuizUIDVIewModelFactory
import com.example.quizzify.ViewModels.LiveQuizUIDViewModel
import com.example.quizzify.ViewModels.QuizViewModel
import com.example.quizzify.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import it.sephiroth.android.library.numberpicker.NumberPicker
import it.sephiroth.android.library.numberpicker.doOnProgressChanged
import okhttp3.internal.Util
import javax.inject.Inject
import kotlin.time.Duration


class ActiveRooms : Fragment() {

    @Inject
    lateinit var  FireStoreInstance: FireStoreInstance

    @Inject
    lateinit var LiveQuizUIDVMFactory:LiveQuizUIDVIewModelFactory

    lateinit var  startfrom:String
    lateinit var validtill:String
    lateinit var duration:String
    lateinit var questionsetuid:String
    lateinit var LiveUIDAdapter:LiveQuizUIDAdapter

   lateinit var dialog:BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_rooms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity().application as QuizApplication).applicationComponent.injectActiveRoom(this)
        val ActiveRoomRV=view.findViewById<RecyclerView>(R.id.ActiveRooms)
        ActiveRoomRV.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        LiveUIDAdapter=LiveQuizUIDAdapter(this, emptyList())
        ActiveRoomRV.adapter=LiveUIDAdapter
        val NewRoom=view.findViewById<FloatingActionButton>(R.id.NewRoom)
        val LiveQuizUIDViewModel:LiveQuizUIDViewModel by viewModels{
            LiveQuizUIDVMFactory
        }
        NewRoom.setOnClickListener{
            RoomDialog()
        }
        LiveQuizUIDViewModel.QuizListIDs.observe(viewLifecycleOwner){
            if(it!=null){
                LiveUIDAdapter.updateList(it)
            }
            else{
                Toast.makeText(requireContext(),"No Active Rooms",Toast.LENGTH_SHORT).show()
            }
        }
        LiveUIDAdapter.onItemClickListener(object :LiveQuizUIDAdapter.QuizSetListener{
            override fun onItemClick(position: Int, QuizSetId: String) {
                val Data=Bundle()
                Data.putString("QuizSetId",QuizSetId)
                ReplaceFrag(ActiveRoomQuestionList(),Data)
            }

        })

    }
    private fun RoomDialog(){
        val DialogView=layoutInflater.inflate(R.layout.new_room_dialog,null)
        dialog= BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
        dialog.setContentView(DialogView)
        val calendar=Calendar.getInstance()
        val year=(calendar.get(Calendar.YEAR)).toString()
        val month=(calendar.get(Calendar.MONTH)+1).toString().padStart(2,'0')
        val day=(calendar.get(Calendar.DAY_OF_MONTH)).toString().padStart(2,'0')
        val SelectedDate="$day-${month}-$year"
        val StartFrom=DialogView.findViewById<EditText>(R.id.editTextDate)
        val RoomName=DialogView.findViewById<TextInputEditText>(R.id.RoomName)
        val ValidTill=DialogView.findViewById<EditText>(R.id.editTextDate2)
        val Duration=DialogView.findViewById<NumberPicker>(R.id.Durations)
        val button=DialogView.findViewById<Button>(R.id.CreateRoom)
        startfrom=year.toString()+month.toString()+day.toString()
        validtill=year.toString()+month.toString()+day.toString()
        duration="30"
        StartFrom.setText(SelectedDate)
        ValidTill.setText(SelectedDate)
        StartFrom.setOnClickListener {
            DatePicker(StartFrom,"StartFrom")
        }
        ValidTill.setOnClickListener{
            DatePicker(ValidTill,"ValidTill")
        }
        Duration.doOnProgressChanged { numberPicker, progress, formUser ->
            duration=progress.toString()
        }
        button.setOnClickListener{
            val Room_Name=RoomName.text.toString()
            if(startfrom.isEmpty() || validtill.isEmpty() || duration.isEmpty() || Room_Name.isEmpty()){
                Toast.makeText(requireContext(),"Please Fill All The Fields",Toast.LENGTH_SHORT).show()
            }
            else if(startfrom.toInt()>validtill.toInt()){
                Toast.makeText(requireContext(),"Start Date Cannot Be Greater Than End Date",Toast.LENGTH_SHORT).show()
            }
            else{
                CreateNewRoom(Room_Name)
                dialog.dismiss()
                Log.d("NewRoom", "Room Created Seccusefully-questionsetuid: $questionsetuid")
                //Toast.makeText(requireContext(),"Room Created Successfully",Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
    private fun DatePicker(view: EditText,type:String){
        val calendar=Calendar.getInstance()
        val year=(calendar.get(Calendar.YEAR)).toString()
        val month=(calendar.get(Calendar.MONTH)).toString().padStart(2,'0')
        val day=(calendar.get(Calendar.DAY_OF_MONTH)).toString().padStart(2,'0')
        val datePickerDialog=DatePickerDialog(requireContext())
        datePickerDialog.setOnDateSetListener { _, newyear, newmonth, dayOfMonth ->
            val new_month=(newmonth+1).toString().padStart(2,'0')
            val new_day=(dayOfMonth).toString().padStart(2,'0')
            val SelectedDate="$new_day-${new_month}-$newyear"
            if(type=="StartFrom"){
                startfrom=newyear.toString()+(new_month).toString()+new_day.toString()
            }
            else{
                validtill=newyear.toString()+(new_month).toString()+new_day.toString()
            }
            view.setText(SelectedDate)
        }
        datePickerDialog.show()
    }
    private fun CreateNewRoom(RoomName:String){
        questionsetuid=Constants.UID+System.currentTimeMillis().toString()
        val FireStoreDB=FireStoreInstance.getFireStore()
        val UIDDB=FireStoreInstance.getFireStore()
        UIDDB.collection("UIDs").document(Constants.UID).get().addOnSuccessListener {
            if(it.exists()){
                UIDDB.collection("UIDs").document(Constants.UID).update("QuizSetIDs", FieldValue.arrayUnion(questionsetuid))
            }
            else{
                val Data=HashMap<String,Any>()
                Data.put("QuizSetIDs", emptyList<String>())
                UIDDB.collection("UIDs").document(Constants.UID).set(Data)
                UIDDB.collection("UIDs").document(Constants.UID).update("QuizSetIDs", FieldValue.arrayUnion(questionsetuid))
            }
        }
        val Data=Bundle()
        val QuizSet=HashMap<String,Any>()
        QuizSet["StartFrom"]=startfrom
        QuizSet["ValidTill"]=validtill
        QuizSet["Duration"]=duration
        QuizSet["UserUid"]=Constants.UID
        QuizSet["QuizSetUid"]=questionsetuid
        QuizSet["Passcode"]="Abhishek"
        QuizSet["RoomName"]=RoomName
        QuizSet["QuestionIds"]=emptyList<String>()
        FireStoreDB.collection("Quizset").document(questionsetuid).set(QuizSet)
            .addOnSuccessListener {
                Toast.makeText(requireContext(),"Room Created Successfully",Toast.LENGTH_SHORT).show()
                Data.putString("QuizSetID",questionsetuid)
                ReplaceFrag(CreateRoom(),Data)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"Room Creation Failed",Toast.LENGTH_SHORT).show()
            }
    }
    private fun ReplaceFrag(Frag: Fragment,Data:Bundle){
        Frag.arguments=Data
        val fragManager=parentFragmentManager
        val fragTransaction=fragManager.beginTransaction()
        fragTransaction.replace(R.id.FrameLayout,Frag)
        fragTransaction.commit()
    }
}