package com.pramoh.kbcqna.presentation.questionnaire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentQuestionBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import androidx.core.graphics.drawable.toDrawable
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class QuestionFragment : BaseFragment() {

    private lateinit var binding: FragmentQuestionBinding
    private val questionViewModel: QuestionViewModel by activityViewModels() // TODO: See if you can remove this, and get the currentPlayerName by something else
    private val timerViewModel: TimerViewModel by viewModels()
    private val args: QuestionFragmentArgs by navArgs()

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

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showQuitDialog()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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

                val optionViews = listOf(tvOption1, tvOption2, tvOption3, tvOption4)
                optionViews.forEach { view ->
                    view.alpha = 1f
                    view.isClickable = true
                }

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
                showQuitDialog()
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

    private fun showQuitDialog() {
        showDialog(
            requireContext(),
            "Do you want to Quit?\nYou will be getting ${
                MoneyTypeConversionUtil.convertToString(
                    questionViewModel.getMoneyWonTillNow()
                )
            }",
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

    private fun handleOptionClick(option: Int) {
        changeOptionColors(option to R.drawable.background_metallic_gold)
        binding.btnLock.setBackgroundColor(requireContext().getColor(R.color.metallic_green))
        binding.btnLock.isEnabled = true
        questionViewModel.setCurrentSelectedOption(option)
    }

    private fun handleLifelineClick(lifeline: Lifeline, positiveButtonNeeded: Boolean) {
        if (questionViewModel.lifelines.value?.get(lifeline.num - 1) != false) {
            val remainingSeconds = timerViewModel.timerValue.value ?: 15L

            if (lifeline == Lifeline.AUDIENCE_POLL) {
                timerViewModel.cancelTimer()
                questionViewModel.onLifelineClick(lifeline.num)
                showAudiencePollDialog(remainingSeconds)
                return
            }
            if (lifeline == Lifeline.PHONE_A_FRIEND) {
                timerViewModel.cancelTimer()
                showPhoneAFriendSelectionDialog(remainingSeconds)
                return
            }

            timerViewModel.cancelTimer()
            if (!positiveButtonNeeded) questionViewModel.onLifelineClick(lifeline.num)
            showDialog(
                requireContext(),
                getTextForLifelines(lifeline),
                if (positiveButtonNeeded) "No" else "Okay",
                if (positiveButtonNeeded) "Yes" else null,
                positiveButtonAction = {
                    if (positiveButtonNeeded) {
                        onLifelinePositiveButtonClick(lifeline, remainingSeconds)
                    }
                },
                negativeButtonAction = {
                    val totalQuestions = questionViewModel.getQuestionsCount()
                    if (args.questionToBeAsked <= totalQuestions - 6) {
                        timerViewModel.startTimer(remainingSeconds)
                    }
                }
            )
        }
    }

    private fun getTextForLifelines(lifeline: Lifeline): String {
        val correctOptionNumber = questionViewModel.currentQuestion.value?.correctOptionNumber ?: 0

        return when (lifeline) {
            Lifeline.AUDIENCE_POLL -> {
                val percentageList = MutableList(3) { (0..15).random() }
                val correctOptionPercentage = 100 - percentageList.sum()
                percentageList.add(correctOptionNumber - 1, correctOptionPercentage)

                String.format(
                    Locale.US,
                    "Option A: %d %%\nOption B: %d %%\nOption C: %d %%\nOption D: %d %%",
                    percentageList[0], percentageList[1], percentageList[2], percentageList[3]
                )
            }

            Lifeline.PHONE_A_FRIEND -> {
                val stringMap =
                    mapOf(1 to "Option A", 2 to "Option B", 3 to "Option C", 4 to "Option D")
                "Your friend has suggested you the ${stringMap[correctOptionNumber]} with ${(61..85).random()}% probability."
            }

            Lifeline.SKIP_QUESTION -> "Do you want to skip the question ?"
            Lifeline.FIFTY_FIFTY -> "Do you want to use 50-50 ?"
        }
    }

    private fun onLifelinePositiveButtonClick(lifeline: Lifeline, remainingSeconds: Long = 15L) {
        questionViewModel.onLifelineClick(lifeline.num)
        if (lifeline == Lifeline.FIFTY_FIFTY) {
            val viewsMap = mapOf(
                0 to binding.tvOption1,
                1 to binding.tvOption2,
                2 to binding.tvOption3,
                3 to binding.tvOption4
            )
            val correctOptionNumber =
                questionViewModel.currentQuestion.value?.correctOptionNumber ?: 0

            val optionVisibility = MutableList(3) { false }
            optionVisibility[(0..2).random()] = true
            optionVisibility.add(correctOptionNumber - 1, true)

            optionVisibility.forEachIndexed { index, isVisible ->
                if (!isVisible) {
                    viewsMap[index]?.apply {
                        isClickable = false
                        animate()
                            .alpha(0f)
                            .setDuration(600)
                            .withEndAction {
                                text = ""
                                alpha = 1f
                            }
                    }
                }
            }
            val totalQuestions = questionViewModel.getQuestionsCount()
            if (args.questionToBeAsked <= totalQuestions - 6) {
                timerViewModel.startTimer(remainingSeconds)
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
        val totalQuestions = questionViewModel.getQuestionsCount()
        if (args.questionToBeAsked > totalQuestions - 6) {
            binding.tvTimer.hide()
        } else {
            binding.tvTimer.show()
            timerViewModel.startTimer(15)
        }
    }

    private fun setQuestion() {
        val totalQuestions = questionViewModel.getQuestionsCount()
        if (args.questionToBeAsked !in 1..totalQuestions) {
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
        val totalQuestions = questionViewModel.getQuestionsCount()
        playMusic(MusicToPlay.QUESTIONNAIRE, args.questionToBeAsked <= totalQuestions - 6)
    }

    private fun showResult(result: ResultType, correctOptionNumber: Int = 0) {
        val totalQuestions = questionViewModel.getQuestionsCount()
        when (result) {

            ResultType.RIGHT_ANSWER -> {
                changeOptionColors(questionViewModel.getCurrentSelectedOption() to R.drawable.background_metallic_green)
                playMusic(MusicToPlay.CORRECT_ANSWER)
                val destination = if (args.questionToBeAsked >= totalQuestions) {
                    QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                        true,
                        questionViewModel.getFinalPrizeAmount()
                    )
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
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                    false,
                    questionViewModel.getLastSafeZone()
                )
                navigateWithDelay(destination, 7000)
            }

            ResultType.TIMER_UP -> {
                changeOptionColors(correctOptionNumber to R.drawable.background_metallic_green)
                playMusic(MusicToPlay.WRONG_ANSWER)
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                    false,
                    questionViewModel.getLastSafeZone()
                )
                navigateWithDelay(destination)
            }

            ResultType.QUIT -> {
                changeOptionColors(correctOptionNumber to R.drawable.background_metallic_green)
                playMusic(MusicToPlay.WRONG_ANSWER)
                val destination = QuestionFragmentDirections.actionQuestionFragmentToResultFragment(
                    false,
                    questionViewModel.getMoneyWonTillNow()
                )
                navigateWithDelay(destination)
            }

            ResultType.SKIP_QUESTION -> {
                changeOptionColors(correctOptionNumber to R.drawable.background_metallic_green)
                playMusic(MusicToPlay.CORRECT_ANSWER)
                val destination =
                    QuestionFragmentDirections.actionQuestionFragmentToPrizeListFragment(args.questionToBeAsked + 1)
                navigateWithDelay(destination)
            }
        }

    }

    private fun navigateWithDelay(destination: NavDirections, delayDuration: Long = 5000) {
        val finalDelay = if (isMusicOn()) delayDuration else 1500L
        CoroutineScope(Dispatchers.Main).launch {
            delay(finalDelay.milliseconds)
            findNavController().navigate(destination)
        }
    }

    enum class ResultType {
        TIMER_UP, RIGHT_ANSWER, WRONG_ANSWER, QUIT, SKIP_QUESTION
    }

    enum class Lifeline(val num: Int) {
        AUDIENCE_POLL(1), PHONE_A_FRIEND(2), FIFTY_FIFTY(3), SKIP_QUESTION(4)
    }

    private fun showAudiencePollDialog(remainingSeconds: Long) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext()).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_audience_poll, null)
        dialog.setView(dialogView)
        dialog.window?.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        val progressA = dialogView.findViewById<android.widget.ProgressBar>(R.id.progress_a)
        val progressB = dialogView.findViewById<android.widget.ProgressBar>(R.id.progress_b)
        val progressC = dialogView.findViewById<android.widget.ProgressBar>(R.id.progress_c)
        val progressD = dialogView.findViewById<android.widget.ProgressBar>(R.id.progress_d)

        val tvPercentA = dialogView.findViewById<android.widget.TextView>(R.id.tv_percent_a)
        val tvPercentB = dialogView.findViewById<android.widget.TextView>(R.id.tv_percent_b)
        val tvPercentC = dialogView.findViewById<android.widget.TextView>(R.id.tv_percent_c)
        val tvPercentD = dialogView.findViewById<android.widget.TextView>(R.id.tv_percent_d)

        val btnClose = dialogView.findViewById<android.widget.Button>(R.id.btn_close)

        val visibleOptions = mutableListOf<Int>()
        if (binding.tvOption1.text.isNotEmpty()) visibleOptions.add(1)
        if (binding.tvOption2.text.isNotEmpty()) visibleOptions.add(2)
        if (binding.tvOption3.text.isNotEmpty()) visibleOptions.add(3)
        if (binding.tvOption4.text.isNotEmpty()) visibleOptions.add(4)

        val qNumber = args.questionToBeAsked
        val correctOpt = questionViewModel.currentQuestion.value?.correctOptionNumber ?: 1

        val correctPercentage = when {
            qNumber <= 5 -> (75..90).random()
            qNumber <= 10 -> (50..72).random()
            else -> (30..50).random()
        }

        val percentages = IntArray(5)
        if (visibleOptions.size == 2) {
            val wrongOpt =
                visibleOptions.firstOrNull { it != correctOpt } ?: (if (correctOpt == 1) 2 else 1)
            percentages[correctOpt] = correctPercentage
            percentages[wrongOpt] = 100 - correctPercentage
        } else {
            percentages[correctOpt] = correctPercentage
            var remaining = 100 - correctPercentage
            val wrongOptions = visibleOptions.filter { it != correctOpt }
            if (wrongOptions.size == 3) {
                val p1 = (0..remaining / 2).random()
                remaining -= p1
                val p2 = (0..remaining).random()
                remaining -= p2
                val p3 = remaining
                percentages[wrongOptions[0]] = p1
                percentages[wrongOptions[1]] = p2
                percentages[wrongOptions[2]] = p3
            }
        }

        dialog.show()
        hideSystemBarsForDialog(dialog)

        animateProgressBar(progressA, tvPercentA, percentages[1])
        animateProgressBar(progressB, tvPercentB, percentages[2])
        animateProgressBar(progressC, tvPercentC, percentages[3])
        animateProgressBar(progressD, tvPercentD, percentages[4])

        btnClose.setOnClickListener {
            dialog.dismiss()
            val totalQuestions = questionViewModel.getQuestionsCount()
            if (args.questionToBeAsked <= totalQuestions - 6) {
                timerViewModel.startTimer(remainingSeconds)
            }
        }
    }

    private fun animateProgressBar(
        progressBar: android.widget.ProgressBar,
        textView: android.widget.TextView,
        target: Int
    ) {
        val animator = android.animation.ValueAnimator.ofInt(0, target)
        animator.duration = 1200
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            progressBar.progress = value
            textView.text = String.format(Locale.US, "%d%%", value)
        }
        animator.start()
    }

    private fun showPhoneAFriendSelectionDialog(remainingSeconds: Long) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext()).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_friend_selection, null)
        dialog.setView(dialogView)
        dialog.window?.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        val btnCancel = dialogView.findViewById<android.widget.Button>(R.id.btn_cancel)
        val layoutFriend1 = dialogView.findViewById<View>(R.id.layout_friend_1)
        val layoutFriend2 = dialogView.findViewById<View>(R.id.layout_friend_2)
        val layoutFriend3 = dialogView.findViewById<View>(R.id.layout_friend_3)

        dialog.show()
        hideSystemBarsForDialog(dialog)

        layoutFriend1.setOnClickListener {
            dialog.dismiss()
            questionViewModel.onLifelineClick(Lifeline.PHONE_A_FRIEND.num)
            startPhoneCall("Rahul", remainingSeconds)
        }

        layoutFriend2.setOnClickListener {
            dialog.dismiss()
            questionViewModel.onLifelineClick(Lifeline.PHONE_A_FRIEND.num)
            startPhoneCall("Aman", remainingSeconds)
        }

        layoutFriend3.setOnClickListener {
            dialog.dismiss()
            questionViewModel.onLifelineClick(Lifeline.PHONE_A_FRIEND.num)
            startPhoneCall("Neha", remainingSeconds)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
            val totalQuestions = questionViewModel.getQuestionsCount()
            if (args.questionToBeAsked <= totalQuestions - 6) {
                timerViewModel.startTimer(remainingSeconds)
            }
        }
    }

    private fun startPhoneCall(friendName: String, remainingSeconds: Long) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext()).create()
        val dialogView = layoutInflater.inflate(R.layout.dialog_phone_call, null)
        dialog.setView(dialogView)
        dialog.window?.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        val tvFriendName = dialogView.findViewById<android.widget.TextView>(R.id.tv_friend_name)
        val tvCallStatus = dialogView.findViewById<android.widget.TextView>(R.id.tv_call_status)
        val tvFriendSpeech = dialogView.findViewById<android.widget.TextView>(R.id.tv_friend_speech)
        val btnAction = dialogView.findViewById<android.widget.Button>(R.id.btn_action)

        val displayName = when (friendName) {
            "Rahul" -> "Rahul (The Scholar)"
            "Aman" -> "Aman (The Gamer)"
            else -> "Neha (The Cautious)"
        }
        tvFriendName.text = displayName
        tvCallStatus.text = getString(R.string.calling)
        btnAction.text = getString(R.string.hang_up)

        dialog.show()
        hideSystemBarsForDialog(dialog)

        val question = questionViewModel.currentQuestion.value
        val correctOptionNum = question?.correctOptionNumber ?: 1
        val correctOptionLetter = when (correctOptionNum) {
            1 -> "Option A"
            2 -> "Option B"
            3 -> "Option C"
            else -> "Option D"
        }
        val correctOptionText = when (correctOptionNum) {
            1 -> question?.option1
            2 -> question?.option2
            3 -> question?.option3
            else -> question?.option4
        } ?: ""

        val wrongOptions = mutableListOf<Pair<String, String>>()
        if (binding.tvOption1.text.isNotEmpty() && correctOptionNum != 1) wrongOptions.add(
            "Option A" to (question?.option1 ?: "")
        )
        if (binding.tvOption2.text.isNotEmpty() && correctOptionNum != 2) wrongOptions.add(
            "Option B" to (question?.option2 ?: "")
        )
        if (binding.tvOption3.text.isNotEmpty() && correctOptionNum != 3) wrongOptions.add(
            "Option C" to (question?.option3 ?: "")
        )
        if (binding.tvOption4.text.isNotEmpty() && correctOptionNum != 4) wrongOptions.add(
            "Option D" to (question?.option4 ?: "")
        )

        val chosenWrong =
            if (wrongOptions.isNotEmpty()) wrongOptions.random() else ("Option A" to "")
        val wrongOptionLetter = chosenWrong.first
        val wrongOptionText = chosenWrong.second

        val category = getQuestionCategory()
        val qNumber = args.questionToBeAsked

        val isCorrect = when (friendName) {
            "Rahul" -> {
                if (category == "popculture") (1..100).random() <= 35
                else (1..100).random() <= 85
            }

            "Aman" -> {
                if (category == "popculture") (1..100).random() <= 90
                else (1..100).random() <= 40
            }

            else -> {
                (1..100).random() <= 80
            }
        }

        val speechText = when (friendName) {
            "Rahul" -> {
                if (category == "popculture") {
                    if (isCorrect) "Well, I don't follow pop culture or sports at all... but I guess it could be $correctOptionLetter ($correctOptionText)?"
                    else "Oh, I really have no interest in pop culture or media. I'm totally guessing here, but maybe $wrongOptionLetter ($wrongOptionText)?"
                } else {
                    if (isCorrect) "Oh! That is an interesting question. I'm positive it's $correctOptionLetter ($correctOptionText). I recall reading about it in a journal recently."
                    else "Hmm... I'm a bit rusty on this topic, but I think it should be $wrongOptionLetter ($wrongOptionText)."
                }
            }

            "Aman" -> {
                if (category == "popculture") {
                    if (isCorrect) "Bro, that's literally so easy! It is definitely $correctOptionLetter ($correctOptionText). Lock it in without a doubt!"
                    else "Uhh... I think it's $wrongOptionLetter ($wrongOptionText)? Wait, actually, let me think... yeah, that sounds right."
                } else {
                    if (isCorrect) "History and general knowledge was so boring, man. But if I had to choose, I'd gamble on $correctOptionLetter ($correctOptionText)."
                    else "Wow, school stuff... I'm totally guessing this, but $wrongOptionLetter ($wrongOptionText) sounds familiar?"
                }
            }

            else -> {
                if (qNumber >= 11) {
                    "Hey, I am so sorry, but I really have absolutely no clue about this question. At this stage of the game, I highly recommend not taking a risk. You should quit or use another lifeline!"
                } else {
                    if (isCorrect) "Hey, let me think carefully... Yes, I am about 80% confident that the answer is $correctOptionLetter ($correctOptionText)."
                    else "I am not entirely sure, but I think Option $wrongOptionLetter ($wrongOptionText) is the safest guess."
                }
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            delay(500.milliseconds)
            tvCallStatus.text = getString(R.string.ringing)
            delay(1000.milliseconds)
            tvCallStatus.text = getString(R.string.connected_00_00)

            var seconds = 0
            val job = launch {
                while (true) {
                    delay(1000.milliseconds)
                    seconds++
                    tvCallStatus.text = String.format(
                        Locale.US,
                        "Connected (%02d:%02d)",
                        seconds / 60,
                        seconds % 60
                    )
                }
            }

            typeText(tvFriendSpeech, speechText) {
                btnAction.text = getString(R.string.ok)
                btnAction.setBackgroundResource(R.drawable.background_metallic_blue)
                btnAction.setOnClickListener {
                    job.cancel()
                    dialog.dismiss()
                    val totalQuestions = questionViewModel.getQuestionsCount()
                    if (args.questionToBeAsked <= totalQuestions - 6) {
                        timerViewModel.startTimer(remainingSeconds)
                    }
                }
            }

            btnAction.setOnClickListener {
                job.cancel()
                dialog.dismiss()
                val totalQuestions = questionViewModel.getQuestionsCount()
                if (args.questionToBeAsked <= totalQuestions - 6) {
                    timerViewModel.startTimer(remainingSeconds)
                }
            }
        }
    }

    private fun getQuestionCategory(): String {
        val questionText =
            questionViewModel.currentQuestion.value?.question?.lowercase(Locale.US) ?: ""
        val historyKeywords = listOf(
            "history",
            "emperor",
            "king",
            "war",
            "battle",
            "century",
            "independence",
            "president",
            "gandhi",
            "ruled",
            "minister",
            "constitution",
            "dynasty",
            "empire"
        )
        val popCultureKeywords = listOf(
            "game",
            "sport",
            "cricket",
            "football",
            "olympic",
            "player",
            "movie",
            "actor",
            "actress",
            "song",
            "film",
            "director",
            "singer",
            "musical",
            "bollywood",
            "cricketer",
            "wicket",
            "stadium"
        )

        return when {
            historyKeywords.any { questionText.contains(it) } -> "history"
            popCultureKeywords.any { questionText.contains(it) } -> "popculture"
            else -> "gk"
        }
    }

    private fun typeText(textView: android.widget.TextView, text: String, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0..text.length) {
                textView.text = text.substring(0, i)
                delay(30.milliseconds)
            }
            onComplete()
        }
    }

    private fun hideSystemBarsForDialog(dialog: androidx.appcompat.app.AlertDialog) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            dialog.window?.insetsController?.apply {
                hide(android.view.WindowInsets.Type.systemBars())
                systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            dialog.window?.decorView?.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }

}