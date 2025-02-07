package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.QuestionSetDataModels.Result
import com.example.quizzify.Repository.QuizRepository
import kotlinx.coroutines.launch

class QuizViewModel(private val repo:QuizRepository):ViewModel() {

    val questionset:LiveData<List<Result>>
        get() = repo.questionset
    fun getParams(QuestionNum:Int, CategoryNum:Int, Difficulty:String){
        viewModelScope.launch {
            repo.getQuestionsSet(QuestionNum,CategoryNum,Difficulty)
        }
    }
}