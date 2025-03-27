package com.example.quizzify.Manager

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.quizzify.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MonthlyUpdateWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
       return try{
           if(Constants.UID==null || Constants.UID=="Abhishek123")
               return Result.failure()

           if (isFirstDayOfMonth()) {
               updateDatabase()
              // Schedule for next month
           }
           Result.success()
       }catch (e:Exception){
           e.printStackTrace()
           Result.failure()
       }
    }
    private suspend fun updateDatabase(){
        try{
            FirebaseFirestore.getInstance().collection("UIDInfo").document(Constants.UID).update("Days Active",emptyList<Map<String,Any>>()).await()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    private fun isFirstDayOfMonth(): Boolean {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_MONTH) == 1
    }


}