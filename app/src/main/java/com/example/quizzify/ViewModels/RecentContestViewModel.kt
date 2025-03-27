package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.quizzify.Repository.ProfileRepo
import com.example.quizzify.datamodels.RecentContestDataModel

class RecentContestViewModel(val repo:ProfileRepo):ViewModel(){
    val _RecentContest:LiveData<List<RecentContestDataModel>>
        get()=repo._RecentContests

    fun getRecentContest(){
        repo.getRecentContests()
    }
}