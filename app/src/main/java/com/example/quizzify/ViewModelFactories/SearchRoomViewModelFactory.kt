package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.SearchRepo
import com.example.quizzify.ViewModels.SearchRoomViewModel
import javax.inject.Inject

class SearchRoomViewModelFactory @Inject constructor(private val SearchRepo:SearchRepo):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchRoomViewModel(SearchRepo) as T
    }
}