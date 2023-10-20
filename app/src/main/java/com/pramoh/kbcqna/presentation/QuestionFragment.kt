package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentQuestionBinding


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
    }

    private fun setObservers() {

        questionViewModel.isLockButtonClickable.observe(viewLifecycleOwner) {
            val backgroundColor = if (it) R.color.metallic_green else R.color.metallic_grey
            binding.btnLock.setBackgroundColor(requireContext().getColor(backgroundColor))
            binding.btnLock.isClickable = it
        }

        questionViewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            with(binding) {
                tvPrizeAmount.text = question.prizeAmount
                tvQuestion.text = question.question
                tvOption1.text = question.option1
                tvOption2.text = question.option2
                tvOption3.text = question.option3
                tvOption4.text = question.option4
            }
        }

        timerViewModel.didTimerEnd.observe(viewLifecycleOwner) {
            showResult(TIMER_UP)
        }

        timerViewModel.timerValue.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it.toString()
        }
    }

    private fun setOnClickListeners() {

        with(binding) {

            tvOption1.setOnClickListener { handleOptionClick(1) }
            tvOption2.setOnClickListener { handleOptionClick(2) }
            tvOption3.setOnClickListener { handleOptionClick(3) }
            tvOption4.setOnClickListener { handleOptionClick(4) }

            ivLifeline1.setOnClickListener { handleLifelineClick(1) }
            ivLifeline2.setOnClickListener { handleLifelineClick(2) }
            ivLifeline3.setOnClickListener { handleLifelineClick(3) }
            ivLifeline4.setOnClickListener { handleLifelineClick(4) }

            tvQuit.setOnClickListener {
                PopUpWindowFragment(
                    "Do you want to Quit?",
                    "Yes",
                    "No",
                    questionViewModel.moneyWonTillNow
                ).show(childFragmentManager, null)
            }

            btnLock.setOnClickListener {
                timerViewModel.cancelTimer()
                questionViewModel.currentQuestion.value?.let {
                    if (it.correctOptionNumber == questionViewModel.currentSelectedOption) {
                        showResult(RIGHT_ANSWER)

                    } else {
                        showResult(WRONG_ANSWER, it.correctOptionNumber)
                    }
                }
            }
        }
    }

    private fun handleOptionClick(option: Int) {
        changeOptionColors2(
            option to R.drawable.background_metallic_gold
        )
        questionViewModel.onOptionClick(option)
    }

    private fun handleLifelineClick(lifeline: Int) {
        questionViewModel.onLifelineClick()
        showComingSoonToast()
        // TODO: show lifeline pop up
    }

    private fun changeOptionColors2(vararg options: Pair<Int, Int>) {
        with(binding) {
            val defaultResId = R.drawable.background_metallic_blue

            val optionViews = mapOf(
                1 to tvOption1,
                2 to tvOption2,
                3 to tvOption3,
                4 to tvOption4
            )

            optionViews.values.forEach { it.setBackgroundResource(defaultResId) }

            options.forEach { (optionNumber, resId) ->
                optionViews[optionNumber]?.setBackgroundResource(resId)
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

    private fun showResult(result: String, correctOptionNumber: Int = 0) {

        when (result) {

            RIGHT_ANSWER -> {
                changeOptionColors2(
                    questionViewModel.currentSelectedOption to R.drawable.background_metallic_green
                )
                val destination = if (args.questionToBeAsked > 14) {
                    QuestionFragmentDirections.actionQuestionFragmentToResultFragment(true, "Rs. 10 Crore")
                } else {
                    QuestionFragmentDirections.actionQuestionFragmentToPrizeListFragment(args.questionToBeAsked + 1)
                }
                navigateWithDelay(destination)
            }

            WRONG_ANSWER -> {
                changeOptionColors2(
                    questionViewModel.currentSelectedOption to R.drawable.background_metallic_red,
                    correctOptionNumber to R.drawable.background_metallic_green
                )
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(false, questionViewModel.lastSafeZone)
                navigateWithDelay(destination, 7000)
            }

            TIMER_UP -> {
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(false, questionViewModel.lastSafeZone)
                navigateWithDelay(destination)
            }

            QUIT -> {
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(false, questionViewModel.moneyWonTillNow)
                navigateWithDelay(destination)
            }
        }

    }

    private fun navigateWithDelay(destination: NavDirections, delayDuration: Long = 5000) {
        Handler().postDelayed({
            findNavController().navigate(destination)
        }, delayDuration)
    }

    companion object {
        private const val TIMER_UP = "TIMER_UP"
        private const val RIGHT_ANSWER = "RIGHT_ANSWER"
        private const val WRONG_ANSWER = "WRONG_ANSWER"
        private const val QUIT = "QUIT"
    }

}