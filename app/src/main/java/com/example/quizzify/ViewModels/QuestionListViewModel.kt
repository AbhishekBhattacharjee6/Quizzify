package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.Repository.LiveQuizRepository
import kotlinx.coroutines.launch

class QuestionListViewModel(val repo:LiveQuizRepository):ViewModel(){
    val questionList:LiveData<List<String>>
        get() = repo._QuestionList

    fun getQuestionList(QuizListID:String){
        viewModelScope.launch {
            repo.getQuestionList(QuizListID)
        }
    }
}