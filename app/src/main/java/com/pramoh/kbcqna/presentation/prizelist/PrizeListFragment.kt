package com.pramoh.kbcqna.presentation.prizelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentPrizeListBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.presentation.questionnaire.QuestionViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.OnBackPressedCallback
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil

@AndroidEntryPoint
class PrizeListFragment : BaseFragment() {

    private lateinit var binding: FragmentPrizeListBinding
    private val args: PrizeListFragmentArgs by navArgs()
    private val questionViewModel: QuestionViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prize_list, container, false)
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
        playMusic(MusicToPlay.PRIZE_LIST)
    }

    private fun setObservers() {
        val list = questionViewModel.getListOfPrizes()
        binding.listView.adapter = PrizeListAdapter(list, args.questionToBeAsked)
        binding.listView.layoutManager = LinearLayoutManager(context)
        binding.listView.post {
            val activePosition = list.size - args.questionToBeAsked
            if (activePosition in list.indices) {
                binding.listView.scrollToPosition(activePosition)
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnNext.setOnClickListenerWithSfxAudio {
            findNavController().navigate(PrizeListFragmentDirections.actionPrizeListFragmentToQuestionFragment(args.questionToBeAsked))
        }
    }

    private fun showQuitDialog() {
        val moneyWon = if (args.questionToBeAsked > 1) {
            questionViewModel.questionsLiveData.value?.data?.get(args.questionToBeAsked - 2)?.prizeAmount ?: 0
        } else {
            0
        }

        showDialog(
            requireContext(),
            "Do you want to Quit?\nYou will be getting ${MoneyTypeConversionUtil.convertToString(moneyWon)}",
            "No",
            "Yes",
            positiveButtonAction = {
                val destination = PrizeListFragmentDirections.actionPrizeListFragmentToResultFragment(false, moneyWon)
                findNavController().navigate(destination)
            }
        )
    }
}