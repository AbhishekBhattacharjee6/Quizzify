package com.example.quizzify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizzify.Dialogs.ProfileSetupDialog
import com.example.quizzify.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class SignUp:AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)
        supportActionBar?.hide()
        val Email=findViewById<TextInputEditText>(R.id.emailInput)
        val Password=findViewById<TextInputEditText>(R.id.passwordInput)
        val ConfirmPassword=findViewById<TextInputEditText>(R.id.confirmPasswordInput)
        val CreateAccountBtn=findViewById<MaterialButton>(R.id.signUpButton)
        val GoogleSignUpBtn=findViewById<MaterialButton>(R.id.googleSignUpButton)

        auth=FirebaseAuth.getInstance()
       CreateAccountBtn.setOnClickListener {
           val emailText=Email.text.toString()
           val passwordText=Password.text.toString()
           val confirmPasswordText=ConfirmPassword.text.toString()
           if(emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()){
               Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
           }
           else{
               if(passwordText==confirmPasswordText){
                   auth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener {task->
                       if(task.isSuccessful){
                           Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_SHORT).show()
                           Constants.UID=auth.currentUser!!.uid
                           UIDFilesExist(Constants.UID)
                           val resultIntent = Intent()
                           setResult(Activity.RESULT_OK,resultIntent)
                           finish()
                       }
                       else{
                           Toast.makeText(this,"Account Creation Failed",Toast.LENGTH_SHORT).show()
                       }
                   }
                   }
           }
       }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("969495768199-gfuingqth9r36ob0la9cm0aaugpciao6.apps.googleusercontent.com")  // Use your web client ID from google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        GoogleSignUpBtn.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign-In Successful!", Toast.LENGTH_SHORT).show()
                    Constants.UID=auth.currentUser!!.uid
                    UIDFilesExist(Constants.UID)
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK,resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun UIDFilesExist(UID:String){
        val UID_Ref= FirebaseFirestore.getInstance().collection("UIDs").document(UID)
        UID_Ref.get().addOnSuccessListener{
            if(!it.exists()){
                val data= hashMapOf(
                    "PreRegisteredRooms" to emptyList<Map<String,Any>>(),
                    "QuizSetIDs" to emptyList<Map<String,Any>>(),
                    "SavedLists" to emptyList<Map<String,Any>>()
                )
                UID_Ref.set(data)
            }
        }
        val UIDInfo_Ref= FirebaseFirestore.getInstance().collection("UIDInfo").document(UID)
        UIDInfo_Ref.get().addOnSuccessListener {
            if (!it.exists()) {
                val data = hashMapOf(
                    "Name" to "",
                    "Questions Attempted" to 0,
                    "Rank" to 0,
                    "Level" to 1,
                    "Correct Answers" to 0,
                    "Contest Participated" to 0,
                    "Achievement" to 0,
                    "Days Active" to emptyList<Map<String,Any>>(),
                    "Recent Contests" to emptyList<Map<String,Any>>(),
                    "UID" to UID,
                    "ImageURI" to ""
                )
                UIDInfo_Ref.set(data)
                val dialog= ProfileSetupDialog.newInstance(UID)
                dialog.show(supportFragmentManager,"ProfileSetupDialog")
            }
        }
    }
}