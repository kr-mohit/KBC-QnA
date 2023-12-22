package com.pramoh.kbcqna.presentation.prizelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil

class PrizeListViewModel : ViewModel() {

    private val _listOfPrizes = MutableLiveData<List<String>>()
    val listOfPrizes: LiveData<List<String>>
        get() = _listOfPrizes

    private val list = mutableListOf<String>()

    init {
        val moneyList = listOf(
            100000000,
            30000000,
            10000000,
            5000000,
            2500000,
            1250000,
            640000,
            320000,
            160000,
            80000,
            40000,
            20000,
            10000,
            5000,
            1000
        ) // TODO: Get this from API, or something else; can't set like this
        moneyList.forEach { list.add(MoneyTypeConversionUtil.convertToString(it)) }
        _listOfPrizes.postValue(list)
    }
}