package com.example.quizzify.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.utils.Constants
import com.google.firebase.firestore.CollectionReference
import javax.inject.Inject

class SavedListRepo @Inject constructor(private val FireStore:FireStoreInstance) {
    var UID_ref:CollectionReference=FireStore.getFireStore().collection("UIDs")
    private val SavedListNames=MutableLiveData<List<String>>(emptyList())
    val _SavedListNames: LiveData<List<String>>
        get() = SavedListNames
     suspend  fun getSavedListNames(){
         UID_ref.document(Constants.UID).get().addOnSuccessListener {
             if(it.exists()){
                 val SavedList=it.get("SavedLists") as List<Map<String,Any>>
                 val listNames = SavedList.map { (it as Map<String, Any>)["ListName"] as String }
                 SavedListNames.postValue(listNames)
             }
         }
     }
}