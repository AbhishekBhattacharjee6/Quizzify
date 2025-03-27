package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.ProfileRepo
import com.example.quizzify.ViewModels.BasicInfoViewModel
import javax.inject.Inject

class BasicInfoViewModelFactory @Inject constructor(val repo: ProfileRepo) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BasicInfoViewModel(repo) as T
    }
}