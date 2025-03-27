package com.example.quizzify.ViewModelFactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quizzify.Repository.ProfileRepo
import com.example.quizzify.ViewModels.RecentContestViewModel
import javax.inject.Inject

class RecentContestViewModelFactory @Inject constructor(private val repo:ProfileRepo):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecentContestViewModel(repo) as T
    }
}