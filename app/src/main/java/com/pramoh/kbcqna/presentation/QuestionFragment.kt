package com.pramoh.kbcqna.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentQuestionBinding


class QuestionFragment : BaseFragment() {

    private lateinit var binding: FragmentQuestionBinding
    private lateinit var viewModel: QuestionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[QuestionViewModel::class.java]

        setObservers()
        setOnClickListeners()
        setTimer()
        setQuestionnaireMusic()
    }

    private fun setObservers() {
        viewModel.isLockButtonClickable.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnLock.setBackgroundColor(Color.GREEN)
                binding.btnLock.isClickable = true
            } else {
                binding.btnLock.setBackgroundColor(Color.DKGRAY)
                binding.btnLock.isClickable = false
            }
        }

        viewModel.currentQuestion.observe(viewLifecycleOwner) {
            binding.tvPrizeAmount.text = it.prizeAmount
            binding.tvQuestion.text = it.question
            binding.tvOption1.text = it.option1
            binding.tvOption2.text = it.option2
            binding.tvOption3.text = it.option3
            binding.tvOption4.text = it.option4
        }
    }

    private fun setOnClickListeners() {

        binding.ivLifeline1.setOnClickListener {
            viewModel.onLifelineClick(1)
            // TODO: show lifeline pop up
        }

        binding.ivLifeline2.setOnClickListener {
            viewModel.onLifelineClick(2)
            // TODO: show lifeline pop up
        }

        binding.ivLifeline3.setOnClickListener {
            viewModel.onLifelineClick(3)
            // TODO: show lifeline pop up
        }

        binding.ivLifeline4.setOnClickListener {
            viewModel.onLifelineClick(4)
            // TODO: show lifeline pop up
        }

        binding.ivQuit.setOnClickListener {
            onBackPressed() //remove this
            // TODO: show quitting pop up
        }

        binding.tvOption1.setOnClickListener {
            changeOptionColors(1, R.drawable.selected_option_background)
            viewModel.onOptionClick(1)
        }

        binding.tvOption2.setOnClickListener {
            changeOptionColors(2, R.drawable.selected_option_background)
            viewModel.onOptionClick(2)
        }

        binding.tvOption3.setOnClickListener {
            changeOptionColors(3, R.drawable.selected_option_background)
            viewModel.onOptionClick(3)
        }

        binding.tvOption4.setOnClickListener {
            changeOptionColors(4, R.drawable.selected_option_background)
            viewModel.onOptionClick(4)
        }

        binding.btnLock.setOnClickListener {
            viewModel.currentQuestion.value?.let {
                if (it.correctOptionNumber == viewModel.currentSelectedOption) {
                    changeOptionColors(viewModel.currentSelectedOption, R.drawable.correct_option_background)
                    // TODO: play right answer music or delay
                    gotoFragment(PrizeListFragment())
                } else {
                    changeOptionColors(viewModel.currentSelectedOption, R.drawable.wrong_option_background)
                    // TODO: play wrong answer music or delay
                    gotoFragment(ResultFragment())
                }
            }
        }
    }

    private fun changeOptionColors(option: Int, resId: Int) {
        when (option) {
            1 -> {
                binding.tvOption1.setBackgroundResource(resId)
                binding.tvOption2.setBackgroundResource(R.drawable.question_background)
                binding.tvOption3.setBackgroundResource(R.drawable.question_background)
                binding.tvOption4.setBackgroundResource(R.drawable.question_background)
            }
            2 -> {
                binding.tvOption2.setBackgroundResource(resId)
                binding.tvOption1.setBackgroundResource(R.drawable.question_background)
                binding.tvOption3.setBackgroundResource(R.drawable.question_background)
                binding.tvOption4.setBackgroundResource(R.drawable.question_background)
            }
            3 -> {
                binding.tvOption3.setBackgroundResource(resId)
                binding.tvOption2.setBackgroundResource(R.drawable.question_background)
                binding.tvOption1.setBackgroundResource(R.drawable.question_background)
                binding.tvOption4.setBackgroundResource(R.drawable.question_background)
            }
            4 -> {
                binding.tvOption4.setBackgroundResource(resId)
                binding.tvOption2.setBackgroundResource(R.drawable.question_background)
                binding.tvOption3.setBackgroundResource(R.drawable.question_background)
                binding.tvOption1.setBackgroundResource(R.drawable.question_background)
            }
        }
    }

    private fun setTimer() {
        // TODO: set timer
    }

    private fun setQuestionnaireMusic() {
        // TODO: set questionnaire music
    }
}