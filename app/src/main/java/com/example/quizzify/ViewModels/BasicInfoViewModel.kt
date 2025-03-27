package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.quizzify.Repository.ProfileRepo
import com.example.quizzify.datamodels.BasicInfoDataModel

class BasicInfoViewModel(val repo:ProfileRepo):ViewModel(){
    val BasicInfo: LiveData<List<BasicInfoDataModel>>
    get() = repo._BasicInfo

    fun getBasicInfo(){
        repo.getBasicInfo()
    }
}