package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
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
    private val homeViewModel: HomeViewModel by activityViewModels() // TODO: See if you can remove this, and get the currentPlayerName by something else
    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
        setOnClickListeners()
        saveData()
        playMusic(MusicToPlay.RESULT_SCREEN)
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

    private fun setOnClickListeners() {
        binding.btnShareCheque.setOnClickListener {
            playSfxAudio()
            showComingSoonToast()
        }

        binding.btnShareGame.setOnClickListener {
            playSfxAudio()
            showComingSoonToast()
        }

        binding.btnMoreGames.setOnClickListener {
            playSfxAudio()
            showComingSoonToast()
        }

        binding.btnStartAgain.setOnClickListener {
            playSfxAudio()
            stopMusic()
            findNavController().navigate(ResultFragmentDirections.actionResultFragmentToHomeFragment())
        }
    }

    private fun saveData() {
        addPlayerToLeaderboard(homeViewModel.getCurrentPlayerName(), args.prizeMoney)
    }

    private fun addPlayerToLeaderboard(playerName: String, moneyWon: Int) {
        resultViewModel.insertPlayerToDB(PlayerData(0, playerName, moneyWon))
    }
}