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
import com.pramoh.kbcqna.domain.model.PlayerData
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaderboardFragment : BaseFragment() {

    private lateinit var binding: FragmentLeaderboardBinding
    private val leaderboardViewModel: LeaderboardViewModel by viewModels()

    private var playerNumber: Int = 1 // TODO: For testing only

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
        leaderboardViewModel.getLeaderboardData()
    }

    private fun setObservers() {

        leaderboardViewModel.leaderboardList.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Error -> {
                    binding.tvEmptyList.text = it.error
                    binding.rvLeaderboard.hide()
                    binding.ivEmptyList.show()
                    binding.tvEmptyList.show()
                }
                is Response.Loading -> {
                    binding.tvEmptyList.text = "Loading..."
                    binding.rvLeaderboard.hide()
                    binding.ivEmptyList.visibility = View.INVISIBLE
                    binding.tvEmptyList.show()
                }
                is Response.Success -> {
                    it.data?.let { list ->
                        if (list.isEmpty()) {
                            binding.rvLeaderboard.hide()
                            binding.tvEmptyList.show()
                            binding.ivEmptyList.show()
                        } else {
                            binding.rvLeaderboard.adapter = LeaderboardAdapter(list)
                            binding.rvLeaderboard.layoutManager = LinearLayoutManager(context)
                            binding.rvLeaderboard.show()
                            binding.ivEmptyList.hide()
                            binding.tvEmptyList.hide()
                        }
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListenerWithSfxAudio {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnReset.setOnClickListenerWithSfxAudio {
            leaderboardViewModel.clearAllData()
        }

        binding.btnAdd.setOnClickListenerWithSfxAudio {
            val moneyList = listOf(
                100000000,
                30000000,
                10000000,
                5000000,
                2500000,
                1250000,
                640000,
                320000,
                160000,
                80000,
                40000,
                20000,
                10000,
                5000,
                1000
            )
            val newEntry = PlayerData(0, "Player ${playerNumber++}", moneyList.random())
            leaderboardViewModel.insertPlayerToDB(newEntry)
        }
    }
}