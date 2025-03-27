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
import com.example.quizzify.Fragments.SaveCollectionsFragment
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.utils.Constants
import com.google.firebase.firestore.FieldValue
import javax.inject.Inject

class NewSaveListDialog:DialogFragment() {

    @Inject
    lateinit var FireStore: FireStoreInstance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        (requireActivity().application as QuizApplication).applicationComponent.injectNewSaveListDialog(this)
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
        Createbtn.setOnClickListener {
            val ListName=ListTitle.text.toString()
            val PvCheck=isPrivate.isChecked
            if(ListName.isEmpty()){
                Toast.makeText(requireContext(),"Enter a Valid ListName",Toast.LENGTH_SHORT).show()
            }
            else{
                CreateList(ListName,PvCheck)

            }
        }
    }
    fun CreateList(TitleName:String,isPrivate:Boolean){
        val emptyArray= listOf<String>()

        val SavedCollectionModule=hashMapOf(
            "TitleName" to TitleName,
            "isPrivate" to isPrivate,
            "Author" to Constants.UID,
            "QuestionIDs" to emptyArray
        )
        val UID_Ref=FireStore.getFireStore().collection("UIDs").document(Constants.UID)
        UID_Ref.get().addOnSuccessListener {
            UID_Ref.update("SavedLists", FieldValue.arrayUnion(SavedCollectionModule)).addOnSuccessListener {
                context?.let {
                    Toast.makeText(it, "List created successfully", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }.addOnFailureListener {
                context?.let {
                    Toast.makeText(it, "List creation Failed", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }.addOnFailureListener {
            Log.d("SavedList","UID Fetch Failed")
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult("dialogDismissed", Bundle())
    }
}