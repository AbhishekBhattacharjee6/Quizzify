package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.Repository.IndividualQuestionFetchRepo
import com.example.quizzify.datamodels.QuestionModel
import kotlinx.coroutines.launch

class IndividualQuestionViewModel(val QuestionRepo:IndividualQuestionFetchRepo):ViewModel() {
    private val _questions = MutableLiveData<List<QuestionModel>>()
    val questions: LiveData<List<QuestionModel>> get() = _questions

    fun getQuestions(QuestionIDs: List<String>) {
        val questionsList = mutableListOf<QuestionModel>()
        var fetchedCount = 0  // Counter to track fetched questions

        for (id in QuestionIDs) {
            QuestionRepo.getQuestion(id) { question ->
                question?.let { questionsList.add(it) }

                fetchedCount++ // Increase count when a question fetch completes

                // Once all questions are fetched, update LiveData
                if (fetchedCount == QuestionIDs.size) {
                    _questions.postValue(questionsList)
                }
            }
        }
    }
}