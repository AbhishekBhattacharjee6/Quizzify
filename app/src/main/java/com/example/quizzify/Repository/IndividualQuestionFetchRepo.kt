package com.example.quizzify.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.datamodels.QuestionModel
import javax.inject.Inject

class IndividualQuestionFetchRepo @Inject constructor(private val FireStore:FireStoreInstance) {
    private val QuestionCollection_Ref = FireStore.getFireStore().collection("LiveQuestions")

    fun getQuestion(QuestionID: String, callback: (QuestionModel?) -> Unit) {
        QuestionCollection_Ref.document(QuestionID).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document != null) {
                    val question = document.getString("Question") ?: ""
                    val correctAnswer = document.getString("CorrectAnswer") ?: ""
                    val wrongAnswer1 = document.getString("WrongAnswer1") ?: ""
                    val wrongAnswer2 = document.getString("WrongAnswer2") ?: ""
                    val wrongAnswer3 = document.getString("WrongAnswer3") ?: ""
                    val questionId=document.getString("QuestionID")?:""
                    val questionModel = QuestionModel(question, correctAnswer, wrongAnswer1, wrongAnswer2, wrongAnswer3,questionId)
                    callback(questionModel) // Return the fetched question
                } else {
                    callback(null) // If question does not exist
                }
            }
            .addOnFailureListener {
                callback(null) // Handle errors
            }
    }
}