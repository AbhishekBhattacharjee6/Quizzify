package com.example.quizzify.Dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.datamodels.SavedCollectionModel
import com.example.quizzify.utils.Constants
import javax.inject.Inject

class SaveListEditDialog: DialogFragment() {

    private var TitleName: String? = null
    private var isChecked:Boolean=false
    private var quizList:List<SavedCollectionModel> = emptyList()
    private var position:Int=0

    @Inject
    lateinit var FireStore: FireStoreInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        (requireActivity().application as QuizApplication).applicationComponent.injectSaveListEditDialog(this)
        arguments.let {
            TitleName = it?.getString(ARG_TITLE)
            isChecked=it?.getBoolean(ARG_CHECKED)!!
            quizList = it.getParcelableArrayList(ARG_QUIZ)?: emptyList()
            position=it.getInt(Pos)
        }
        Log.d("QuizList",quizList.toString())
        Log.d("Position",position.toString())
        Log.d("TitleName",TitleName.toString())
        Log.d("isChecked",isChecked.toString())
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
        return inflater.inflate(R.layout.new_save_list_create_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ListTitle=view.findViewById<EditText>(R.id.etListName)
        val isPrivate=view.findViewById<CheckBox>(R.id.cbPrivate)
        val Closebtn=view.findViewById<ImageView>(R.id.btnClose)
        val Createbtn=view.findViewById<Button>(R.id.btnCreate)
        val Cancelbtn=view.findViewById<Button>(R.id.btnCancel)
        Closebtn.setOnClickListener {
            dismiss()
        }
        Cancelbtn.setOnClickListener {
            dismiss()
        }
        Createbtn.text="SAVE"
        ListTitle.setText(TitleName)
        isPrivate.isChecked=isChecked
        Createbtn.setOnClickListener {
            val Title=ListTitle.text.toString()
            if(Title.isNotEmpty()){
                EditList(quizList,position,Title,isPrivate.isChecked,quizList[position].QuestionIDs)
            }
            else{
                Toast.makeText(requireContext(),"Please Enter a Title", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object{
        private const val ARG_TITLE="title"
        private const val ARG_CHECKED="checked"
        private const val ARG_QUIZ = "arg_quiz"
        private const val Pos="position"
        fun newInstance(
            title:String,
            checked:Boolean,
            quizList:List<SavedCollectionModel>,
            position:Int
        ):SaveListEditDialog{
            val fragment=SaveListEditDialog()
            val args=Bundle().apply {
                putString(ARG_TITLE,title)
                putBoolean(ARG_CHECKED,checked)
                putParcelableArrayList(ARG_QUIZ, ArrayList(quizList))
                putInt(Pos,position)
            }
            fragment.arguments = args
            return fragment
        }
    }
    fun EditList(QuizList:List<SavedCollectionModel>, Index:Int, TitleName:String, isPrivate:Boolean, QuestionList:List<String>){
        val newModule= SavedCollectionModel(TitleName,isPrivate, Constants.UID,QuestionList)
        var NewList=QuizList.toMutableList()
        NewList[Index]=newModule
        val UID_Ref=FireStore.getFireStore().collection("UIDs").document(Constants.UID)
        val updatedSavedList = NewList.map { it.toMap() }
        UID_Ref.update("SavedLists", updatedSavedList)
            .addOnSuccessListener {
                Log.d("Firestore", "savedlist field updated successfully")
                Toast.makeText(requireContext(),"List Edited Succesfully", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error updating savedlist field", e)
                dismiss()
            }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult("dialogDismissed2", Bundle())
    }
}