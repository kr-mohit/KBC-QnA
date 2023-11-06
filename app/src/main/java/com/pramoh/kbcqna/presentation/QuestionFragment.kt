package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentQuestionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionFragment : BaseFragment() {

    private lateinit var binding: FragmentQuestionBinding
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private val timerViewModel: TimerViewModel by viewModels()
    private val args: PrizeListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()
        setOnClickListeners()
        setQuestion()
        setAudio()
    }

    private fun setObservers() {

        questionViewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            with(binding) {
                setTimer()
                tvPrizeAmount.text = question.prizeAmount
                tvQuestion.text = question.question
                tvOption1.text = question.option1
                tvOption2.text = question.option2
                tvOption3.text = question.option3
                tvOption4.text = question.option4
            }
        }

        timerViewModel.didTimerEnd.observe(viewLifecycleOwner) { didTimeEnd ->
            if (didTimeEnd) {
                questionViewModel.currentQuestion.value?.correctOptionNumber?.let {
                    disableAllButtonsClick()
                    showResult(TIMER_UP, it)
                }
            }
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
                showDialog(
                    requireContext(),
                    "Do you want to Quit?\nYou will be getting ${questionViewModel.getMoneyWonTillNow()}",
                    "No",
                    "Yes",
                    positiveButtonAction = {
                        timerViewModel.cancelTimer()
                        disableAllButtonsClick()
                        questionViewModel.currentQuestion.value?.correctOptionNumber?.let {
                            showResult(QUIT, it)
                        }
                    }
                )
            }

            btnLock.setOnClickListener {
                timerViewModel.cancelTimer()
                disableAllButtonsClick()
                questionViewModel.currentQuestion.value?.let {
                    if (it.correctOptionNumber == questionViewModel.getCurrentSelectedOption()) {
                        showResult(RIGHT_ANSWER)
                    } else {
                        showResult(WRONG_ANSWER, it.correctOptionNumber)
                    }
                }
            }
        }
    }

    private fun handleOptionClick(option: Int) {
        changeOptionColors(option to R.drawable.background_metallic_gold)
        binding.btnLock.setBackgroundColor(requireContext().getColor(R.color.metallic_green))
        binding.btnLock.isEnabled = true
        questionViewModel.setCurrentSelectedOption(option)
    }

    private fun handleLifelineClick(lifeline: Int) {
        questionViewModel.onLifelineClick()
        showDialog(
            requireContext(),
            "Coming Soon\nStay Tuned...",
            "Okay"
        )
    }

    private fun disableAllButtonsClick() {
        with(binding) {
            val allButtonList = listOf(
                ivLifeline1,
                ivLifeline2,
                ivLifeline3,
                ivLifeline4,
                tvQuit,
                tvOption1,
                tvOption2,
                tvOption3,
                tvOption4,
                btnLock
            )
            allButtonList.forEach { it.isClickable = false }
        }
    }

    private fun changeOptionColors(vararg options: Pair<Int, Int>) {
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
        if (args.questionToBeAsked < 1 || args.questionToBeAsked > 15) {
            Toast.makeText(context, "Question Number out of bounds", Toast.LENGTH_SHORT).show()
        } else {
            questionViewModel.setCurrentQuestion(args.questionToBeAsked)
        }
    }

    private fun showResult(result: String, correctOptionNumber: Int = 0) {

        stopExoPlayer()

        when (result) {

            RIGHT_ANSWER -> {
                changeOptionColors(questionViewModel.getCurrentSelectedOption() to R.drawable.background_metallic_green)
                playAudio(R.raw.audio_correct_answer)
                val destination = if (args.questionToBeAsked > 14) {
                    QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                        true,
                        "Rs. 10 Crore"
                    )
                } else {
                    QuestionFragmentDirections.actionQuestionFragmentToPrizeListFragment(args.questionToBeAsked + 1)
                }
                navigateWithDelay(destination)
            }

            WRONG_ANSWER -> {
                changeOptionColors(
                    questionViewModel.getCurrentSelectedOption() to R.drawable.background_metallic_red,
                    correctOptionNumber to R.drawable.background_metallic_green
                )
                playAudio(R.raw.audio_wrong_answer)
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                    false,
                    questionViewModel.getLastSafeZone()
                )
                navigateWithDelay(destination, 7000)
            }

            TIMER_UP -> {
                changeOptionColors(correctOptionNumber to R.drawable.background_metallic_green)
                playAudio(R.raw.audio_wrong_answer)
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                    false,
                    questionViewModel.getLastSafeZone()
                )
                navigateWithDelay(destination)
                playAudio(R.raw.audio_wrong_answer)
            }

            QUIT -> {
                changeOptionColors(correctOptionNumber to R.drawable.background_metallic_green)
                playAudio(R.raw.audio_wrong_answer)
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                    false,
                    questionViewModel.getMoneyWonTillNow()
                )
                navigateWithDelay(destination)
            }
        }

    }

    private fun navigateWithDelay(destination: NavDirections, delayDuration: Long = 5000) {
        Handler().postDelayed({
            stopExoPlayer()
            findNavController().navigate(destination)
        }, delayDuration)
    }

    private fun setAudio() {
        playAudio(R.raw.audio_questionnaire)
        setAudioTransitionFromQuestionnaireToTicktock(args.questionToBeAsked)
    }

    companion object {
        private const val TIMER_UP = "TIMER_UP"
        private const val RIGHT_ANSWER = "RIGHT_ANSWER"
        private const val WRONG_ANSWER = "WRONG_ANSWER"
        private const val QUIT = "QUIT"
    }

}