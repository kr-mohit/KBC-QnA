package com.pramoh.kbcqna.presentation.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pramoh.kbcqna.domain.repository.MainRepository
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.pramoh.kbcqna.domain.model.Question

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _prizeOptions = MutableLiveData<Response<List<AdminPrizeOption>>>()
    val prizeOptions: LiveData<Response<List<AdminPrizeOption>>> get() = _prizeOptions

    private val _updateStatus = MutableLiveData<Response<Unit>?>()
    val updateStatus: LiveData<Response<Unit>?> get() = _updateStatus

    private val _appUpdateInfo =
        MutableLiveData<Response<com.pramoh.kbcqna.domain.model.AppUpdateInfo>>()
    val appUpdateInfo: LiveData<Response<com.pramoh.kbcqna.domain.model.AppUpdateInfo>> get() = _appUpdateInfo

    private val _updateInfoStatus = MutableLiveData<Response<Unit>?>()
    val updateInfoStatus: LiveData<Response<Unit>?> get() = _updateInfoStatus

    fun loadPrizeConfig() {
        _prizeOptions.value = Response.Loading()
        viewModelScope.launch {
            val allPrizes = mainRepository.getUniquePrizeAmounts()
            val remoteResponse = mainRepository.getRemotePrizeAmounts()

            when (remoteResponse) {
                is Response.Success -> {
                    val activePrizes = remoteResponse.data ?: emptyList()
                    val options = allPrizes.map { prize ->
                        AdminPrizeOption(prize, activePrizes.contains(prize))
                    }
                    _prizeOptions.postValue(Response.Success(options))
                }

                is Response.Error -> {
                    val options = allPrizes.map { prize ->
                        AdminPrizeOption(prize, false)
                    }
                    _prizeOptions.postValue(Response.Success(options))
                }

                is Response.Loading -> {}
            }
        }
    }

    fun updateRemotePrizes(selectedPrizes: List<Int>) {
        _updateStatus.value = Response.Loading()
        viewModelScope.launch {
            val result = mainRepository.updateRemotePrizeAmounts(selectedPrizes)
            _updateStatus.postValue(result)
        }
    }

    fun resetUpdateStatus() {
        _updateStatus.value = null
    }

    fun loadAppUpdateConfig() {
        _appUpdateInfo.value = Response.Loading()
        viewModelScope.launch {
            val result = mainRepository.checkForAppUpdate()
            _appUpdateInfo.postValue(result)
        }
    }

    fun updateAppUpdateConfig(newVersion: String, dialogType: String, updateMessage: String) {
        _updateInfoStatus.value = Response.Loading()
        viewModelScope.launch {
            val result =
                mainRepository.updateRemoteAppUpdateInfo(newVersion, dialogType, updateMessage)
            _updateInfoStatus.postValue(result)
        }
    }

    fun resetUpdateInfoStatus() {
        _updateInfoStatus.value = null
    }

    private val _leaderboardOptions = MutableLiveData<Response<List<AdminLeaderboardOption>>>()
    val leaderboardOptions: LiveData<Response<List<AdminLeaderboardOption>>> get() = _leaderboardOptions

    private val _deleteLeaderboardStatus = MutableLiveData<Response<Unit>?>()
    val deleteLeaderboardStatus: LiveData<Response<Unit>?> get() = _deleteLeaderboardStatus

    fun loadLeaderboardConfig() {
        _leaderboardOptions.value = Response.Loading()
        viewModelScope.launch {
            val result = mainRepository.getRemoteLeaderboardWithDocIds()
            when (result) {
                is Response.Success -> {
                    val options = result.data?.map { player ->
                        AdminLeaderboardOption(
                            player.docId,
                            player.playerName,
                            player.moneyWon,
                            false
                        )
                    } ?: emptyList()
                    _leaderboardOptions.postValue(Response.Success(options))
                }

                is Response.Error -> {
                    _leaderboardOptions.postValue(
                        Response.Error(
                            result.error ?: "Failed to load leaderboard"
                        )
                    )
                }

                is Response.Loading -> {}
            }
        }
    }

    fun deleteSelectedPlayers(docIds: List<String>) {
        _deleteLeaderboardStatus.value = Response.Loading()
        viewModelScope.launch {
            val result = mainRepository.deleteRemoteLeaderboardPlayers(docIds)
            _deleteLeaderboardStatus.postValue(result)
        }
    }

    fun resetDeleteLeaderboardStatus() {
        _deleteLeaderboardStatus.value = null
    }

    private val _feedbackOptions = MutableLiveData<Response<List<AdminFeedbackOption>>>()
    val feedbackOptions: LiveData<Response<List<AdminFeedbackOption>>> get() = _feedbackOptions

    private val _deleteFeedbackStatus = MutableLiveData<Response<Unit>?>()
    val deleteFeedbackStatus: LiveData<Response<Unit>?> get() = _deleteFeedbackStatus

    fun loadFeedbackConfig() {
        _feedbackOptions.value = Response.Loading()
        viewModelScope.launch {
            val result = mainRepository.getRemoteFeedbacks()
            _feedbackOptions.postValue(result)
        }
    }

    fun deleteSelectedFeedbacks(docIds: List<String>) {
        _deleteFeedbackStatus.value = Response.Loading()
        viewModelScope.launch {
            val result = mainRepository.deleteRemoteFeedbacks(docIds)
            _deleteFeedbackStatus.postValue(result)
        }
    }

    fun resetDeleteFeedbackStatus() {
        _deleteFeedbackStatus.value = null
    }

    private val _questionStats = MutableLiveData<Response<Map<Int, Int>>>()
    val questionStats: LiveData<Response<Map<Int, Int>>> get() = _questionStats

    private val _maintenanceUpdateStatus = MutableLiveData<Response<Unit>?>()
    val maintenanceUpdateStatus: LiveData<Response<Unit>?> get() = _maintenanceUpdateStatus

    fun loadQuestionStats() {
        _questionStats.value = Response.Loading()
        viewModelScope.launch {
            val result = mainRepository.getRemoteQuestionStats()
            _questionStats.postValue(result)
        }
    }

    fun updateMaintenance(isMaintenance: Boolean, message: String) {
        _maintenanceUpdateStatus.value = Response.Loading()
        viewModelScope.launch {
            val result = mainRepository.updateRemoteMaintenanceInfo(isMaintenance, message)
            _maintenanceUpdateStatus.postValue(result)
        }
    }

    fun resetMaintenanceUpdateStatus() {
        _maintenanceUpdateStatus.value = null
    }

    private val _addQuestionStatus = MutableLiveData<Response<Unit>?>()
    val addQuestionStatus: LiveData<Response<Unit>?> get() = _addQuestionStatus

    private val _allPrizeAmounts = MutableLiveData<List<Int>>()
    val allPrizeAmounts: LiveData<List<Int>> get() = _allPrizeAmounts

    fun addQuestion(question: Question) {
        _addQuestionStatus.value = Response.Loading()
        viewModelScope.launch {
            val result = mainRepository.addRemoteQuestion(question)
            _addQuestionStatus.postValue(result)
        }
    }

    fun resetAddQuestionStatus() {
        _addQuestionStatus.value = null
    }

    fun loadAllPrizeAmounts() {
        viewModelScope.launch {
            val prizes = mainRepository.getUniquePrizeAmounts()
            _allPrizeAmounts.postValue(prizes)
        }
    }
}
