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
                    binding.tvEmptyList.text = getString(R.string.loading)
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
                            binding.rvLeaderboard.adapter = LeaderboardAdapter(requireContext(), list)
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
        binding.btnBack.setOnClickListener { // TODO: app getting crashed when changing to setOnClickListenerWithSfxAudio
            playSfxAudio()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnReset.setOnClickListenerWithSfxAudio {
            leaderboardViewModel.clearAllData()
        }
    }
}