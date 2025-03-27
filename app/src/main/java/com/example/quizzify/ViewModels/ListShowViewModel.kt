package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.Repository.SavedListRepo
import com.example.quizzify.datamodels.SavedCollectionModel
import kotlinx.coroutines.launch

class ListShowViewModel(private val repo: SavedListRepo):ViewModel(){
    val SavedNameListSet: LiveData<List<SavedCollectionModel>>
        get()=repo._SavedListNames
    fun getSavedLists(){
        viewModelScope.launch {
            repo.getSavedListNames()
        }
    }
}