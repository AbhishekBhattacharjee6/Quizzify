package com.example.quizzify.DI

import com.example.quizzify.Fragments.ActiveRoomQuestionList
import com.example.quizzify.Fragments.ActiveRooms
import com.example.quizzify.Fragments.CategoryFragment
import com.example.quizzify.Fragments.CreateRoom
import com.example.quizzify.Fragments.EditQuestionFragment
import com.example.quizzify.Fragments.LiveContest
import com.example.quizzify.Fragments.PreRegisteredRooms
import com.example.quizzify.Fragments.QuizQuestionFragment
import com.example.quizzify.Fragments.SearchRoom
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface ApplicationComponent {

    fun injectCategory(frag:CategoryFragment)
    fun injectQuiz(frag:QuizQuestionFragment)
    fun injectActiveRoom(frag:ActiveRooms)
    fun injectQuestion(frag:CreateRoom)
    fun injectQuestionList(frag:ActiveRoomQuestionList)
    fun injectSearchRoom(frag:SearchRoom)
    fun injectPreRegRooms(frag:PreRegisteredRooms)
    fun injectliveContest(frag:LiveContest)
    fun injectEditQuestions(frag:EditQuestionFragment)

}