package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PrizeListViewModel: ViewModel() {

    private val _listOfPrizes = MutableLiveData<List<String>>()
    val listOfPrizes: LiveData<List<String>>
        get() = _listOfPrizes

    private val list = mutableListOf<String>()

    init {
        list.add("Rs. 10,00,00,000")
        list.add("Rs. 3,00,00,000")
        list.add("Rs. 1,00,00,000")
        list.add("Rs. 50,00,000")
        list.add("Rs. 25,00,000")
        list.add("Rs. 12,50,000")
        list.add("Rs. 6,40,000")
        list.add("Rs. 3,20,000")
        list.add("Rs. 1,60,000")
        list.add("Rs. 80,000")
        list.add("Rs. 40,000")
        list.add("Rs. 20,000")
        list.add("Rs. 10,000")
        list.add("Rs. 5,000")
        list.add("Rs. 1,000")

        _listOfPrizes.postValue(list)
    }

    val currentQuestion = 4

}