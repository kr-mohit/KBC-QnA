package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentResultBinding
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.utils.MoneyTypeConversionUtil
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
        setObserver()
        setOnClickListeners()
        playMusic(MusicToPlay.RESULT_SCREEN)
        resultViewModel.getPlayerNameSharedPref()
    }

    private fun setUI() {
        binding.tvPrizeAmount.text = MoneyTypeConversionUtil.convertToString(args.prizeMoney)
        if (args.didUserWin) {
            binding.tvBetterLuck.hide()
            binding.tvNextTime.hide()
        } else {
            binding.tvBetterLuck.show()
            binding.tvNextTime.show()
        }
    }

    private fun setObserver() {
        resultViewModel.playerNameSharedPref.observe(viewLifecycleOwner) {
            addPlayerToLeaderboard(it, args.prizeMoney)
        }

        resultViewModel.didPlayerInsert.observe(viewLifecycleOwner) {
            Toast.makeText(context, "Game saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setOnClickListeners() {
        binding.btnShareCheque.setOnClickListenerWithSfxAudio {
            showComingSoonToast()
        }

        binding.btnShareGame.setOnClickListenerWithSfxAudio {
            showComingSoonToast()
        }

        binding.btnMoreGames.setOnClickListenerWithSfxAudio {
            showComingSoonToast()
        }

        binding.btnStartAgain.setOnClickListenerWithSfxAudio {
            stopMusic()
            findNavController().navigate(ResultFragmentDirections.actionResultFragmentToHomeFragment())
        }
    }

    private fun addPlayerToLeaderboard(playerName: String, moneyWon: Int) {
        resultViewModel.insertPlayerToDB(PlayerData(0, playerName, moneyWon))
    }
}