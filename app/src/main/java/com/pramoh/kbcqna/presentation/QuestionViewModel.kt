package com.pramoh.kbcqna.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pramoh.kbcqna.domain.model.Question

class QuestionViewModel:ViewModel() {

    private val listOfQuestions = mutableListOf<Question>() // TODO: get this from db or api
    lateinit var moneyWonTillNow: String
    lateinit var lastSafeZone: String
    var currentSelectedOption: Int = 0

    private val _isLockButtonClickable = MutableLiveData<Boolean>()
    val isLockButtonClickable: LiveData<Boolean>
        get() = _isLockButtonClickable

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question>
        get() = _currentQuestion

    init {

        _isLockButtonClickable.postValue(false)

        listOfQuestions.add(Question("What is the capital of India?", "Mumbai", "Chennai", "Delhi", "Kolkata", 3, "Rs. 1,000", "INDIA"))
        listOfQuestions.add(Question("Which of the following corresponds to ‘one by two’?", "Pura", "Sawa", "Adha", "Pauna", 3, "Rs. 5,000", "INDIA"))
        listOfQuestions.add(Question("Which of the following gods is also known as ‘Gauri Nandan’?", "Agni", "Indra", "Hanuman", "Ganesha", 4, "Rs. 10,000", "INDIA"))
        listOfQuestions.add(Question("In the game of ludo the discs or tokens are of how many colours?", "One", "Two", "Three", "Four", 4, "Rs. 20,000", "INDIA"))
        listOfQuestions.add(Question("Which of these terms can only be used for women?", "Dirghaayu", "Suhagan", "Chiranjeevi", "Sushil", 2, "Rs. 40,000", "INDIA"))
        listOfQuestions.add(Question("Which of these sports requires you to shout out a word loudly during play?", "Ludo", "Kho-kho", "Playing cards", "Chess", 2, "Rs. 80,000", "INDIA"))
        listOfQuestions.add(Question("Which of these spices is the smallest in size?", "Ajwain", "Jeera", "Saunf", "Methi Seeds", 1, "Rs. 1,60,000", "INDIA"))
        listOfQuestions.add(Question("What kind of creature was Bakasur, whom Kansa sent to kill Sri Krishna in his childhood?", "Bird", "Snake", "Lizard", "Deer", 1, "Rs. 3,20,000", "INDIA"))
        listOfQuestions.add(Question("Who wrote the introduction to the English translation of Rabindranath Tagore’s Gitanjali?", "P.B. Shelley", "Alfred Tennyson", "W.B. Yeats", "T.S. Elliot", 3, "Rs. 6,40,000", "INDIA"))
        listOfQuestions.add(Question("Which of these are names of national parks located in Madhya Pradesh?", "Krishna and Kanhaiya", "Krishna and Kanhaiya", "Ghanshyam and Murari", "Girdhar and Gopal", 2, "Rs. 12,50,000", "INDIA"))
        listOfQuestions.add(Question("The wife of which of these famous sports persons was once captain of Indian volleyball team?", "K.D.Jadav", "Dhyan Chand", "Dhyan Chand", "Milkha Singh", 4, "Rs. 25,00,000", "INDIA"))
        listOfQuestions.add(Question("Who are the only married couple elected to the 16th Lok Sabha?", "Sukhbir Singh-Harshimrat Kaur Badal", "Pappu Yadav-Ranjeet Ranjan", "Priya Ranjan-Deepa Dasmunshi", "Prakash-Brinda Karat", 2, "Rs. 50,00,000", "INDIA"))
        listOfQuestions.add(Question("Among whom of the following does the Indian Constitution permit to take part in the proceedings of Parliament?", "Solicitor General ", "Attorney General ", "Cabinet Secretary", "Cabinet Chief Justice", 2, "Rs. 1 Crore", "INDIA"))
        listOfQuestions.add(Question("The historic Indo-Pak talks of 1972 between Indira Gandhi and Zulfikar Ali Bhutto were held at which place in Shimla?", "Viceregal Lodge", "Gorton Castle", "Barnes Court", "Cecil Hotel", 3, "Rs. 3 Crore", "INDIA"))
        listOfQuestions.add(Question("Which colonial power ended its involvement in India by selling the rights of the Nicobar Islands to the British on October 18, 1868?", "Belgium", "Italy", "Denmark", "France", 3, "Rs. 10 Crore", "INDIA"))
    }

    fun setCurrentQuestion(quesNumber: Int) {
        // TODO: handle if ques number is less than 1
        _currentQuestion.postValue(listOfQuestions[quesNumber-1])

        moneyWonTillNow = if (quesNumber < 2) {
            "Rs. 0"
        } else {
            listOfQuestions[quesNumber-2].prizeAmount // TODO: do something about this, can't set here
        }

        lastSafeZone = if (quesNumber >= 10) {
            "Rs. 6, 40, 000"
        } else if (quesNumber >= 7) {
            "Rs. 80, 000"
        } else {
            "Rs. 0"
        }
    }

    fun onLifelineClick() {
        // TODO: logic for lifeline click
    }

    fun onOptionClick(option: Int) {
        _isLockButtonClickable.postValue(true)
        currentSelectedOption = option
    }
}