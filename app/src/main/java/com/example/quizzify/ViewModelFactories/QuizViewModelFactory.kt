package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.QuizRepository
import com.example.quizzify.ViewModels.QuizViewModel
import javax.inject.Inject

class QuizViewModelFactory @Inject constructor(private val repo:QuizRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuizViewModel(repo) as T
    }
}