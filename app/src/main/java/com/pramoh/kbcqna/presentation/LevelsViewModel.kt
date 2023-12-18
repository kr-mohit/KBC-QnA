package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.domain.usecases.GetQuestionsFromDBUseCase
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelsViewModel @Inject constructor(
    private val getQuestionsFromDBUseCase: GetQuestionsFromDBUseCase
): ViewModel(){

    private var onStartClicked: Boolean = false // TODO: Check if this can be removed

    private val _questionsFromDB = MutableLiveData<Response<List<Question>>>()
    val questionsFromDB: LiveData<Response<List<Question>>>
        get() = _questionsFromDB

    fun getQuestionsForLevel(level: Int) {
        viewModelScope.launch {
            _questionsFromDB.postValue(getQuestionsFromDBUseCase.invoke(level))
        }
    }

    fun setOnStartClicked(value: Boolean) {
        onStartClicked = value
    }

    fun getOnStartClicked(): Boolean {
        return onStartClicked
    }
}