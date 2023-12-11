package com.pramoh.kbcqna.presentation

import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {

    private var onStartClicked: Boolean = false // TODO: Check if this can be removed

    fun setOnStartClicked(value: Boolean) {
        onStartClicked = value
    }

    fun getOnStartClicked(): Boolean {
        return onStartClicked
    }
}