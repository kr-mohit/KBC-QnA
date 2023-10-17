package com.pramoh.kbcqna.presentation

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
                binding.btnLock.setBackgroundColor(requireContext().getColor(R.color.metallic_green))
                binding.btnLock.isClickable = true
            } else {
                binding.btnLock.setBackgroundColor(requireContext().getColor(R.color.metallic_grey))
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
            viewModel.onLifelineClick()
            // TODO: show lifeline pop up
            displayComingSoonToast()
        }

        binding.ivLifeline2.setOnClickListener {
            viewModel.onLifelineClick()
            // TODO: show lifeline pop up
            displayComingSoonToast()
        }

        binding.ivLifeline3.setOnClickListener {
            viewModel.onLifelineClick()
            // TODO: show lifeline pop up
            displayComingSoonToast()
        }

        binding.ivLifeline4.setOnClickListener {
            viewModel.onLifelineClick()
            // TODO: show lifeline pop up
            displayComingSoonToast()
        }

        binding.tvQuit.setOnClickListener {
            onBackPressed() //remove this
            // TODO: show quitting pop up
        }

        binding.tvOption1.setOnClickListener {
            changeOptionColors(1, R.drawable.background_metallic_gold)
            viewModel.onOptionClick(1)
        }

        binding.tvOption2.setOnClickListener {
            changeOptionColors(2, R.drawable.background_metallic_gold)
            viewModel.onOptionClick(2)
        }

        binding.tvOption3.setOnClickListener {
            changeOptionColors(3, R.drawable.background_metallic_gold)
            viewModel.onOptionClick(3)
        }

        binding.tvOption4.setOnClickListener {
            changeOptionColors(4, R.drawable.background_metallic_gold)
            viewModel.onOptionClick(4)
        }

        binding.btnLock.setOnClickListener {
            viewModel.currentQuestion.value?.let {
                if (it.correctOptionNumber == viewModel.currentSelectedOption) {
                    changeOptionColors(viewModel.currentSelectedOption, R.drawable.background_metallic_green)
                    // TODO: play right answer music or delay
                    gotoFragment(PrizeListFragment())
                } else {
                    changeOptionColors(viewModel.currentSelectedOption, R.drawable.background_metallic_red)
                    // TODO: play wrong answer music or delay
                    gotoFragment(ResultFragment())
                }
            }
        }
    }

    private fun changeOptionColors(option: Int, resId: Int, defaultResId: Int = R.drawable.background_metallic_blue) {
        when (option) {
            1 -> {
                binding.tvOption1.setBackgroundResource(resId)
                binding.tvOption2.setBackgroundResource(defaultResId)
                binding.tvOption3.setBackgroundResource(defaultResId)
                binding.tvOption4.setBackgroundResource(defaultResId)
            }
            2 -> {
                binding.tvOption2.setBackgroundResource(resId)
                binding.tvOption1.setBackgroundResource(defaultResId)
                binding.tvOption3.setBackgroundResource(defaultResId)
                binding.tvOption4.setBackgroundResource(defaultResId)
            }
            3 -> {
                binding.tvOption3.setBackgroundResource(resId)
                binding.tvOption2.setBackgroundResource(defaultResId)
                binding.tvOption1.setBackgroundResource(defaultResId)
                binding.tvOption4.setBackgroundResource(defaultResId)
            }
            4 -> {
                binding.tvOption4.setBackgroundResource(resId)
                binding.tvOption2.setBackgroundResource(defaultResId)
                binding.tvOption3.setBackgroundResource(defaultResId)
                binding.tvOption1.setBackgroundResource(defaultResId)
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