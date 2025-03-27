package com.example.quizzify.DI

import com.example.quizzify.BottomSheets.LeaderBoardBottomSheet
import com.example.quizzify.BottomSheets.SavedListBottomSheet
import com.example.quizzify.Dialogs.CreateRoomDialog
import com.example.quizzify.Dialogs.NewSaveListDialog
import com.example.quizzify.Dialogs.PreRegisterDialog
import com.example.quizzify.Dialogs.ProfileSetupDialog
import com.example.quizzify.Dialogs.SaveListEditDialog
import com.example.quizzify.Fragments.ActiveRoomQuestionList
import com.example.quizzify.Fragments.ActiveRooms
import com.example.quizzify.Fragments.AddNewQuestion
import com.example.quizzify.Fragments.CategoryFragment
import com.example.quizzify.Fragments.CreateRoom
import com.example.quizzify.Fragments.EditQuestionFragment
import com.example.quizzify.Fragments.FinalResultFragment
import com.example.quizzify.Fragments.LiveContest
import com.example.quizzify.Fragments.PracticeFinalResultFragment
import com.example.quizzify.Fragments.PreRegisteredRooms
import com.example.quizzify.Fragments.ProfileFragment
import com.example.quizzify.Fragments.QuizQuestionFragment
import com.example.quizzify.Fragments.SaveCollectionsFragment
import com.example.quizzify.Fragments.SavedCollectionQuestionList
import com.example.quizzify.Fragments.SearchRoom
import com.example.quizzify.Fragments.TestResults
import com.example.quizzify.MainActivity
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
    fun injectAddQuestions(frag:AddNewQuestion)
    fun injectFinalResult(frag:FinalResultFragment)
    fun injectSaveCollectionsFragment(frag: SaveCollectionsFragment)
    fun injectSavedCollectionQuestionList(frag:SavedCollectionQuestionList)
    fun injectPracticeFinalResult(frag:PracticeFinalResultFragment)
    fun injectPreRegisterDialog(clas: PreRegisterDialog)
    fun injectNewSaveListDialog(clas:NewSaveListDialog)
    fun injectSaveListEditDialog(clas:SaveListEditDialog)

    fun injectCreateRoomDialog(clas:CreateRoomDialog)

    fun injectSavedListBottomSheet(clas: SavedListBottomSheet)

    fun injectLeaderBoardBottomSheet(clas: LeaderBoardBottomSheet)
    fun injectProfileFrag(frag: ProfileFragment)

    fun injectTestResults(frag:TestResults)

    fun injectPracticeTestResults(frag:PracticeFinalResultFragment)
    fun injectMainActivity(clas:MainActivity)

    fun injectProfileSetupDialog(frag: ProfileSetupDialog)
}