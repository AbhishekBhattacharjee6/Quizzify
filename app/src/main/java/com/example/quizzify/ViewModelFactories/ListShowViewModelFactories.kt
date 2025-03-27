package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.SavedListRepo
import com.example.quizzify.ViewModels.ListShowViewModel
import javax.inject.Inject

class ListShowViewModelFactories @Inject constructor(private val repo: SavedListRepo):
    ViewModelProvider.Factory{
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return ListShowViewModel(repo) as T
    }

}