package com.example.quizzify.DI

import com.example.quizzify.Retrofit.QuestionSetAPI
import com.example.quizzify.Retrofit.TriviaCategoryAPI
import com.example.quizzify.datamodels.TriviaCategory
import com.example.quizzify.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton//Application Wide one object
    @Provides
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(Constants.BaseURl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideAPI(retrofit:Retrofit):TriviaCategoryAPI{
        return retrofit.create(TriviaCategoryAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideQuestionSetAPI(retrofit:Retrofit):QuestionSetAPI{
        return retrofit.create(QuestionSetAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideFirestore():FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }
}