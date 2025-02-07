package com.example.quizzify.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.quizzify.QuestionSetDataModels.Result
import com.example.quizzify.Retrofit.QuestionSetAPI
import com.example.quizzify.Retrofit.TriviaCategoryAPI
import com.example.quizzify.datamodels.TriviaCategory
import javax.inject.Inject

class QuizRepository @Inject constructor(private val TriviaCategoryApi: TriviaCategoryAPI, private val QuestionSetApi:QuestionSetAPI) {

    //For CategorySet
    private val _categories = MutableLiveData<List<TriviaCategory>>(emptyList())
    val categories: LiveData<List<TriviaCategory>>
        get() = _categories

    //For QuestionSet

    private val _questionset = MutableLiveData<List<Result>>(emptyList())
    val questionset : LiveData<List<Result>>
        get() = _questionset

    //Fetch Functions

    suspend fun getCategories(){
        val result=TriviaCategoryApi.getCategory()
        if(result.isSuccessful && result.body()!=null){
            _categories.postValue(result.body()!!.trivia_categories)
        }
    }

    suspend fun getQuestionsSet(num:Int,cat:Int,difficulty:String){
        val result = QuestionSetApi.getQuestions(num,cat,difficulty,"multiple")
        if(result.isSuccessful && result.body()!=null){
            _questionset.postValue(result.body()!!.results)
        }
    }

}