package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.LiveQuizRepository
import com.example.quizzify.ViewModels.ARIndividualQuestionViewModel
import javax.inject.Inject

class ARIndividualQuestionViewModelFactory @Inject constructor(private val repo:LiveQuizRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ARIndividualQuestionViewModel(repo) as T
    }
}