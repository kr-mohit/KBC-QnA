package com.pramoh.kbcqna.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.pramoh.kbcqna.R
import com.pramoh.kbcqna.databinding.FragmentPopUpWindowBinding

class PopUpWindowFragment(
    private val popupText: String,
    private val popupButton1Text: String,
    private val popupButton2Text: String,
    private val prizeMoney: String

) : DialogFragment() {

    private lateinit var binding: FragmentPopUpWindowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pop_up_window, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
        setOnClickListeners()
    }

    private fun setUI() {
        binding.tvPopupWindowText.text = popupText
        binding.btnPopupWindowButton1.text = popupButton1Text
        binding.btnPopupWindowButton2.text = popupButton2Text

        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        }
        // TODO: hide system bars
    }

    private fun setOnClickListeners() {
        binding.btnPopupWindowButton1.setOnClickListener {
            // TODO: open result fragment
            Toast.makeText(context, "Feature Coming Soon", Toast.LENGTH_SHORT).show()
//            findNavController().navigate(PopUpWindowFragmentDirections.actionPopUpWindowFragment3ToResultFragment(false, prizeMoney))
        }
        binding.btnPopupWindowButton2.setOnClickListener {
            this.dismiss()
        }
    }

}