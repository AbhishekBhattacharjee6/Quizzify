package com.example.quizzify.Manager

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.quizzify.MainActivity
import com.example.quizzify.R
import com.example.quizzify.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class PreRegNotificationWorker(context:Context,workerParams: WorkerParameters):Worker(context,workerParams){
    override fun doWork(): Result {
        if(Constants.UID==null || Constants.UID=="Abhishek123")
            return Result.failure()
        checkAndSendNotifications()
        return Result.success()
    }

    private fun checkAndSendNotifications(){
        val dateToday=getDate()
        FirebaseFirestore.getInstance().collection("UIDs").document(Constants.UID).get().addOnSuccessListener {
            if(it.exists()){
                val PreRegRooms=it.get("PreRegisteredRooms") as? List<Map<String, Any>> ?: emptyList()
                for(rooms in PreRegRooms){
                    val startDate=rooms["StartFrom"] as? String
                    val roomName=rooms["RoomName"] as? String
                    if(startDate==dateToday){
                        sendNotification(roomName!!,applicationContext)
                    }
                }
            }
        }

    }
    private fun getDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val new_month = (month).toString().padStart(2, '0')
        val new_day = (day).toString().padStart(2, '0')
        val date = year.toString() + new_month.toString() + new_day.toString()
        return date
    }
    private fun sendNotification(contestTitle:String, context:Context){
        val channelID="Contest_Channel"
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val name = "Contest Notifications"
            val descriptionText = "Notifications for contests"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel=NotificationChannel(channelID,name,importance).apply {
                description=descriptionText
            }
           val notificationManager:NotificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val intent=Intent(context,MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification= NotificationCompat.Builder(context,channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(contestTitle)
            .setContentText("Your scheduled contest is starting now!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()


        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(1, notification)
    }

}