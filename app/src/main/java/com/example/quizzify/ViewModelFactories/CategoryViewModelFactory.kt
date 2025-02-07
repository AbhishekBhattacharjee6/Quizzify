package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.QuizRepository
import com.example.quizzify.ViewModels.CategoryViewModel
import javax.inject.Inject

class CategoryViewModelFactory @Inject constructor(private val repo:QuizRepository):ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(repo) as T
    }
}