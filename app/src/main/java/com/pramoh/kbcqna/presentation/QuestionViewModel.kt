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

    private val listOfQuestions = mutableListOf<Question>() // TODO: get this from db or api

    var currentSelectedOption: Int = 0

    init {
        _isLockButtonClickable.postValue(false)

        listOfQuestions.add(
            Question("What is the capital of India?",
                "Mumbai", "Chennai", "Delhi", "Kolkata",
                3, "Rs. 20,000", "INDIA"
            )
        )
    }

    fun setCurrentQuestion(quesNumber: Int) {
        // TODO: handle if ques number is less than 1, and increment by 1 when u get the list
//        _currentQuestion.postValue(listOfQuestions[quesNumber-1])
        _currentQuestion.postValue(listOfQuestions[0])
    }

    fun onLifelineClick() {
        // TODO: logic for lifeline click
    }

    fun onOptionClick(option: Int) {
        _isLockButtonClickable.postValue(true)
        currentSelectedOption = option
    }
}