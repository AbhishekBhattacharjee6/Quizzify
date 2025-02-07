package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.LiveQuizRepository
import com.example.quizzify.ViewModels.LiveQuizUIDViewModel
import javax.inject.Inject

class LiveQuizUIDVIewModelFactory @Inject constructor(private val repo:LiveQuizRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LiveQuizUIDViewModel(repo) as T
    }
}