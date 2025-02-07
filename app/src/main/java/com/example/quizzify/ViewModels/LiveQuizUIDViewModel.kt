package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.Repository.LiveQuizRepository
import kotlinx.coroutines.launch

class LiveQuizUIDViewModel(val repo:LiveQuizRepository):ViewModel(){
    val QuizListIDs:LiveData<List<String>>
        get()=repo._QuizListID
    init {
        viewModelScope.launch{
            repo.getQuizListIDs()
        }
    }
}