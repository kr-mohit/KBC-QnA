package com.pramoh.kbcqna.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentAdminStatsBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminStatsFragment: BaseFragment() {

    private lateinit var binding: FragmentAdminStatsBinding
    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_stats, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setOnClickListeners()

        adminViewModel.loadQuestionStats()
    }

    private fun setupObservers() {
        adminViewModel.questionStats.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.progressLoading.visibility = View.VISIBLE
                    binding.rvStats.visibility = View.GONE
                }
                is Response.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    binding.rvStats.visibility = View.VISIBLE
                    val statsMap = response.data ?: emptyMap()
                    val total = statsMap.values.sum()
                    binding.tvTotalQuestions.text = "$total Questions"

                    // Sort statistics by prize amount in ascending order
                    val sortedStats = statsMap.toList().sortedBy { it.first }
                    binding.rvStats.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvStats.adapter = AdminStatsAdapter(sortedStats)
                }
                is Response.Error -> {
                    binding.progressLoading.visibility = View.GONE
                    Toast.makeText(context, response.error ?: "Failed to load stats", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListenerWithSfxAudio {
            findNavController().navigateUp()
        }
    }
}
