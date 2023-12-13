package com.pramoh.kbcqna.presentation

import android.os.Bundle
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
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionFragment : BaseFragment() {

    private lateinit var binding: FragmentQuestionBinding
    private val questionViewModel: QuestionViewModel by activityViewModels() // TODO: See if you can remove this, and get the currentPlayerName by something else
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
        setLifelines()
        setMusic()
    }

    private fun setObservers() {

        questionViewModel.currentQuestion.observe(viewLifecycleOwner) { question ->
            with(binding) {
                setTimer()
                tvPrizeAmount.text = MoneyTypeConversionUtil.convertToString(question.prizeAmount)
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
                    showResult(ResultType.TIMER_UP, it)
                }
            }
        }

        timerViewModel.timerValue.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it.toString()
        }

        questionViewModel.lifelines.observe(viewLifecycleOwner) {
            val lifelineMap = mapOf(
                0 to binding.ivLifeline1Cross,
                1 to binding.ivLifeline2Cross,
                2 to binding.ivLifeline3Cross,
                3 to binding.ivLifeline4Cross
            )
            for (i in it.indices) {
                lifelineMap[i]?.visibility = if (it[i]) View.INVISIBLE else View.VISIBLE
            }
        }
    }

    private fun setOnClickListeners() {

        with(binding) {

            tvOption1.setOnClickListenerWithSfxAudio { handleOptionClick(1) }
            tvOption2.setOnClickListenerWithSfxAudio { handleOptionClick(2) }
            tvOption3.setOnClickListenerWithSfxAudio { handleOptionClick(3) }
            tvOption4.setOnClickListenerWithSfxAudio { handleOptionClick(4) }

            ivLifeline1.setOnClickListenerWithSfxAudio { handleLifelineClick(Lifeline.AUDIENCE_POLL, false) }
            ivLifeline2.setOnClickListenerWithSfxAudio { handleLifelineClick(Lifeline.PHONE_A_FRIEND, false) }
            ivLifeline3.setOnClickListenerWithSfxAudio { handleLifelineClick(Lifeline.FIFTY_FIFTY, true) }
            ivLifeline4.setOnClickListenerWithSfxAudio { handleLifelineClick(Lifeline.SKIP_QUESTION, true) }

            tvQuit.setOnClickListenerWithSfxAudio {
                showDialog(
                    requireContext(),
                    "Do you want to Quit?\nYou will be getting ${questionViewModel.getMoneyWonTillNow()}",
                    "No",
                    "Yes",
                    positiveButtonAction = {
                        timerViewModel.cancelTimer()
                        disableAllButtonsClick()
                        questionViewModel.currentQuestion.value?.correctOptionNumber?.let {
                            showResult(ResultType.QUIT, it)
                        }
                    }
                )
            }

            btnLock.setOnClickListenerWithSfxAudio {
                timerViewModel.cancelTimer()
                disableAllButtonsClick()
                questionViewModel.currentQuestion.value?.let {
                    if (it.correctOptionNumber == questionViewModel.getCurrentSelectedOption()) {
                        showResult(ResultType.RIGHT_ANSWER)
                    } else {
                        showResult(ResultType.WRONG_ANSWER, it.correctOptionNumber)
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

    private fun handleLifelineClick(lifeline: Lifeline, positiveButtonNeeded: Boolean) {
        if (questionViewModel.lifelines.value?.get(lifeline.num-1) != false) {
            if (!positiveButtonNeeded) questionViewModel.onLifelineClick(lifeline.num)
            showDialog(
                requireContext(),
                getTextForLifelines(lifeline),
                if (positiveButtonNeeded) "No" else "Okay",
                if (positiveButtonNeeded) "Yes" else null,
                positiveButtonAction = { if (positiveButtonNeeded) onLifelinePositiveButtonClick(lifeline) }
            )
        }
    }

    private fun getTextForLifelines(lifeline: Lifeline): String {
        val correctOptionNumber = questionViewModel.currentQuestion.value?.correctOptionNumber ?: 0

        return when(lifeline) {
            Lifeline.AUDIENCE_POLL -> {
                val percentageList = MutableList(3) {(0..15).random() }
                val correctOptionPercentage = 100 - percentageList.sum()
                percentageList.add(correctOptionNumber-1, correctOptionPercentage)

                String.format(
                    "Option A: %d %%\nOption B: %d %%\nOption C: %d %%\nOption D: %d %%",
                    percentageList[0], percentageList[1], percentageList[2], percentageList[3]
                )
            }
            Lifeline.PHONE_A_FRIEND -> {
                val stringMap = mapOf(1 to "Option A", 2 to "Option B", 3 to "Option C", 4 to "Option D")
                "Your friend has suggested you the ${stringMap[correctOptionNumber]} with ${(61..85).random()}% probability."
            }
            Lifeline.SKIP_QUESTION -> "Do you want to skip the question ?"
            Lifeline.FIFTY_FIFTY -> "Do you want to use 50-50 ?"
        }
    }

    private fun onLifelinePositiveButtonClick(lifeline: Lifeline) {
        questionViewModel.onLifelineClick(lifeline.num)
        if (lifeline == Lifeline.FIFTY_FIFTY) {
            val viewsMap = mapOf(0 to binding.tvOption1, 1 to binding.tvOption2, 2 to binding.tvOption3, 3 to binding.tvOption4)
            val correctOptionNumber = questionViewModel.currentQuestion.value?.correctOptionNumber ?: 0

            val optionVisibility = MutableList(3) {false}
            optionVisibility[(0.. 2).random()] = true
            optionVisibility.add(correctOptionNumber-1, true)

            optionVisibility.forEachIndexed { index, isVisible ->
                if (!isVisible) {
                    viewsMap[index]?.apply {
                        text = ""
                        isClickable = false
                    }
                }
            }
        } else if (lifeline == Lifeline.SKIP_QUESTION) {
            timerViewModel.cancelTimer()
            disableAllButtonsClick()
            questionViewModel.currentQuestion.value?.correctOptionNumber?.let {
                showResult(ResultType.SKIP_QUESTION, it)
            }
        }
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
        if (args.questionToBeAsked >= 10) {
            binding.tvTimer.hide()
        } else {
            binding.tvTimer.show()
            timerViewModel.startTimer(15)
        }
    }

    private fun setQuestion() {
        if (args.questionToBeAsked !in 1..15) {
            Toast.makeText(context, "Question Number out of bounds", Toast.LENGTH_SHORT).show()
        } else {
            questionViewModel.setCurrentQuestion(args.questionToBeAsked)
        }
    }

    private fun setLifelines() {
        if (args.questionToBeAsked == 1) {
            questionViewModel.setLifelines()
        }
    }

    private fun setMusic() {
        playMusic(MusicToPlay.QUESTIONNAIRE, args.questionToBeAsked < 10)
    }

    private fun showResult(result: ResultType, correctOptionNumber: Int = 0) {
        stopMusic()
        when (result) {

            ResultType.RIGHT_ANSWER -> {
                changeOptionColors(questionViewModel.getCurrentSelectedOption() to R.drawable.background_metallic_green)
                playMusic(MusicToPlay.CORRECT_ANSWER)
                val destination = if (args.questionToBeAsked > 14) {
                    QuestionFragmentDirections.actionQuestionFragmentToResultFragment(true, 100000000)
                } else {
                    QuestionFragmentDirections.actionQuestionFragmentToPrizeListFragment(args.questionToBeAsked + 1)
                }
                navigateWithDelay(destination)
            }

            ResultType.WRONG_ANSWER -> {
                changeOptionColors(
                    questionViewModel.getCurrentSelectedOption() to R.drawable.background_metallic_red,
                    correctOptionNumber to R.drawable.background_metallic_green
                )
                playMusic(MusicToPlay.WRONG_ANSWER)
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(false, questionViewModel.getLastSafeZone())
                navigateWithDelay(destination, 7000)
            }

            ResultType.TIMER_UP -> {
                changeOptionColors(correctOptionNumber to R.drawable.background_metallic_green)
                playMusic(MusicToPlay.WRONG_ANSWER)
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(false, questionViewModel.getLastSafeZone())
                navigateWithDelay(destination)
            }

            ResultType.QUIT -> {
                changeOptionColors(correctOptionNumber to R.drawable.background_metallic_green)
                playMusic(MusicToPlay.WRONG_ANSWER)
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(false, questionViewModel.getMoneyWonTillNow())
                navigateWithDelay(destination)
            }

            ResultType.SKIP_QUESTION -> {
                changeOptionColors(correctOptionNumber to R.drawable.background_metallic_green)
                playMusic(MusicToPlay.CORRECT_ANSWER)
                val destination = QuestionFragmentDirections.actionQuestionFragmentToPrizeListFragment(args.questionToBeAsked + 1)
                navigateWithDelay(destination)
            }
        }

    }

    private fun navigateWithDelay(destination: NavDirections, delayDuration: Long = 5000) {

        CoroutineScope(Dispatchers.Main).launch {
            delay(delayDuration)
            stopMusic()
            findNavController().navigate(destination)
        }
    }

    enum class ResultType {
        TIMER_UP, RIGHT_ANSWER, WRONG_ANSWER, QUIT, SKIP_QUESTION
    }

    enum class Lifeline(val num: Int) {
        AUDIENCE_POLL(1), PHONE_A_FRIEND(2), FIFTY_FIFTY(3), SKIP_QUESTION(4)
    }

}