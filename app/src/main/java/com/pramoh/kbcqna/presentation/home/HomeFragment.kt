package com.pramoh.kbcqna.presentation.home

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
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.presentation.ExoplayerViewModel
import com.pramoh.kbcqna.presentation.questionnaire.QuestionViewModel
import com.pramoh.kbcqna.utils.Constants
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val questionViewModel: QuestionViewModel by activityViewModels() // TODO: See if you can remove this, and get the currentPlayerName by something else
    private val homeViewModel: HomeViewModel by activityViewModels() // TODO: See if you can remove this, and get the currentPlayerName by something else
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

    private var isOnlinePlayAttempted = false

    private fun setObservers() {
        homeViewModel.playerNameSharedPref.observe(viewLifecycleOwner) {
            binding.etPlayerName.setText(if (it == "") "Player" else it)
        }

        questionViewModel.questionsLiveData.observe(viewLifecycleOwner) { response ->
            when(response) {
                is Response.Loading -> {
                    binding.homeProgressBar.show()
                    binding.tvLoadingText.show()
                }
                is Response.Success -> {
                    binding.homeProgressBar.hide()
                    binding.tvLoadingText.hide()
                    if (homeViewModel.getOnStartClicked()) {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPrizeListFragment(1))
                    }
                }
                is Response.Error -> {
                    binding.homeProgressBar.hide()
                    binding.tvLoadingText.hide()
                    enableAllButtonsClick()
                    if (isOnlinePlayAttempted) {
                        isOnlinePlayAttempted = false
                        showDialog(
                            requireContext(),
                            "Failed to fetch online questions. Would you like to play offline instead?",
                            "No",
                            "Yes",
                            positiveButtonAction = {
                                if (binding.etPlayerName.text.isNotBlank()) {
                                    disableAllButtonsClick()
                                    homeViewModel.setOnStartClicked(true)
                                    questionViewModel.fetchQuestionsOffline()
                                }
                            }
                        )
                    } else {
                        Toast.makeText(context, response.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        exoplayerViewModel.isMusicOn.observe(viewLifecycleOwner) {
            if (it) {
                playMusic(MusicToPlay.HOME_SCREEN)
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnPlayOnline.setOnClickListenerWithSfxAudio {
            if (binding.etPlayerName.text.isBlank()) {
                Toast.makeText(context, "Enter Player Name", Toast.LENGTH_SHORT).show()
            } else {
                disableAllButtonsClick()
                isOnlinePlayAttempted = true
                homeViewModel.setOnStartClicked(true)
                homeViewModel.setPlayerNameSharedPref(binding.etPlayerName.text.toString())
                homeViewModel.setCurrentPlayerName(binding.etPlayerName.text.toString())
                questionViewModel.fetchQuestions(Constants.FULL_URL)
            }
        }

        binding.btnPlayOffline.setOnClickListenerWithSfxAudio {
            if (binding.etPlayerName.text.isBlank()) {
                Toast.makeText(context, "Enter Player Name", Toast.LENGTH_SHORT).show()
            } else {
                disableAllButtonsClick()
                isOnlinePlayAttempted = false
                homeViewModel.setOnStartClicked(true)
                homeViewModel.setPlayerNameSharedPref(binding.etPlayerName.text.toString())
                homeViewModel.setCurrentPlayerName(binding.etPlayerName.text.toString())
                questionViewModel.fetchQuestionsOffline()
            }
        }

        binding.btnSettings.setOnClickListenerWithSfxAudio {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
        }

        binding.ivOption.setOnClickListenerWithSfxAudio {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLeaderboardFragment())
        }
    }

    private fun disableAllButtonsClick() {
        binding.btnPlayOnline.isClickable = false
        binding.btnPlayOffline.isClickable = false
        binding.btnSettings.isClickable = false
        binding.etPlayerName.isEnabled = false
        binding.ivOption.isClickable = false
    }

    private fun enableAllButtonsClick() {
        binding.btnPlayOnline.isClickable = true
        binding.btnPlayOffline.isClickable = true
        binding.btnSettings.isClickable = true
        binding.etPlayerName.isEnabled = true
        binding.ivOption.isClickable = true
    }
}