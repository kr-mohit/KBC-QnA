package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment: BaseFragment() {

    private lateinit var binding: FragmentResultBinding
    private val resultViewModel: ResultViewModel by viewModels()
    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
        setOnClickListeners()
        saveWonLostData(args.didUserWin)
    }

    private fun setUI() {
        binding.tvPrizeAmount.text = args.prizeMoney
        if (args.didUserWin) {
            binding.tvBetterLuck.visibility = View.GONE
            binding.tvNextTime.visibility = View.GONE
        } else {
            binding.tvBetterLuck.visibility = View.VISIBLE
            binding.tvNextTime.visibility = View.VISIBLE
        }
    }

    private fun setOnClickListeners() {
        binding.btnShareCheque.setOnClickListener {
            displayComingSoonToast()
        }

        binding.btnShareGame.setOnClickListener {
            displayComingSoonToast()
        }

        binding.btnMoreGames.setOnClickListener {
            displayComingSoonToast()
        }

        binding.btnStartAgain.setOnClickListener {
            findNavController().navigate(ResultFragmentDirections.actionResultFragmentToHomeFragment())
        }
    }

    private fun saveWonLostData(didUserWin: Boolean) {
        // TODO: find out whether user won or lost
        resultViewModel.saveWonLostData(didUserWin)
    }
}