package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val exoplayerViewModel: ExoplayerViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchSharedPref()
        setObservers()
        setOnClickListeners()
    }

    private fun fetchSharedPref() {
        exoplayerViewModel.getMusicSharedPref()
        exoplayerViewModel.getSfxAudioSharedPref()
        homeViewModel.getPlayerNameSharedPref()
    }

    private fun setObservers() {
        homeViewModel.playerNameSharedPref.observe(viewLifecycleOwner) {
            binding.etPlayerName.setText(if (it == "") "Player" else it)
        }

        exoplayerViewModel.isMusicOn.observe(viewLifecycleOwner) {
            if (it) {
                playMusic(MusicToPlay.HOME_SCREEN)
            } else {
                stopMusic()
            }
        }

//        questionViewModel.questionsLiveData.observe(viewLifecycleOwner) { response ->
//            when(response) {
//                is Response.Loading -> {
//                    binding.homeProgressBar.show()
//                }
//                is Response.Success -> {
//                    binding.homeProgressBar.hide()
//                    if (homeViewModel.getOnStartClicked()) {
//                        stopMusic()
//                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPrizeListFragment(1))
//                    }
//                }
//                is Response.Error -> {
//                    binding.homeProgressBar.hide()
//                    Toast.makeText(context, response.error, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
    }

    private fun setOnClickListeners() {
        binding.btnGoOnline.setOnClickListenerWithSfxAudio {
            showComingSoonToast()
//            if (NetworkUtils.isOnline(requireContext())) {
//                if (binding.etPlayerName.text.isBlank()) {
//                    Toast.makeText(context, "Enter Player Name", Toast.LENGTH_SHORT).show()
//                } else {
//                    binding.btnSettings.isClickable = false
//                    binding.etPlayerName.isEnabled = false
//                    binding.ivOption.isClickable = false
//                    homeViewModel.setOnStartClicked(true)
//                    homeViewModel.setPlayerNameSharedPref(binding.etPlayerName.text.toString())
//                    questionViewModel.fetchQuestions(Constants.FULL_URL)
//                }
//            } else {
//                Toast.makeText(context, "Check internet connection", Toast.LENGTH_SHORT).show()
//            }
        }

        binding.btnPlayOffline.setOnClickListenerWithSfxAudio {
            if (binding.etPlayerName.text.isBlank()) {
                Toast.makeText(context, "Enter Player Name", Toast.LENGTH_SHORT).show()
            } else {
                homeViewModel.setPlayerNameSharedPref(binding.etPlayerName.text.toString())
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLevelsFragment())
            }
        }

        binding.btnSettings.setOnClickListenerWithSfxAudio {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
        }

        binding.ivOption.setOnClickListenerWithSfxAudio {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLeaderboardFragment())
        }
    }
}