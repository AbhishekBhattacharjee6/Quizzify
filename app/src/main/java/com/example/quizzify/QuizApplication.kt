package com.example.quizzify

import android.app.Application
import com.example.quizzify.DI.ApplicationComponent
import com.example.quizzify.DI.DaggerApplicationComponent

class QuizApplication:Application(){
    lateinit var applicationComponent: ApplicationComponent
    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder().build()
    }

}