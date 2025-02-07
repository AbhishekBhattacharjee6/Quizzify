package com.example.quizzify.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class CreateRoom : Fragment() {

    @Inject
    lateinit var FireStore:FireStoreInstance

    lateinit var CurrentQuestionNumber:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity().application as QuizApplication).applicationComponent.injectQuestion(this)
        val RoomCode= arguments?.getString("QuizSetID")
        val Question=view.findViewById<TextInputEditText>(R.id.Questions)
        //Correct Answer
        val Option1=view.findViewById<TextInputEditText>(R.id.Option1)
        //Wrong Answers
        val Option2=view.findViewById<TextInputEditText>(R.id.Option2)
        val Option3=view.findViewById<TextInputEditText>(R.id.Option3)
        val Option4=view.findViewById<TextInputEditText>(R.id.Option4)
        val addBtn=view.findViewById<Button>(R.id.button5)

        addBtn.setOnClickListener {
            val QuestionBody=Question.text.toString()
            val CorrectAnswer=Option1.text.toString()
            val WrongAnswer1=Option2.text.toString()
            val WrongAnswer2=Option3.text.toString()
            val WrongAnswer3=Option4.text.toString()
            if(QuestionBody.isEmpty() || CorrectAnswer.isEmpty() || WrongAnswer1.isEmpty() || WrongAnswer2.isEmpty() || WrongAnswer3.isEmpty()){
                Toast.makeText(requireContext(),"Please fill all the fields",Toast.LENGTH_SHORT).show()
                Log.d("Values","QuestionBody:$QuestionBody,CorrectAnswer:$CorrectAnswer,WrongAnswer1:$WrongAnswer1,WrongAnswer2:$WrongAnswer2,WrongAnswer3:$WrongAnswer3")
            }
            else{
                CreateQuestion(RoomCode.toString(),QuestionBody,CorrectAnswer,WrongAnswer1,WrongAnswer2,WrongAnswer3)
                Log.d("Created","Question Created Successfully")
            }
        }
    }
    fun CreateQuestion(QuizSetID:String,QuestionBody:String,CorrectAnswer:String,WrongAnswer1:String,WrongAnswer2:String,WrongAnswer3:String) {
        val QuizSetDB = FireStore.getFireStore()
        val QuestionsDB = FireStore.getFireStore()
        QuizSetDB.collection("Quizset").document(QuizSetID).get().addOnSuccessListener {
            if(it.exists()){
                val QuestionIDList=it.get("QuestionIds") as ArrayList<*>
            }
            else{
                Toast.makeText(requireContext(),"QuizSet doesn't exist",Toast.LENGTH_SHORT).show()
            }
        }
        val QuestionID=QuizSetID+System.currentTimeMillis().toString()
        val Question=HashMap<String,Any>()
        Question.put("QuestionID",QuestionID)
        Question.put("Question",QuestionBody)
        Question.put("CorrectAnswer",CorrectAnswer)
        Question.put("WrongAnswer1",WrongAnswer1)
        Question.put("WrongAnswer2",WrongAnswer2)
        Question.put("WrongAnswer3",WrongAnswer3)
        QuestionsDB.collection("LiveQuestions").document(QuestionID).set(Question).addOnSuccessListener {
            Toast.makeText(requireContext(),"Question Added Succesfully",Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"Something Went Wrong",Toast.LENGTH_SHORT).show()
            }
        QuizSetDB.collection("Quizset").document(QuizSetID).update("QuestionIds",FieldValue.arrayUnion(QuestionID)).addOnSuccessListener {
            Log.d("Success","Question Added Successfully")
        }.addOnFailureListener {
            Log.d("Failure","Question Not Added")
        }
    }

}