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
import com.pramoh.kbcqna.utils.Constants
import com.pramoh.kbcqna.utils.NetworkUtils
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val questionViewModel: QuestionViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
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
        homeViewModel.setOnStarClicked(false)
    }

    private fun fetchSavedData() {
        homeViewModel.getWonLostData()
        exoplayerViewModel.getAudioSharedPref()
    }

    private fun setObservers() {
        homeViewModel.wonLostData.observe(viewLifecycleOwner) {
            binding.tvWonCount.text = getString(R.string.wins, it.wins.toString())
            binding.tvLostCount.text = getString(R.string.lost, it.loses.toString())
        }

        questionViewModel.questionsLiveData.observe(viewLifecycleOwner) { response ->
            when(response) {
                is Response.Loading -> {
                    binding.homeProgressBar.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    binding.homeProgressBar.visibility = View.GONE
                    if (homeViewModel.getOnStartClicked()) {
                        exoplayerViewModel.stop()
                        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPrizeListFragment(1))
                    }
                }
                is Response.Error -> {
                    binding.homeProgressBar.visibility = View.GONE
                    Toast.makeText(context, response.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        exoplayerViewModel.isSoundOn.observe(viewLifecycleOwner) {
            if (it) {
                exoplayerViewModel.setupAndPlay(R.raw.audio_home_screen)
            } else {
                exoplayerViewModel.stop()
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnStart.setOnClickListener {
            if (NetworkUtils.isOnline(requireContext())) {
                homeViewModel.setOnStarClicked(true)
                questionViewModel.fetchQuestions(Constants.FULL_URL)
            } else {
                Toast.makeText(context, "Check internet connection", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
        }
    }
}