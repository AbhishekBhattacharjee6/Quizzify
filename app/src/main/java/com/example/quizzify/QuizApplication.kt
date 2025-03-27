package com.example.quizzify

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.quizzify.DI.ApplicationComponent
import com.example.quizzify.DI.DaggerApplicationComponent
import com.example.quizzify.Manager.ExpiredContestWorker
import com.example.quizzify.Manager.MonthlyUpdateWorker
import com.example.quizzify.Manager.PreRegNotificationWorker
import com.example.quizzify.Manager.PreRegisteredRoomValidCheck
import java.util.Calendar
import java.util.concurrent.TimeUnit

class QuizApplication:Application(){
    lateinit var applicationComponent: ApplicationComponent
    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder().build()
        scheduleNextMonthWorker(this)
        inititateExpiredContestWorker(this)
        initiatePreRegisteredRoomValidCheck(this)
        setupDailyWorker(this)
    }
    private fun setupDailyWorker(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<PreRegNotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(getInitialDelay(), TimeUnit.MILLISECONDS) // Start at a fixed time
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "DailyContestNotification",
            ExistingPeriodicWorkPolicy.KEEP, // Ensures it doesn’t create duplicates
            workRequest
        )
    }
    fun scheduleNextMonthWorker(context: Context) {
        val delay = getDelayUntilNextMonthFirstDay()

        val workRequest = OneTimeWorkRequestBuilder<MonthlyUpdateWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiresCharging(true) // Runs only when charging
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "MonthlyUpdateWorker",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    // Function to calculate delay until next month's first day at midnight
    private fun getDelayUntilNextMonthFirstDay(): Long {
        val now = Calendar.getInstance()
        val nextMonth = Calendar.getInstance().apply {
            add(Calendar.MONTH, 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return nextMonth.timeInMillis - now.timeInMillis
    }
    fun inititateExpiredContestWorker(context:Context){
        val workRequest = PeriodicWorkRequestBuilder<ExpiredContestWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "ExpiredContestWorker",
            ExistingPeriodicWorkPolicy.KEEP, // Prevents re-scheduling if already scheduled
            workRequest
        )
    }
    fun initiatePreRegisteredRoomValidCheck(context: Context){
        val workRequest = OneTimeWorkRequestBuilder<PreRegisteredRoomValidCheck>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "PreRegisteredRoomValidCheck",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    private fun getInitialDelay(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 8) // Set to 8 AM
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)

        val now = java.util.Calendar.getInstance()
        if (now.after(calendar)) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1) // Schedule for next day if past 8 AM
        }

        return calendar.timeInMillis - now.timeInMillis
    }
}