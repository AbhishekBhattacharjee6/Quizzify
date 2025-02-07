package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.IndividualQuestionFetchRepo
import com.example.quizzify.ViewModels.IndividualQuestionViewModel
import javax.inject.Inject

class IndividualQuestionViewModelFactory @Inject constructor(private val QuestionRepo:IndividualQuestionFetchRepo):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return IndividualQuestionViewModel(QuestionRepo) as T
    }
}