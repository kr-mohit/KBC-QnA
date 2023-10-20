package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentQuestionBinding
import com.pramoh.kbcqna.utils.Constants


class QuestionFragment : BaseFragment() {

    private lateinit var binding: FragmentQuestionBinding
    private val questionViewModel: QuestionViewModel by viewModels()
    private val timerViewModel: TimerViewModel by viewModels()
    private val args: PrizeListFragmentArgs by navArgs()

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

        timerViewModel.didTimerEnd.observe(viewLifecycleOwner) {
            showCorrectOption(Constants.TIMER_UP)
        }

        timerViewModel.timerValue.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it.toString()
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
            PopUpWindowFragment(
                "Do you want to Quit?",
                "Yes",
                "No",
                questionViewModel.moneyWonTillNow
            ).show(childFragmentManager, null)
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
            timerViewModel.cancelTimer()
            questionViewModel.currentQuestion.value?.let {
                if (it.correctOptionNumber == questionViewModel.currentSelectedOption) {
                    showCorrectOption(Constants.RIGHT_ANSWER)

                } else {
                    showCorrectOption(Constants.WRONG_ANSWER)
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
            timerViewModel.startTimer(15)
        }
    }

    private fun setQuestion() {
        questionViewModel.setCurrentQuestion(args.questionToBeAsked)
    }

    private fun showCorrectOption(id: String) {

        when (id) {

            Constants.RIGHT_ANSWER -> {
                changeOptionColors(questionViewModel.currentSelectedOption, R.drawable.background_metallic_green)
                // TODO: play right answer music or delay
                Handler().postDelayed(
                    {
                    if (args.questionToBeAsked > 14) {
                        findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                            true, "Rs. 10 Crore"))
                    } else {
                        findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToPrizeListFragment(args.questionToBeAsked + 1))
                    }
                    },
                    5000
                )
            }

            Constants.WRONG_ANSWER -> {
                changeOptionColors(questionViewModel.currentSelectedOption, R.drawable.background_metallic_red)
                findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                    false, questionViewModel.lastSafeZone))
                // TODO: play wrong answer music or delay
                // TODO: get the last safe zone and money accordingly
            }

            Constants.TIMER_UP -> {
                Handler().postDelayed(
                    {
                        findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                            false, questionViewModel.lastSafeZone))
                    }, 5000
                )
            }

            Constants.QUIT -> {
                Handler().postDelayed(
                    {
                        findNavController().navigate(QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                            false, questionViewModel.moneyWonTillNow))
                    }, 5000
                )
            }
        }

    }

    private fun setMusic() {
        // TODO: set questionnaire music
    }
}