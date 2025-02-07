package com.example.quizzify.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.Repository.LiveQuizRepository
import com.example.quizzify.datamodels.ARQuestionListModel
import kotlinx.coroutines.launch

class ARIndividualQuestionViewModel(val repo:LiveQuizRepository):ViewModel() {
    val IndividualQuestion:LiveData<List<ARQuestionListModel>>
        get() = repo._IndividualQuestion
    fun getQuestion(QuestionId:List<String>){
       viewModelScope.launch {
           repo.getQuestion(QuestionId)
           Log.d("VMIQ",IndividualQuestion.value.toString())
       }
    }
}