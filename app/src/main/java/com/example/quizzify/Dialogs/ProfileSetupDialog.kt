package com.example.quizzify.Dialogs

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.example.quizzify.SharedPreference.ImagePreference
import com.example.quizzify.SharedPreference.NamePreference
import com.example.quizzify.utils.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class ProfileSetupDialog:DialogFragment() {

    @Inject
    lateinit var  FireStoreInstance: FireStoreInstance

    private var URI:Uri?=null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
           // uploadImageToFirebase(it)
            URI=it
        }
    }

    private var UID:String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
        (requireActivity().application as QuizApplication).applicationComponent.injectProfileSetupDialog(this)
        UID=arguments?.getString(ARG_UID)
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
        return inflater.inflate(R.layout.dialog_profile_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val Name=view.findViewById<TextInputEditText>(R.id.nameInput)
        val User_ID=view.findViewById<TextInputEditText>(R.id.usernameInput)
        User_ID.isClickable=false
        User_ID.text= Editable.Factory.getInstance().newEditable(UID?:"")
        val Img_View=view.findViewById<ImageView>(R.id.profileImageView)
        val SaveButton=view.findViewById<MaterialButton>(R.id.saveButton)
        val SkipButton=view.findViewById<MaterialButton>(R.id.skipButton)
        SkipButton.setOnClickListener {
            Toast.makeText(requireContext(),"Not Skippable", Toast.LENGTH_SHORT).show()
        }
        Img_View.setOnClickListener{
            openGallery()
        }
        SaveButton.setOnClickListener {
            val NameText=Name.text.toString()
            if(URI!=null && NameText.isNotEmpty()){
                uploadImageToFirebase(URI!!)
                saveName(NameText)
            }
        }
    }
    companion object{
        private const val ARG_UID= "uid"
        fun newInstance(uid:String):ProfileSetupDialog{
            val frag= ProfileSetupDialog()
            val args=Bundle().apply {
                putString(ARG_UID, uid)
            }
            frag.arguments=args
            return frag
        }
    }
    fun openGallery() {
        pickImageLauncher.launch("image/*")
    }
    fun uploadImageToFirebase(imageUri: Uri){
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${System.currentTimeMillis()}.jpg")

        storageRef.putFile(imageUri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                saveUriToFireStore(uri.toString())
            }
        }.addOnFailureListener {

        }
    }
    fun saveUriToFireStore(uri:String){
        FireStoreInstance.getFireStore().collection("UIDInfo").document(UID!!).update("ImageURI",uri).addOnSuccessListener {
            val SharedPref= ImagePreference(requireContext())
            SharedPref.saveUserImgURI(uri)
            Constants.ImageURI=uri
            Toast.makeText(requireContext(),"Saved", Toast.LENGTH_SHORT).show()
        }
    }
    fun saveName(Name:String){
        val SharedPref= NamePreference(requireContext())

        FireStoreInstance.getFireStore().collection("UIDInfo").document(UID!!).update("Name",Name).addOnSuccessListener {
            SharedPref.saveUserName(Name)
            Constants.Name=Name
            Log.d("ProfileSetupDialog", "Name and UID updated successfully")
            dismiss()
        }
    }
}