package com.pramoh.kbcqna.presentation

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel : ViewModel() {

    private var timer: CountDownTimer? = null

    private val _didTimeEnd = MutableLiveData<Boolean>()
    val didTimerEnd: LiveData<Boolean>
        get() = _didTimeEnd

    private val _timerValue = MutableLiveData<Long>()
    val timerValue: LiveData<Long>
        get() = _timerValue

    fun startTimer(seconds: Long) {
        if (timer == null) {
            timer = object : CountDownTimer((seconds+1)*1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    _timerValue.postValue(millisUntilFinished/1000)
                }

                override fun onFinish() {
                    _didTimeEnd.postValue(true)
                }
            }.start()
        }
    }

    fun cancelTimer() {
        timer?.cancel()
        timer = null
    }
}
