package com.example.quizzify.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzify.Firestore.FireStoreInstance
import com.example.quizzify.datamodels.LeaderBoardModel
import javax.inject.Inject

class LeaderBoardRepository @Inject constructor(private val FireStore:FireStoreInstance) {
     private val QuizSetID_Ref=FireStore.getFireStore().collection("Quizset")
    private val LeaderBoard_List=MutableLiveData<List<LeaderBoardModel>>(emptyList())
    val _LeaderBoard_List:LiveData<List<LeaderBoardModel>>
        get()=LeaderBoard_List
   fun getLeaderBoard(QuizSetID:String){
        QuizSetID_Ref.document(QuizSetID).get().addOnSuccessListener {
            if(it.exists()){
                val leaderboardList = it.get("LeaderBoard") as? List<Map<String, Any>>
                val LeaderBoardList=mutableListOf<LeaderBoardModel>()
                if (!leaderboardList.isNullOrEmpty()) {
                    val convertedList = leaderboardList.map { map ->
                        LeaderBoardModel(
                            UID = map["UID"] as? String ?: "",
                            Name = map["Name"] as? String ?: "",
                            Score = (map["Score"] as? Long)?.toInt() ?: 0,
                            TotalScore=(map["TotalScore"] as? Long)?.toInt() ?: 0,
                            ImgUrl = (map["ImgUrl"] as? String).toString()
                        )
                    }
                    LeaderBoard_List.postValue(convertedList)
                    Log.d("Firestore", "Fetched Leaderboard: $convertedList")
                }
                else{
                    LeaderBoard_List.postValue(emptyList())
                }
            }
        }.addOnFailureListener {
            Log.d("Firestore", "Error fetching leaderboard: ${it.message}")
        }
    }

}