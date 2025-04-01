package com.example.quizzify.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.datamodels.BasicInfoDataModel
import com.example.quizzify.datamodels.RecentContestDataModel
import com.example.quizzify.utils.Constants
import javax.inject.Inject

class ProfileRepo @Inject constructor(private val FireStore:FireStoreInstance) {
    val UID_Ref=FireStore.getFireStore().collection("UIDInfo").document(Constants.UID)
    private val BasicInfo=MutableLiveData<List<BasicInfoDataModel>>(emptyList())
    val _BasicInfo:LiveData<List<BasicInfoDataModel>>
        get() = BasicInfo

    fun getBasicInfo(){
        UID_Ref.get().addOnSuccessListener {
            if(it.exists()){
                val Basic_Info=BasicInfoDataModel(
                    it.getString("Name")!!,
                    it.getString("UID")!!,
                    it.get("Questions Attempted").toString().toInt(),
                    it.get("Correct Answers").toString().toInt(),
                    it.get("Achievement").toString().toInt(),
                    it.get("Rank").toString().toInt(),
                    it.get("Contest Participated").toString().toInt(),
                    it.get("Level").toString().toInt(),
                    it.get("ImageURI").toString()
                )
                BasicInfo.postValue(listOf(Basic_Info))
            }
            }

    }

   private val RecentContests=MutableLiveData<List<RecentContestDataModel>>(emptyList())
    val _RecentContests:LiveData<List<RecentContestDataModel>>
        get() = RecentContests
    fun getRecentContests(){
      UID_Ref.get().addOnSuccessListener {
          if(it.exists()){
              val playersList = it.get("Recent Contests") as? MutableList<HashMap<String, Any>> ?: mutableListOf()
              val List=playersList.map {map->
                  RecentContestDataModel(
                      RoomID = map["roomID"].toString(),
                      RoomName = map["roomName"].toString(),
                      Total = map["totalQuestions"].toString().toInt(),
                      Correct = map["correctAnswers"].toString().toInt(),
                      Wrong = map["wrongAnswers"].toString().toInt(),
                      Validity = map["validTill"].toString()
                  )
              }
              RecentContests.postValue(List)
          }
      }
    }
}