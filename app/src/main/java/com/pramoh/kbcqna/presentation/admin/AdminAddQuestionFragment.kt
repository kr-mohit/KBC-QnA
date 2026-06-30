package com.pramoh.kbcqna.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentAdminAddQuestionBinding
import com.pramoh.kbcqna.domain.model.Question
import com.pramoh.kbcqna.presentation.BaseFragment
import com.pramoh.kbcqna.utils.Response
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminAddQuestionFragment: BaseFragment() {

    private lateinit var binding: FragmentAdminAddQuestionBinding
    private val adminViewModel: AdminViewModel by viewModels()

    private val correctOptions = listOf(1, 2, 3, 4)
    private val regions = listOf("GLOBAL", "INDIA")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_add_question, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupObservers()
        setOnClickListeners()

        adminViewModel.loadAllPrizeAmounts()
    }

    private fun setupSpinners() {
        // Correct Option Spinner
        val correctAdapter = ArrayAdapter(requireContext(), R.layout.spinner_selected_item, correctOptions)
        correctAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerCorrectOption.adapter = correctAdapter

        // Region Spinner
        val regionAdapter = ArrayAdapter(requireContext(), R.layout.spinner_selected_item, regions)
        regionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerRegion.adapter = regionAdapter
    }

    private fun setupObservers() {
        adminViewModel.allPrizeAmounts.observe(viewLifecycleOwner) { prizes ->
            val prizeAdapter = ArrayAdapter(requireContext(), R.layout.spinner_selected_item, prizes.map { it.toString() })
            prizeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.spinnerPrizeAmount.adapter = prizeAdapter
        }

        adminViewModel.addQuestionStatus.observe(viewLifecycleOwner) { response ->
            if (response == null) return@observe
            when (response) {
                is Response.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.text = getString(R.string.adding_question_)
                    binding.progressLoading.visibility = View.VISIBLE
                }
                is Response.Success -> {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = getString(R.string.add_question)
                    binding.progressLoading.visibility = View.GONE
                    Toast.makeText(context, getString(R.string.question_added_successfully), Toast.LENGTH_SHORT).show()
                    
                    // Clear fields upon successful insertion
                    clearFields()
                    adminViewModel.resetAddQuestionStatus()
                }
                is Response.Error -> {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.text = getString(R.string.add_question)
                    binding.progressLoading.visibility = View.GONE
                    Toast.makeText(context, response.error ?: "Failed to add question", Toast.LENGTH_SHORT).show()
                    adminViewModel.resetAddQuestionStatus()
                }
            }
        }
    }

    private fun clearFields() {
        binding.etQuestion.text.clear()
        binding.etOption1.text.clear()
        binding.etOption2.text.clear()
        binding.etOption3.text.clear()
        binding.etOption4.text.clear()
        binding.spinnerCorrectOption.setSelection(0)
        binding.spinnerRegion.setSelection(0)
        if (binding.spinnerPrizeAmount.adapter != null && binding.spinnerPrizeAmount.adapter.count > 0) {
            binding.spinnerPrizeAmount.setSelection(0)
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListenerWithSfxAudio {
            findNavController().navigateUp()
        }

        binding.btnSave.setOnClickListenerWithSfxAudio {
            val question = binding.etQuestion.text.toString().trim()
            val option1 = binding.etOption1.text.toString().trim()
            val option2 = binding.etOption2.text.toString().trim()
            val option3 = binding.etOption3.text.toString().trim()
            val option4 = binding.etOption4.text.toString().trim()

            if (question.isEmpty() || option1.isEmpty() || option2.isEmpty() || option3.isEmpty() || option4.isEmpty()) {
                Toast.makeText(context, "Please fill in all question and option fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListenerWithSfxAudio
            }

            val correctOption = binding.spinnerCorrectOption.selectedItem as Int
            val region = binding.spinnerRegion.selectedItem as String

            val prizeStr = binding.spinnerPrizeAmount.selectedItem?.toString()
            if (prizeStr.isNullOrEmpty()) {
                Toast.makeText(context, "Prize amounts not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListenerWithSfxAudio
            }
            val prizeAmount = prizeStr.toIntOrNull() ?: 0

            val questionObj = Question(
                question = question,
                option1 = option1,
                option2 = option2,
                option3 = option3,
                option4 = option4,
                correctOptionNumber = correctOption,
                prizeAmount = prizeAmount,
                region = region
            )

            val message = "Question:\n\"$question\"\n\nOptions:\n1. $option1\n2. $option2\n3. $option3\n4. $option4\n\nCorrect Option: $correctOption\nPrize: $prizeAmount\nRegion: $region"

            showDialog(
                context = requireContext(),
                dialogTitle = "Confirm Upload",
                titleText = message,
                positiveButtonText = "Upload",
                positiveButtonAction = {
                    adminViewModel.addQuestion(questionObj)
                },
                negativeButtonText = "Cancel"
            )
        }
    }
}