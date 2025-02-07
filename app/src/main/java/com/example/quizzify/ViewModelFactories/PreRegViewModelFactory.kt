package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.PreRegRepo
import com.example.quizzify.ViewModels.PreRegRoomViewModel
import javax.inject.Inject

class PreRegViewModelFactory @Inject constructor(private val PreRegRepo:PreRegRepo):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PreRegRoomViewModel(PreRegRepo) as T
    }
}