package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quizzify.Repository.LeaderBoardRepository
import com.example.quizzify.datamodels.LeaderBoardModel

class LeaderBoardViewModel(private val LeaderBoardRepo:LeaderBoardRepository):ViewModel() {
    private val LeaderBoard_List= MutableLiveData<List<LeaderBoardModel>>(emptyList())
    val _LeaderBoard_List: LiveData<List<LeaderBoardModel>>
        get()=LeaderBoard_List
    fun getQuizId(quizId:String){
        LeaderBoardRepo.getLeaderBoard(quizId)
    }
}