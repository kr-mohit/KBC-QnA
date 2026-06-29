package com.pramoh.kbcqna.presentation.questionnaire

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.domain.usecases.GetQuestionListUseCase
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val getQuestionListUseCase: GetQuestionListUseCase
) :ViewModel() {

    private var moneyWonTillNow: Int = 0
    private var lastSafeZone: Int = 0
    private var currentSelectedOption: Int = 0
    private var isOnlineGame: Boolean = false

    private val _questionsLiveData = MutableLiveData<Response<List<Question>>>()
    val questionsLiveData: LiveData<Response<List<Question>>>
        get() = _questionsLiveData

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question>
        get() = _currentQuestion

    private val _lifelines = MutableLiveData<List<Boolean>>()
    val lifelines: LiveData<List<Boolean>>
        get() = _lifelines

    fun fetchQuestions(url: String, questionCount: Int? = null) {
        isOnlineGame = true
        viewModelScope.launch {
            _questionsLiveData.postValue(Response.Loading())
            val response = getQuestionListUseCase.invoke(url, questionCount)
            _questionsLiveData.postValue(response)
        }
    }

    fun fetchQuestionsOffline() {
        isOnlineGame = false
        viewModelScope.launch {
            _questionsLiveData.postValue(Response.Loading())
            val response = getQuestionListUseCase.invokeLocal()
            _questionsLiveData.postValue(response)
        }
    }

    fun isOnlineGame(): Boolean {
        return isOnlineGame
    }

    fun setCurrentQuestion(quesNumber: Int) {
        val currentQuestion = questionsLiveData.value!!.data!![quesNumber-1]
        _currentQuestion.postValue(currentQuestion)
        setMoneyWonTillNow(quesNumber)
        setLastSafeZone(quesNumber)
    }

    fun setLifelines() {
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
        moneyWonTillNow = if (quesNumber < 2) {
            0
        } else {
            questionsLiveData.value!!.data!![quesNumber-2].prizeAmount
        }
    }

    private fun setLastSafeZone(quesNumber: Int) {
        val questions = questionsLiveData.value?.data ?: emptyList()
        val total = questions.size

        lastSafeZone = if (total >= 3) {
            // Scale safe zones to 1/3 and 2/3 of the total questions length (simulating Q5 and Q10 milestones)
            val safeZone1Index = (total * 6) / 15 - 1
            val safeZone2Index = (total * 9) / 15 - 1

            val currentZeroBasedIndex = quesNumber - 1
            if (currentZeroBasedIndex > safeZone2Index && safeZone2Index in questions.indices) {
                questions[safeZone2Index].prizeAmount
            } else if (currentZeroBasedIndex > safeZone1Index && safeZone1Index in questions.indices) {
                questions[safeZone1Index].prizeAmount
            } else {
                0
            }
        } else {
            0
        }
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

    fun getQuestionsCount(): Int {
        return questionsLiveData.value?.data?.size ?: 15
    }

    fun getFinalPrizeAmount(): Int {
        val questions = questionsLiveData.value?.data ?: emptyList()
        return if (questions.isNotEmpty()) questions.last().prizeAmount else 100000000
    }

    fun getListOfPrizes(): List<String> {
        val questions = questionsLiveData.value?.data ?: emptyList()
        return questions.map { it.prizeAmount }.sortedDescending().map { MoneyTypeConversionUtil.convertToString(it) }
    }
}