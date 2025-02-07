package com.example.quizzify.Repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.datamodels.QuizSetModel
import com.google.firebase.firestore.CollectionReference
import javax.inject.Inject

class SearchRepo @Inject constructor(private val FireStore:FireStoreInstance) {
    var QuizSet_Ref:CollectionReference=FireStore.getFireStore().collection("Quizset")
    private val QuizSetList= MutableLiveData<List<QuizSetModel>>(emptyList())
    val _QuizSetList:LiveData<List<QuizSetModel>>
        get()=QuizSetList
    suspend fun getQuizSetList(Room:String){
        QuizSet_Ref.whereEqualTo("RoomName",Room).get().addOnSuccessListener {
            Log.d("RoomFound","RoomFound"+Room)
            var QuizList= mutableListOf<QuizSetModel>()
            for(document in it){
                Log.d("InsertingRoom","InsertingRoom")
                var User=QuizSetModel(
                    Duration = document.getString("Duration")!!,
                    PassCode = document.getString("Passcode")!!,
                    QuizSetId = document.getString("QuizSetUid")!!,
                    RoomName = document.getString("RoomName")!!,
                    StartFrom = document.getString("StartFrom")!!,
                    CreatorUID = document.getString("UserUid")!!,
                    ValidTill = document.getString("ValidTill")!!
                )
                QuizList.add(User)
            }
            QuizSetList.postValue(QuizList)
        }.addOnFailureListener {
            Log.d("SearchFailed","No Room found")
        }
    }
}