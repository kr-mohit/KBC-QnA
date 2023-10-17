package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchSavedData()
        setObservers()
        setOnClickListeners()
    }

    private fun fetchSavedData() {
        homeViewModel.getWonLostData()
    }

    private fun setObservers() {
        homeViewModel.wonLostData.observe(viewLifecycleOwner) {
            binding.tvWonCount.text = getString(R.string.wins, it.wins.toString())
            binding.tvLostCount.text = getString(R.string.lost, it.loses.toString())
        }
    }

    private fun setOnClickListeners() {
        binding.btnStart.setOnClickListener {
            gotoFragment(PrizeListFragment())
        }

        binding.btnSettings.setOnClickListener {
            gotoFragment(SettingsFragment())
        }
    }
}