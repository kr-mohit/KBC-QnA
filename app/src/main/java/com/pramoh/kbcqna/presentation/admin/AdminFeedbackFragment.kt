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
import com.pramoh.kbcqna.databinding.FragmentAdminFeedbackBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminFeedbackFragment : BaseFragment() {

    private lateinit var binding: FragmentAdminFeedbackBinding
    private val adminViewModel: AdminViewModel by viewModels()
    private var adapter: AdminFeedbackAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_admin_feedback, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setOnClickListeners()

        adminViewModel.loadFeedbackConfig()
    }

    private fun setupObservers() {
        adminViewModel.feedbackOptions.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> {
                    binding.progressLoading.visibility = View.VISIBLE
                    binding.rvFeedbackOptions.visibility = View.GONE
                }

                is Response.Success -> {
                    binding.progressLoading.visibility = View.GONE
                    binding.rvFeedbackOptions.visibility = View.VISIBLE
                    response.data?.let { options ->
                        adapter = AdminFeedbackAdapter(options)
                        binding.rvFeedbackOptions.layoutManager =
                            LinearLayoutManager(requireContext())
                        binding.rvFeedbackOptions.adapter = adapter
                    }
                }

                is Response.Error -> {
                    binding.progressLoading.visibility = View.GONE
                    Toast.makeText(
                        context,
                        response.error ?: "Failed to load feedbacks",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        adminViewModel.deleteFeedbackStatus.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            when (response) {
                is Response.Loading -> {
                    binding.btnDelete.isEnabled = false
                    binding.btnDelete.text = getString(R.string.marking_done_)
                }

                is Response.Success -> {
                    binding.btnDelete.isEnabled = true
                    binding.btnDelete.text = getString(R.string.mark_done)
                    Toast.makeText(
                        context,
                        getString(R.string.feedbacks_marked_done_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    adminViewModel.resetDeleteFeedbackStatus()
                    adminViewModel.loadFeedbackConfig()
                }

                is Response.Error -> {
                    binding.btnDelete.isEnabled = true
                    binding.btnDelete.text = getString(R.string.mark_done)
                    Toast.makeText(context, response.error ?: "Action failed", Toast.LENGTH_SHORT)
                        .show()
                    adminViewModel.resetDeleteFeedbackStatus()
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
                Toast.makeText(
                    context,
                    getString(R.string.please_select_at_least_one_feedback),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                adminViewModel.deleteSelectedFeedbacks(selected)
            }
        }
    }
}