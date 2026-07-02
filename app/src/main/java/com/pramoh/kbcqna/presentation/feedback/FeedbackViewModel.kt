package com.pramoh.kbcqna.presentation.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.usecases.SubmitFeedbackUseCase
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val submitFeedbackUseCase: SubmitFeedbackUseCase
) : ViewModel() {

    private val _submitResult = MutableLiveData<Response<Unit>?>()
    val submitResult: LiveData<Response<Unit>?>
        get() = _submitResult

    fun submitFeedback(name: String, email: String, type: String, message: String) {
        _submitResult.value = Response.Loading()
        viewModelScope.launch {
            val result = submitFeedbackUseCase(name, email, type, message)
            _submitResult.postValue(result)
        }
    }

    fun resetSubmitResult() {
        _submitResult.value = null
    }
}