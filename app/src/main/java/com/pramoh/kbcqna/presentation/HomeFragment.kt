package com.pramoh.kbcqna.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        fetchSavedData()
        setObservers()
        setOnClickListeners()
    }

    private fun fetchSavedData() {
        viewModel.getWonLostData()
    }

    private fun setObservers() {
        viewModel.wonLostData.observe(viewLifecycleOwner) {
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