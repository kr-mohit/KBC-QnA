package com.pramoh.kbcqna.presentation.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentFeedbackBinding
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackFragment : BaseFragment() {

    private lateinit var binding: FragmentFeedbackBinding
    private val viewModel: FeedbackViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_feedback, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        setupObservers()
        setOnClickListeners()
    }

    private fun setupSpinner() {
        val feedbackTypes = listOf(
            getString(R.string.feedback),
            getString(R.string.bug_report)
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_selected_item, feedbackTypes)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerFeedbackType.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.submitResult.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            when (response) {
                is Response.Loading -> {
                    binding.progressLoading.show()
                    disableAllButtonsClick()
                }

                is Response.Success -> {
                    binding.progressLoading.hide()
                    enableAllButtonsClick()
                    Toast.makeText(
                        context,
                        getString(R.string.feedback_submitted_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.resetSubmitResult()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }

                is Response.Error -> {
                    binding.progressLoading.hide()
                    enableAllButtonsClick()
                    Toast.makeText(
                        context,
                        response.error ?: "Submission failed",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.resetSubmitResult()
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListenerWithSfxAudio {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSubmit.setOnClickListenerWithSfxAudio {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val type = binding.spinnerFeedbackType.selectedItem.toString()
            val message = binding.etMessage.text.toString().trim()

            if (message.isEmpty()) {
                Toast.makeText(
                    context,
                    getString(R.string.please_enter_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.submitFeedback(name, email, type, message)
            }
        }
    }

    private fun disableAllButtonsClick() {
        binding.btnSubmit.isClickable = false
        binding.btnBack.isClickable = false
    }

    private fun enableAllButtonsClick() {
        binding.btnSubmit.isClickable = true
        binding.btnBack.isClickable = true
    }
}