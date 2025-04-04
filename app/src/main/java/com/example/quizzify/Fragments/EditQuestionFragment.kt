package com.example.quizzify.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.quizzify.BottomNavBar.CustomBottomNavigation
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.QuizApplication
import com.example.quizzify.R
import com.google.android.material.textfield.TextInputEditText
import javax.inject.Inject


class EditQuestionFragment : Fragment() {

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
        return inflater.inflate(R.layout.edit_add_questions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().findViewById<CustomBottomNavigation>(R.id.bottomNavigation).visibility = View.GONE
        (requireActivity().application as QuizApplication).applicationComponent.injectEditQuestions(this)
        val QuestionId_Ref=FireStore.getFireStore().collection("LiveQuestions")
        super.onViewCreated(view, savedInstanceState)
        val question= arguments?.getString("Question")
        val ca= arguments?.getString("CA")
        val wa1= arguments?.getString("WA1")
        val wa2= arguments?.getString("WA2")
        val wa3= arguments?.getString("WA3")
        val QuestionId= arguments?.getString("QuizId")
        Log.d("QuestionId",QuestionId.toString())
        val QuestionBody=view.findViewById<TextInputEditText>(R.id.questionInput)
        val CorrectAnswer=view.findViewById<TextInputEditText>(R.id.correctOptionInput)
        val WrongAnswer1=view.findViewById<TextInputEditText>(R.id.wrongOption1Input)
        val WrongAnswer2=view.findViewById<TextInputEditText>(R.id.wrongOption2Input)
        val WrongAnswer3=view.findViewById<TextInputEditText>(R.id.wrongOption3Input)
        val Editbtn=view.findViewById<Button>(R.id.saveButton)
        QuestionBody.setText(question)
        CorrectAnswer.setText(ca)
        WrongAnswer1.setText(wa1)
        WrongAnswer2.setText(wa2)
        WrongAnswer3.setText(wa3)
        Editbtn.setOnClickListener {
            val FetchedQuestion=QuestionBody.text.toString()
            val FetchedCA=CorrectAnswer.text.toString()
            val FetchedWA1=WrongAnswer1.text.toString()
            val FetchedWA2=WrongAnswer2.text.toString()
            val FetchedWA3=WrongAnswer3.text.toString()
            if(FetchedQuestion.isNotEmpty() && FetchedCA.isNotEmpty() && FetchedWA1.isNotEmpty() && FetchedWA2.isNotEmpty() && FetchedWA3.isNotEmpty()){
                val Id_Ref=QuestionId_Ref.document(QuestionId.toString())
                Id_Ref.update(
                    "Question",FetchedQuestion,
                    "CorrectAnswer",FetchedCA,
                    "WrongAnswer1",FetchedWA1,
                    "WrongAnswer2",FetchedWA2,
                    "WrongAnswer3",FetchedWA3
                ).addOnSuccessListener {
                    Toast.makeText(requireContext(),"Question Updated Successfully",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(requireContext(),"Fill all the fields properly",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().findViewById<CustomBottomNavigation>(R.id.bottomNavigation).visibility = View.VISIBLE
    }
}