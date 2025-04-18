package com.example.quizzify.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.datamodels.ARQuestionListModel
import com.example.quizzify.datamodels.RoomSetModel
import com.example.quizzify.utils.Constants
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class LiveQuizRepository @Inject constructor(private val FireStore:FireStoreInstance){
    //FOR RETRIEVING QUIZSET LIST
    var UID_Ref: CollectionReference = FireStore.getFireStore().collection("UIDs")
    private val QuizListID=MutableLiveData<List<RoomSetModel>>(emptyList())
    val _QuizListID:LiveData<List<RoomSetModel>>
        get()=QuizListID

    suspend fun getQuizListIDs(){
        UID_Ref.document(Constants.UID).get().addOnSuccessListener {
            if(it.exists()){
                val quizSetList = it.get("QuizSetIDs") as? List<Map<String, Any>> ?: emptyList()
                val quizList = quizSetList.map { map ->
                    RoomSetModel(
                        StartFrom = map["StartFrom"] as? String ?: "",
                        ValidTill = map["ValidTill"] as? String ?: "",
                        Duration = map["Duration"] as? String ?: "",
                        UserUid = map["UserUid"] as? String ?: "",
                        QuizSetUid = map["QuizSetUid"] as? String ?: "",
                        Passcode = map["Passcode"] as? String ?: "",
                        RoomName = map["RoomName"] as? String ?: "",
                        SaveAllowed = map["SaveAllowed"] as? Boolean ?: true
                    )
                }
                QuizListID.postValue(quizList)
            }
        }
    }

    //FOR RETRIEVING QUESTION ID LIST OF QUIZ SET
    var QuizList_Ref:CollectionReference=FireStore.getFireStore().collection("Quizset")
    private val Question_List=MutableLiveData<List<String>>(emptyList())
    val _QuestionList:LiveData<List<String>>
        get() = Question_List
    suspend fun getQuestionList(QuizSetID:String){
        QuizList_Ref.document(QuizSetID).get().addOnSuccessListener {
            if(it.exists()){
                val QuestionList=it.get("QuestionIds") as List<String>
               Question_List.postValue(QuestionList)
            }
        }
    }

    var IndividualQuestion_Ref:CollectionReference=FireStore.getFireStore().collection("LiveQuestions")
    private val IndividualQuestion=MutableLiveData<List<ARQuestionListModel>>(emptyList())
    val _IndividualQuestion:LiveData<List<ARQuestionListModel>>
        get()=IndividualQuestion
    suspend fun getQuestion(QuizIDs:List<String>){
        var temp_List= mutableListOf<ARQuestionListModel>()
        for(QuestionID in QuizIDs) {
            val it = IndividualQuestion_Ref.document(QuestionID).get().await()
                 if (it.exists()) {
                    val QuestionInfo = ARQuestionListModel(
                        it.getString("Question") ?: "",
                        it.getString("CorrectAnswer") ?: "Deleted",
                        it.getString("WrongAnswer1") ?: "Deleted",
                        it.getString("WrongAnswer2") ?: "Deleted",
                        it.getString("WrongAnswer3") ?: "Deleted",
                        it.getString("QuestionID") ?: ""
                    )
                    Log.d("QuestionInfo", QuestionInfo.toString())
                    temp_List.add(QuestionInfo)
                }

        }
        withContext(Dispatchers.Main) {
            if (temp_List.isNotEmpty()) {
                Log.d("FinalTempList", temp_List.toString())
                IndividualQuestion.value = temp_List // Use setValue on Main thread
                Log.d("SetorNot", IndividualQuestion.value.toString())
            }
        }
   }


    //FOR RETRIEVING PRE-REGISTERED ROOMS FROM UID
    private val PreRegRooms=MutableLiveData<List<String>>(emptyList())
    val _PreRegRooms:LiveData<List<String>>
        get()=PreRegRooms
    suspend fun getPreRegRooms(){
        UID_Ref.document(Constants.UID).get().addOnSuccessListener {
            if(it.exists()){
                val Pre_Reg_Rooms=it.get("PreRegisteredRooms") as List<String>
                PreRegRooms.postValue(Pre_Reg_Rooms)
            }
        }
    }
}