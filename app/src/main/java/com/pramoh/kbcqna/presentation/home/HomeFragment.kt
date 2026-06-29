package com.pramoh.kbcqna.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import com.pramoh.kbcqna.utils.NetworkUtils
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
    private var nameToCheck = ""

    private fun setObservers() {
        homeViewModel.playerNameSharedPref.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.etPlayerName.setText(it)
            } else {
                binding.etPlayerName.setText("")
            }
        }

        homeViewModel.playerNameCheckResult.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            when (response) {
                is Response.Loading -> {
                    binding.homeProgressBar.show()
                    binding.tvLoadingText.text = getString(R.string.checking_username)
                    binding.tvLoadingText.show()
                    disableAllButtonsClick()
                }
                is Response.Success -> {
                    val exists = response.data ?: false
                    if (exists) {
                        binding.homeProgressBar.hide()
                        binding.tvLoadingText.hide()
                        enableAllButtonsClick()
                        binding.etPlayerName.error = "Name already exists in leaderboard!"
                        binding.etPlayerName.requestFocus()
                        Toast.makeText(context, "Name already exists in the leaderboard. Please choose a different name.", Toast.LENGTH_SHORT).show()
                        homeViewModel.resetPlayerNameCheck()
                    } else {
                        homeViewModel.resetPlayerNameCheck()
                        homeViewModel.setOnStartClicked(true)
                        homeViewModel.setPlayerNameSharedPref(nameToCheck)
                        homeViewModel.setCurrentPlayerName(nameToCheck)
                        if (isOnlinePlayAttempted) {
                            questionViewModel.fetchQuestions(Constants.FULL_URL)
                        } else {
                            questionViewModel.fetchQuestionsOffline()
                        }
                    }
                }
                is Response.Error -> {
                    binding.homeProgressBar.hide()
                    binding.tvLoadingText.hide()
                    enableAllButtonsClick()
                    Toast.makeText(context, response.error ?: "Failed to verify username", Toast.LENGTH_SHORT).show()
                    homeViewModel.resetPlayerNameCheck()
                }
            }
        }

        questionViewModel.questionsLiveData.observe(viewLifecycleOwner) { response ->
            when(response) {
                is Response.Loading -> {
                    binding.homeProgressBar.show()
                    binding.tvLoadingText.text = getString(R.string.loading_questions)
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
            val name = binding.etPlayerName.text.toString().trim()
            if (name.isEmpty()) {
                binding.etPlayerName.error = "Name is required to play!"
                binding.etPlayerName.requestFocus()
                Toast.makeText(context, "Please enter your name first", Toast.LENGTH_SHORT).show()
            } else {
                if (NetworkUtils.isOnline(requireContext())) {
                    isOnlinePlayAttempted = true
                    nameToCheck = name
                    homeViewModel.checkPlayerNameExists(name)
                } else {
                    Toast.makeText(context, "Internet connection required to play online!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnPlayOffline.setOnClickListenerWithSfxAudio {
            val name = binding.etPlayerName.text.toString().trim()
            if (name.isEmpty()) {
                binding.etPlayerName.error = "Name is required to play!"
                binding.etPlayerName.requestFocus()
                Toast.makeText(context, "Please enter your name first", Toast.LENGTH_SHORT).show()
            } else {
                disableAllButtonsClick()
                isOnlinePlayAttempted = false
                homeViewModel.setOnStartClicked(true)
                homeViewModel.setPlayerNameSharedPref(name)
                homeViewModel.setCurrentPlayerName(name)
                questionViewModel.fetchQuestionsOffline()
            }
        }

        binding.ivOption.setOnClickListenerWithSfxAudio {
            val popup = PopupMenu(requireContext(), binding.ivOption)
            popup.menu.add(Menu.NONE, 1, Menu.NONE, "Leaderboard")
            popup.menu.add(Menu.NONE, 2, Menu.NONE, "Settings")
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    1 -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLeaderboardFragment())
                        true
                    }
                    2 -> {
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun disableAllButtonsClick() {
        binding.btnPlayOnline.isClickable = false
        binding.btnPlayOffline.isClickable = false
        binding.etPlayerName.isEnabled = false
        binding.ivOption.isClickable = false
    }

    private fun enableAllButtonsClick() {
        binding.btnPlayOnline.isClickable = true
        binding.btnPlayOffline.isClickable = true
        binding.etPlayerName.isEnabled = true
        binding.ivOption.isClickable = true
    }
}