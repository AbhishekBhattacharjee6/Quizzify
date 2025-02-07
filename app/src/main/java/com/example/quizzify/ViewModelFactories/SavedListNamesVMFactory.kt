package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.SavedListRepo
import com.example.quizzify.ViewModels.SavedListNamesViewModel
import javax.inject.Inject

class SavedListNamesVMFactory @Inject constructor(private val repo:SavedListRepo):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SavedListNamesViewModel(repo) as T
    }

}