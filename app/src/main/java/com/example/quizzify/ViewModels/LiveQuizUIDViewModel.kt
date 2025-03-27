package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.Repository.LiveQuizRepository
import com.example.quizzify.datamodels.RoomSetModel
import kotlinx.coroutines.launch

class LiveQuizUIDViewModel(val repo:LiveQuizRepository):ViewModel(){
    val QuizListIDs:LiveData<List<RoomSetModel>>
        get()=repo._QuizListID
    init {
        viewModelScope.launch{
            repo.getQuizListIDs()
        }
    }
    fun fetchQuizList() {
        viewModelScope.launch {
            repo.getQuizListIDs()
        }
    }
}