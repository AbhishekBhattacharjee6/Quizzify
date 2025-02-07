package com.example.quizzify.Retrofit

import com.example.quizzify.QuestionSetDataModels.MainDataClass
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface QuestionSetAPI {
    @GET("api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int = 10,
        @Query("category") category: Int = 9,
        @Query("difficulty") difficulty: String = "easy",
        @Query("type") type: String = "multiple",
        @Query("encode") encode: String = "base64"
    ): Response<MainDataClass>
}