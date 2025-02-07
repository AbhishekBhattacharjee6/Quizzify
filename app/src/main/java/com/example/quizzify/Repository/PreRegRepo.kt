package com.example.quizzify.Repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.datamodels.QuizSetModel
import com.example.quizzify.utils.Constants
import javax.inject.Inject

class PreRegRepo @Inject constructor(private val FireStore:FireStoreInstance) {

    var UID_ref= FireStore.getFireStore().collection("UIDs").document(Constants.UID)
    private val PreRegRoomList= MutableLiveData<List<QuizSetModel>>(emptyList())
    val _PreRegRoomList:LiveData<List<QuizSetModel>>
        get() = PreRegRoomList

    suspend fun getPreRegRooms(){
        UID_ref.get().addOnSuccessListener {document->
            val preRegRooms=document.get("PreRegisteredRooms") as List<Map<String,Any>>
            val newRooms=preRegRooms.map{QuizSetModel.fromMap(it)}
            PreRegRoomList.postValue(newRooms)
        }
    }
}