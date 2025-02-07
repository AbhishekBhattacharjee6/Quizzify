package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.LiveQuizRepository
import com.example.quizzify.ViewModels.QuestionListViewModel
import javax.inject.Inject

class QuestionListViewModelFactory @Inject constructor(val repo:LiveQuizRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuestionListViewModel(repo) as T
    }

}