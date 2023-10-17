package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramoh.kbcqna.domain.model.Question

class QuestionViewModel:ViewModel() {

    private val _isLockButtonClickable = MutableLiveData<Boolean>()
    val isLockButtonClickable: LiveData<Boolean>
        get() = _isLockButtonClickable

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question>
        get() = _currentQuestion

    var currentSelectedOption: Int = 0

    init {
        _isLockButtonClickable.postValue(false)
        _currentQuestion.postValue(
            Question(
                "What is the capital of India?",
                "Mumbai",
                "Chennai",
                "Delhi",
                "Kolkata",
                3,
                "Rs. 20,000",
                "INDIA"
        )
        )
    }

    fun onLifelineClick() {
        // TODO: logic for lifeline click
    }

    fun onOptionClick(option: Int) {
        _isLockButtonClickable.postValue(true)
        currentSelectedOption = option
    }
}