package com.example.quizzify.Manager

import android.content.Context
import android.icu.util.Calendar
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.quizzify.datamodels.FullQuizSetModel
import com.example.quizzify.datamodels.RoomSetModel
import com.example.quizzify.datamodels.WorkerQuizSetModel
import com.example.quizzify.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ExpiredContestWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            if(Constants.UID==null || Constants.UID=="Abhishek123")
                Result.failure()
            checkAndRemoveExpiredContests()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun removeQuizSet(UID: String) = withContext(Dispatchers.IO) {
        try {
            val document = FirebaseFirestore.getInstance()
                .collection("Quizset")
                .document(UID)
                .get()
                .await()

            val questionIDs = document.get("QuestionIds") as? List<String> ?: emptyList()
            questionIDs.forEach { removeIndividualQuestion(it) }

            // Delete QuizSet document
            FirebaseFirestore.getInstance().collection("Quizset").document(UID).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun removeIndividualQuestion(questionID: String) = withContext(Dispatchers.IO) {
        try {
            FirebaseFirestore.getInstance().collection("LiveQuestions").document(questionID)
                .delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun checkAndRemoveExpiredContests() = withContext(Dispatchers.IO) {
        val currentDate = getCurrentDate()

        val quizList = getQuizSetListFromFirestore().toMutableList()
        for (quiz in quizList) {
            val endDate = quiz.ValidTill.toIntOrNull() ?: continue
            if (currentDate >= endDate + 5) {
                removeQuizSet(quiz.QuizSetUid)
                quizList.remove(quiz)
            }
        }
        val FinalQuizList=quizList.map { it.toMap() }
        FirebaseFirestore.getInstance().collection("UIDs").document(Constants.UID).update("QuizSetIDs", FinalQuizList).await()
    }

    private suspend fun getQuizSetListFromFirestore(): List<FullQuizSetModel> {
        return try {
            val document = FirebaseFirestore.getInstance()
                .collection("UIDs")
                .document(Constants.UID)
                .get()
                .await()

            val quizSetList = document.get("QuizSetIDs") as? List<Map<String, Any>> ?: emptyList()
            quizSetList.map { map ->
             FullQuizSetModel(
                 Duration = map["Duration"] as? String ?: "",
                 PassCode = map["PassCode"] as? String ?: "",
                 QuestionIds = map["QuestionIds"] as? List<String> ?: emptyList(),
                 QuizSetUid = map["QuizSetUid"] as? String ?: "",
                 RoomName = map["RoomName"] as? String ?: "",
                 SaveAllowed = map["SaveAllowed"] as? Boolean ?: false,
                 StartTime = map["StartTime"] as? String ?: "",
                 UserUid = map["UserUid"] as? String ?: "",
                 ValidTill = map["ValidTill"] as? String ?: ""
             )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun getCurrentDate(): Int {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = (calendar.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        return "$year$month$day".toInt()
    }
}