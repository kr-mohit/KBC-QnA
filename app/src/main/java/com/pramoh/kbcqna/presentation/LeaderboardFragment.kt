package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentLeaderboardBinding
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaderboardFragment : BaseFragment() {

    private lateinit var binding: FragmentLeaderboardBinding
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_leaderboard, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchLeaderboardData()
        setObservers()
        setOnClickListeners()
    }

    private fun fetchLeaderboardData() {
        leaderboardViewModel.getWonLostData()
        leaderboardViewModel.getLeaderboardTable()
    }

    private fun setObservers() {
        leaderboardViewModel.wonLostData.observe(viewLifecycleOwner) {
            binding.tvWonCount.text = getString(R.string.wins, it.wins.toString())
            binding.tvLostCount.text = getString(R.string.lost, it.loses.toString())
        }

        leaderboardViewModel.leaderboardList.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Error -> {
                    // TODO:
                }
                is Response.Loading -> {
                    // TODO:
                }
                is Response.Success -> {
                    // TODO:
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListener {
            playSfxAudio()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}