package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.Repository.QuizRepository
import com.example.quizzify.datamodels.TriviaCategory
import kotlinx.coroutines.launch

class CategoryViewModel(val repo:QuizRepository):ViewModel() {
    val categoryList:LiveData<List<TriviaCategory>>
        get() = repo.categories
    init {
        viewModelScope.launch {
            repo.getCategories()
        }
    }
}