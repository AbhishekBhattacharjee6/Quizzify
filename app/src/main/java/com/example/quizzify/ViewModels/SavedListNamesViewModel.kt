package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.Repository.SavedListRepo
import kotlinx.coroutines.launch

class SavedListNamesViewModel(private val repo:SavedListRepo):ViewModel() {
    val SavedNameListSet:LiveData<List<String>>
        get()=repo._SavedListNames
    fun getListNames(){
        viewModelScope.launch {
            repo.getSavedListNames()
        }
    }
}