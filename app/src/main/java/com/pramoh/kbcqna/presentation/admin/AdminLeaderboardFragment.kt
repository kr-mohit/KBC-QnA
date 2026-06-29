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
import com.pramoh.kbcqna.databinding.FragmentAdminLeaderboardBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminLeaderboardFragment: BaseFragment() {

    private lateinit var binding: FragmentAdminLeaderboardBinding
    private val adminViewModel: AdminViewModel by viewModels()
    private var adapter: AdminLeaderboardAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_leaderboard, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setOnClickListeners()

        adminViewModel.loadLeaderboardConfig()
    }

    private fun setupObservers() {
        adminViewModel.leaderboardOptions.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.progressLoading.visibility = View.VISIBLE
                    binding.rvPlayerOptions.visibility = View.GONE
                }
                is Response.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    binding.rvPlayerOptions.visibility = View.VISIBLE
                    response.data?.let { options ->
                        adapter = AdminLeaderboardAdapter(options)
                        binding.rvPlayerOptions.layoutManager = LinearLayoutManager(requireContext())
                        binding.rvPlayerOptions.adapter = adapter
                    }
                }
                is Response.Error -> {
                    binding.progressLoading.visibility = View.GONE
                    Toast.makeText(context, response.error ?: "Failed to load leaderboard", Toast.LENGTH_SHORT).show()
                }
            }
        }

        adminViewModel.deleteLeaderboardStatus.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            when (response) {
                is Response.Loading -> {
                    binding.btnDelete.isEnabled = false
                    binding.btnDelete.text = "Deleting Selected..."
                }
                is Response.Success -> {
                    binding.btnDelete.isEnabled = true
                    binding.btnDelete.text = "Delete Selected"
                    Toast.makeText(context, "Entrants deleted successfully!", Toast.LENGTH_SHORT).show()
                    adminViewModel.resetDeleteLeaderboardStatus()
                    adminViewModel.loadLeaderboardConfig()
                }
                is Response.Error -> {
                    binding.btnDelete.isEnabled = true
                    binding.btnDelete.text = "Delete Selected"
                    Toast.makeText(context, response.error ?: "Deletion failed", Toast.LENGTH_SHORT).show()
                    adminViewModel.resetDeleteLeaderboardStatus()
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListenerWithSfxAudio {
            findNavController().navigateUp()
        }

        binding.btnSelectAll.setOnClickListenerWithSfxAudio {
            adapter?.selectAll()
        }

        binding.btnUnselectAll.setOnClickListenerWithSfxAudio {
            adapter?.unselectAll()
        }

        binding.btnDelete.setOnClickListenerWithSfxAudio {
            val selected = adapter?.getSelectedDocIds()
            if (selected.isNullOrEmpty()) {
                Toast.makeText(context, "Please select at least one entrant to delete", Toast.LENGTH_SHORT).show()
            } else {
                adminViewModel.deleteSelectedPlayers(selected)
            }
        }
    }
}
