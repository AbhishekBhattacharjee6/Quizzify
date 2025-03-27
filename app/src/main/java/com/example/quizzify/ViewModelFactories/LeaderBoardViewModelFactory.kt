package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.LeaderBoardRepository
import com.example.quizzify.ViewModels.LeaderBoardViewModel
import com.example.quizzify.datamodels.LeaderBoardModel
import javax.inject.Inject

class LeaderBoardViewModelFactory @Inject constructor(private val LeaderBoardRepo:LeaderBoardRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LeaderBoardViewModel(LeaderBoardRepo) as T
    }
}