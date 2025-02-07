package com.example.quizzify.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.Repository.SearchRepo
import com.example.quizzify.datamodels.QuizSetModel
import kotlinx.coroutines.launch

class SearchRoomViewModel(private val SearchRepo:SearchRepo):ViewModel() {
    val SearchResult: LiveData<List<QuizSetModel>>
        get()=SearchRepo._QuizSetList

    fun getSearchResults(SearchQuery:String){
        viewModelScope.launch {
            SearchRepo.getQuizSetList(SearchQuery)
        }
    }
}