package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentLeaderboardBinding
import com.pramoh.kbcqna.domain.model.LeaderboardData
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
                    binding.rvLeaderboard.adapter = it.data?.let { it1 -> LeaderboardAdapter(it1) }
                    binding.rvLeaderboard.layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListener {
            playSfxAudio()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnReset.setOnClickListener {
            playSfxAudio()
            leaderboardViewModel.clearAllData()
        }

        binding.btnAdd.setOnClickListener {
            playSfxAudio()
            val newEntry = LeaderboardData(0, "Player ${(0..10).random()}", "Rs. ${(1..50).random()*10000}")
            leaderboardViewModel.addScoreToDB(newEntry)
        }
    }
}