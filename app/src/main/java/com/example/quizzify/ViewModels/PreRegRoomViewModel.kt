package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.Repository.PreRegRepo
import com.example.quizzify.datamodels.QuizSetModel
import kotlinx.coroutines.launch

class PreRegRoomViewModel( val PreRegRepo:PreRegRepo):ViewModel() {
    val PreRegRoomList:LiveData<List<QuizSetModel>>
        get() = PreRegRepo._PreRegRoomList
    init {
        viewModelScope.launch {
            PreRegRepo.getPreRegRooms()
        }
    }
    fun Refresh(){
        viewModelScope.launch {
            PreRegRepo.getPreRegRooms()
        }
    }
}