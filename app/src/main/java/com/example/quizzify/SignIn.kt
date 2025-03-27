package com.example.quizzify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.quizzify.Dialogs.ProfileSetupDialog
import com.example.quizzify.Firestore.FireStoreInstance
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

class SignIn:AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    private val signUpLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Sign-up was successful, return to MainActivity
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        val Email=findViewById<TextInputEditText>(R.id.emailInput)
        val PassWord=findViewById<TextInputEditText>(R.id.passwordInput)
        val SignInBtn=findViewById<MaterialButton>(R.id.signInButton)
        val GoogleSignInBtn=findViewById<MaterialButton>(R.id.googleSignInButton)
        val ForgotPassword=findViewById<TextView>(R.id.forgotPasswordText)
        val SignUp=findViewById<TextView>(R.id.signUpText)
        SignInBtn.setOnClickListener {
            val EmailText=Email.text!!
            val PasswordText=PassWord.text!!
            if(EmailText.isNotEmpty() && PasswordText.isNotEmpty()){
                auth.signInWithEmailAndPassword(EmailText.toString(), PasswordText.toString()).addOnCompleteListener{task->
                   if(task.isSuccessful){
                       Toast.makeText(this, "Sign-In Successful!", Toast.LENGTH_SHORT).show()
                       Constants.UID=auth.currentUser!!.uid
                       SaveName(Constants.UID)
                       setResult(Activity.RESULT_OK)
                       finish()
                   }
                   else{
                        Toast.makeText(this,"Sign In Failed!!",Toast.LENGTH_SHORT).show()
                   }
                }
            }
            else{
                Toast.makeText(this,"Fill all the Fields Correctly",Toast.LENGTH_SHORT).show()
            }
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("969495768199-gfuingqth9r36ob0la9cm0aaugpciao6.apps.googleusercontent.com")  // Use your web client ID from google-services.json
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        SignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            signUpLauncher.launch(intent)
        }
        GoogleSignInBtn.setOnClickListener {
            signIn()
        }
        ForgotPassword.setOnClickListener {
            val emailText=Email.text!!.trim().toString()
            if(emailText.isNotEmpty()){
                ForgotPassword(emailText)
            }
            else {
                Toast.makeText(this, "Enter Your Email", Toast.LENGTH_SHORT).show()
            }
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do nothing, blocking the back press
                Toast.makeText(this@SignIn,"Sign Up First",Toast.LENGTH_SHORT).show()
            }
        })
    }
    fun SaveName(UID:String){
        val UID_Ref=FirebaseFirestore.getInstance().collection("UIDInfo").document(UID).get().addOnCompleteListener {
            Constants.Name=it.result.get("Name").toString()
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
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign-In Successful!", Toast.LENGTH_SHORT).show()
                    Constants.UID=auth.currentUser!!.uid
                   UIDFilesExist(Constants.UID)
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun ForgotPassword(Email:String){
        auth.sendPasswordResetEmail(Email).addOnCompleteListener{task->
            if(task.isSuccessful){
                Toast.makeText(this,"Password Reset Email Sent!",Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Password Reset Failed!!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun UIDFilesExist(UID:String){
        val UID_Ref=FirebaseFirestore.getInstance().collection("UIDs").document(UID)
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
        val UIDInfo_Ref=FirebaseFirestore.getInstance().collection("UIDInfo").document(UID)
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
                val dialog=ProfileSetupDialog.newInstance(UID)
                dialog.show(supportFragmentManager,"ProfileSetupDialog")
            }
        }
    }
}