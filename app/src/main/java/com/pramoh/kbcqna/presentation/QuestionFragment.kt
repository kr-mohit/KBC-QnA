package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentQuestionBinding


class QuestionFragment : BaseFragment() {

    private lateinit var binding: FragmentQuestionBinding
    private val questionViewModel: QuestionViewModel by viewModels()
    private val args: PrizeListFragmentArgs by navArgs()
    private lateinit var timer: CountDownTimer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setOnClickListeners()
        setUI()
    }

    private fun setUI() {
        setQuestion()
        setTimer()
        setMusic()
    }

    private fun setObservers() {

        questionViewModel.isLockButtonClickable.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnLock.setBackgroundColor(requireContext().getColor(R.color.metallic_green))
                binding.btnLock.isClickable = true
            } else {
                binding.btnLock.setBackgroundColor(requireContext().getColor(R.color.metallic_grey))
                binding.btnLock.isClickable = false
            }
        }

        questionViewModel.currentQuestion.observe(viewLifecycleOwner) {
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
            questionViewModel.onLifelineClick()
            // TODO: show lifeline pop up
            showComingSoonToast()
        }

        binding.ivLifeline2.setOnClickListener {
            questionViewModel.onLifelineClick()
            // TODO: show lifeline pop up
            showComingSoonToast()
        }

        binding.ivLifeline3.setOnClickListener {
            questionViewModel.onLifelineClick()
            // TODO: show lifeline pop up
            showComingSoonToast()
        }

        binding.ivLifeline4.setOnClickListener {
            questionViewModel.onLifelineClick()
            // TODO: show lifeline pop up
            showComingSoonToast()
        }

        binding.tvQuit.setOnClickListener {
            findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                false,
                questionViewModel.moneyWonTillNow.value ?: "Error"))
            // TODO: show quitting pop up
        }

        binding.tvOption1.setOnClickListener {
            changeOptionColors(1, R.drawable.background_metallic_gold)
            questionViewModel.onOptionClick(1)
        }

        binding.tvOption2.setOnClickListener {
            changeOptionColors(2, R.drawable.background_metallic_gold)
            questionViewModel.onOptionClick(2)
        }

        binding.tvOption3.setOnClickListener {
            changeOptionColors(3, R.drawable.background_metallic_gold)
            questionViewModel.onOptionClick(3)
        }

        binding.tvOption4.setOnClickListener {
            changeOptionColors(4, R.drawable.background_metallic_gold)
            questionViewModel.onOptionClick(4)
        }

        binding.btnLock.setOnClickListener {
            timer.cancel()
            questionViewModel.currentQuestion.value?.let {
                if (it.correctOptionNumber == questionViewModel.currentSelectedOption) {

                    changeOptionColors(questionViewModel.currentSelectedOption, R.drawable.background_metallic_green)
                    // TODO: play right answer music or delay

                    if (args.questionToBeAsked > 14) {
                        findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToResultFragment(true, "Rs. 10 Crores"))
                        // TODO: get the prize money through list (or leave as it is)
                    } else {
                        findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToPrizeListFragment(args.questionToBeAsked+1))
                    }

                } else {
                    changeOptionColors(questionViewModel.currentSelectedOption, R.drawable.background_metallic_red)
                    findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToResultFragment(false, "Rs. 0"))
                    // TODO: play wrong answer music or delay
                    // TODO: get the prize money through list
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
        if (args.questionToBeAsked >= 8) {
            binding.tvTimer.visibility = View.GONE
        } else {
            binding.tvTimer.visibility = View.VISIBLE
            timer = object: CountDownTimer(15000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.tvTimer.text = (millisUntilFinished / 1000).toString()
                }

                override fun onFinish() {
                    timer.cancel()
                    findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToResultFragment(false, "Rs. 0"))
                    // TODO: show time up dialog
                }
            }.start()
        }
    }

    private fun setQuestion() {
        questionViewModel.setCurrentQuestion(args.questionToBeAsked)
    }

    private fun setMusic() {
        // TODO: set questionnaire music
    }
}