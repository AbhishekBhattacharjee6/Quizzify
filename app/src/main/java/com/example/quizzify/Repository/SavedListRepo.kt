package com.example.quizzify.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.datamodels.SavedCollectionModel
import com.example.quizzify.utils.Constants
import com.google.firebase.firestore.CollectionReference
import javax.inject.Inject

class SavedListRepo @Inject constructor(private val FireStore:FireStoreInstance) {
    var UID_ref:CollectionReference=FireStore.getFireStore().collection("UIDs")
    private val SavedListNames=MutableLiveData<List<SavedCollectionModel>>(emptyList())
    val _SavedListNames: LiveData<List<SavedCollectionModel>>
        get() = SavedListNames
    suspend fun getSavedListNames() {
        UID_ref.document(Constants.UID).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Get the "SavedLists" field as an object
                    val savedListRaw = document.get("SavedLists")

                    // Debug: Print raw data
                    Log.d("SavedListRepo", "Raw SavedLists: $savedListRaw")

                    // Try casting to a list
                    val savedList = savedListRaw as? List<Map<String, Any>> ?: emptyList()

                    // Debug: Check size of extracted list
                    Log.d("SavedListRepo", "Extracted list size: ${savedList.size}")

                    // Convert data to model list
                    val convertedList = savedList.map { map ->
                        SavedCollectionModel(
                            TitleName = map["TitleName"] as? String ?: "",
                            isPrivate = map["isPrivate"] as? Boolean ?: false,
                            Author = map["Author"] as? String ?: "",
                            QuestionIDs = map["QuestionIDs"] as? List<String> ?: emptyList()
                        )
                    }

                    // Debug: Print converted data
                    Log.d("SavedListRepo", "Converted List: $convertedList")

                    // Post to LiveData
                    SavedListNames.postValue(convertedList)
                }
            }
            .addOnFailureListener {
                Log.e("SavedListRepo", "Error fetching data: ${it.message}")
            }
    }

}