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
import com.pramoh.kbcqna.utils.NetworkUtils
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.drawable.toDrawable

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val questionViewModel: QuestionViewModel by activityViewModels() // TODO: See if you can remove this, and get the currentPlayerName by something else
    private val homeViewModel: HomeViewModel by activityViewModels() // TODO: See if you can remove this, and get the currentPlayerName by something else
    private val exoplayerViewModel: ExoplayerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        homeViewModel.getPlayerNameSharedPref() // Keep this to populate SharedPreferences cache in VM

        questionViewModel.questionsLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.homeProgressBar.show()
                    binding.tvLoadingText.text = getString(R.string.loading_questions)
                    binding.tvLoadingText.show()
                }

                is Response.Success -> {
                    binding.homeProgressBar.hide()
                    binding.tvLoadingText.hide()
                    if (homeViewModel.getOnStartClicked()) {
                        findNavController().navigate(
                            HomeFragmentDirections.actionHomeFragmentToPrizeListFragment(
                                1
                            )
                        )
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
                                disableAllButtonsClick()
                                homeViewModel.setOnStartClicked(true)
                                questionViewModel.fetchQuestionsOffline()
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
        binding.btnPlay.setOnClickListenerWithSfxAudio {
            disableAllButtonsClick()
            homeViewModel.setCurrentPlayerName("")
            if (NetworkUtils.isOnline(requireContext())) {
                isOnlinePlayAttempted = true
                homeViewModel.setOnStartClicked(true)
                questionViewModel.fetchQuestions(Constants.FULL_URL)
            } else {
                isOnlinePlayAttempted = false
                homeViewModel.setOnStartClicked(true)
                questionViewModel.fetchQuestionsOffline()
            }
        }

        binding.ivOption.setOnClickListenerWithSfxAudio {
            val popupView = layoutInflater.inflate(R.layout.layout_home_options_popup, null)
            val popupWindow = android.widget.PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.setBackgroundDrawable(android.graphics.Color.TRANSPARENT.toDrawable())

            val tvLeaderboard =
                popupView.findViewById<android.widget.TextView>(R.id.tv_popup_option_leaderboard)
            val tvSettings =
                popupView.findViewById<android.widget.TextView>(R.id.tv_popup_option_settings)

            tvLeaderboard.setOnClickListener {
                playSfxAudio()
                popupWindow.dismiss()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLeaderboardFragment())
            }

            tvSettings.setOnClickListener {
                playSfxAudio()
                popupWindow.dismiss()
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
            }

            // Measure popup view to calculate exact width/height in pixels
            popupView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            val popupWidth = popupView.measuredWidth
            val xOffset = binding.ivOption.width - popupWidth
            val yOffset = -binding.ivOption.height

            popupWindow.showAsDropDown(
                binding.ivOption,
                xOffset,
                yOffset
            )
        }
    }

    private fun disableAllButtonsClick() {
        binding.btnPlay.isClickable = false
        binding.ivOption.isClickable = false
    }

    private fun enableAllButtonsClick() {
        binding.btnPlay.isClickable = true
        binding.ivOption.isClickable = true
    }
}