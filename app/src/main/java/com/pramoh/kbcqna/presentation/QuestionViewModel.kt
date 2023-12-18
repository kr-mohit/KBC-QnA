package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(
//    private val getQuestionListUseCase: GetQuestionListUseCase
) :ViewModel() {

    private var moneyWonTillNow: Int = 0
    private var lastSafeZone: Int = 0
    private var currentSelectedOption: Int = 0
    private var questionToBeAsked: Int = 0

    private val _questionsLiveData = MutableLiveData<Response<List<Question>>>()
    val questionsLiveData: LiveData<Response<List<Question>>>
        get() = _questionsLiveData

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question>
        get() = _currentQuestion

    private val _lifelines = MutableLiveData<List<Boolean>>()
    val lifelines: LiveData<List<Boolean>>
        get() = _lifelines

//    fun fetchQuestions(url: String) {
//        viewModelScope.launch {
//            _questionsLiveData.postValue(Response.Loading())
//            val response = getQuestionListUseCase.invoke(url)
//            _questionsLiveData.postValue(response)
//        }
//    }

    fun setListOfQuestions(list: List<Question>) {
        _questionsLiveData.postValue(Response.Success(list))
        setQuestionToBeAsked(1)
        setLifelines()
    }

    fun setCurrentQuestion(quesNumber: Int) {
        val currentQuestion = questionsLiveData.value!!.data!![quesNumber-1]
        _currentQuestion.postValue(currentQuestion)
        setMoneyWonTillNow(quesNumber)
        setLastSafeZone(quesNumber)
    }

    private fun setLifelines() {
        _lifelines.postValue(listOf(true, true, true, true))
    }

    fun onLifelineClick(lifeline: Int) {
        val lifelineList = lifelines.value!!.toMutableList()
        lifelineList[lifeline-1] = false
        _lifelines.postValue(lifelineList)
    }

    fun setCurrentSelectedOption(option: Int) {
        currentSelectedOption = option
    }

    private fun setMoneyWonTillNow(quesNumber: Int) {
        moneyWonTillNow = (if (quesNumber < 2) 0 else questionsLiveData.value!!.data!![quesNumber-2].prizeAmount) // TODO: do something about this, can't set here
    }

    private fun setLastSafeZone(quesNumber: Int) {
        lastSafeZone = if (quesNumber >= 10) 640000 else if (quesNumber >= 7) 80000 else 0
    }

    fun setQuestionToBeAsked(number: Int) {
        questionToBeAsked = number
    }

    fun getCurrentSelectedOption(): Int {
        return currentSelectedOption
    }

    fun getMoneyWonTillNow(): Int {
        return moneyWonTillNow
    }

    fun getLastSafeZone(): Int {
        return lastSafeZone
    }

    fun getQuestionToBeAsked(): Int {
        return questionToBeAsked
    }
}