package com.example.quizzify.ViewModels

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountDownTimerViewModel():ViewModel() {
    private var countDownTimer:CountDownTimer? = null
   private val remainingtime= MutableLiveData<Long>()
    val _remainingtime:LiveData<Long>
        get() = remainingtime
    fun startTimer(Duration:String) {
        val millisinFuture = Duration.toInt() * 60 * 1000
        val countDownInterval = 1000
        countDownTimer=object :CountDownTimer(millisinFuture.toLong(),countDownInterval.toLong()){
            override fun onTick(p0: Long) {
              remainingtime.postValue(p0)
            }

            override fun onFinish() {
               remainingtime.postValue(0)
            }

        }
    }

}