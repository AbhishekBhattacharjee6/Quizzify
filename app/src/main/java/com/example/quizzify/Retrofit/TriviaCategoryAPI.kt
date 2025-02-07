package com.example.quizzify.Retrofit

import com.example.quizzify.datamodels.QuizCategories
import com.example.quizzify.datamodels.TriviaCategory
import retrofit2.Response
import retrofit2.http.GET


interface TriviaCategoryAPI {
    @GET("api_category.php")
   suspend fun getCategory():Response<QuizCategories>
}