package com.example.quizzify.Manager

import android.content.Context
import android.icu.util.Calendar
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.quizzify.datamodels.QuizSetModel
import com.example.quizzify.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PreRegisteredRoomValidCheck(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            if(Constants.UID==null || Constants.UID=="Abhishek123")
                Result.failure()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun checkAndRemoveRooms() = withContext(Dispatchers.IO) {
        val currentDate = getCurrentDate()

        val PreRegList=getPreRegisteredRooms().toMutableList()
        for(room in PreRegList){
            val endDate=room.ValidTill.toIntOrNull() ?: continue
            if(currentDate>= 5+endDate){
                PreRegList.remove(room)
            }
            else{
                if(checkExistence(room.QuizSetId))
                    PreRegList.remove(room)
            }
        }
        val FinalPreRegList=PreRegList.map { it.toMap()}
        FirebaseFirestore.getInstance().collection("UID").document(Constants.UID).update("PreRegisteredRooms",FinalPreRegList).await()
    }

    private suspend fun getPreRegisteredRooms(): List<QuizSetModel> {
        return try {
            val document = FirebaseFirestore.getInstance()
                .collection("UID")
                .document(Constants.UID)
                .get()
                .await()

            val preRegList = document.get("PreRegisteredRooms") as? List<Map<String, Any>> ?: emptyList()


            preRegList.map { QuizSetModel.fromMap(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

  private suspend fun checkExistence(room:String):Boolean= withContext(Dispatchers.IO){
      return@withContext try {
          val document=FirebaseFirestore.getInstance().collection("Quizset").document(room).get().await()
          document.exists()
      }catch (e:Exception){
          e.printStackTrace()
          false
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