package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentHomeBinding
import com.pramoh.kbcqna.utils.Constants
import com.pramoh.kbcqna.utils.NetworkUtils
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val exoplayerViewModel: ExoplayerViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchSavedData()
        setObservers()
        setOnClickListeners()
        homeViewModel.setOnStartClicked(false)
    }

    private fun fetchSavedData() {
        exoplayerViewModel.getMusicSharedPref()
        exoplayerViewModel.getSfxAudioSharedPref()
        homeViewModel.getPlayerNameSharedPref()
    }

    private fun setObservers() {
        homeViewModel.playerNameSharedPref.observe(viewLifecycleOwner) {
            binding.etPlayerName.setText(if (it == "") "Player" else it)
        }

        questionViewModel.questionsLiveData.observe(viewLifecycleOwner) { response ->
            when(response) {
                is Response.Loading -> {
                    binding.homeProgressBar.show()
                }
                is Response.Success -> {
                    binding.homeProgressBar.hide()
                    if (homeViewModel.getOnStartClicked()) {
                        stopMusic()
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPrizeListFragment(1))
                    }
                }
                is Response.Error -> {
                    binding.homeProgressBar.hide()
                    Toast.makeText(context, response.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        exoplayerViewModel.isMusicOn.observe(viewLifecycleOwner) {
            if (it) {
                playMusic(MusicToPlay.HOME_SCREEN)
            } else {
                stopMusic()
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnStart.setOnClickListener {
            binding.btnSettings.isClickable = false
            playSfxAudio()
            if (NetworkUtils.isOnline(requireContext())) {
                if (binding.etPlayerName.text.isBlank()) {
                    Toast.makeText(context, "Enter Player Name", Toast.LENGTH_SHORT).show()
                } else {
                    homeViewModel.setOnStartClicked(true)
                    homeViewModel.setPlayerNameSharedPref(binding.etPlayerName.text.toString())
                    homeViewModel.setCurrentPlayerName(binding.etPlayerName.text.toString()) // TODO: Did this for getting player name at the result screen; check if this can be done without using activityViewModels()
                    questionViewModel.fetchQuestions(Constants.FULL_URL)
                }
            } else {
                Toast.makeText(context, "Check internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSettings.setOnClickListener {
            playSfxAudio()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
        }

        binding.ivOption.setOnClickListener {
            playSfxAudio()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLeaderboardFragment())
        }
    }
}