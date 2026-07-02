package com.pramoh.kbcqna.presentation.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pramoh.kbcqna.databinding.ItemAdminFeedbackBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminFeedbackAdapter(
    private val options: List<AdminFeedbackOption>
) : RecyclerView.Adapter<AdminFeedbackAdapter.FeedbackViewHolder>() {

    class FeedbackViewHolder(val binding: ItemAdminFeedbackBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val binding =
            ItemAdminFeedbackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedbackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val option = options[position]

        holder.binding.tvType.text = option.type.uppercase(Locale.getDefault())

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.binding.tvDate.text = dateFormat.format(Date(option.timestamp))

        val authorText = when {
            option.name.isNotEmpty() && option.email.isNotEmpty() -> "${option.name} (${option.email})"
            option.name.isNotEmpty() -> option.name
            option.email.isNotEmpty() -> option.email
            else -> "Anonymous"
        }
        holder.binding.tvAuthor.text = "From: $authorText"
        holder.binding.tvMessage.text = option.message

        holder.binding.cbFeedback.setOnCheckedChangeListener(null)
        holder.binding.cbFeedback.isChecked = option.isSelected

        holder.binding.cbFeedback.setOnCheckedChangeListener { _, isChecked ->
            option.isSelected = isChecked
        }
    }

    override fun getItemCount(): Int = options.size

    fun getSelectedDocIds(): List<String> {
        return options.filter { it.isSelected }.map { it.docId }
    }

    fun selectAll() {
        options.forEach { it.isSelected = true }
        notifyDataSetChanged()
    }

    fun unselectAll() {
        options.forEach { it.isSelected = false }
        notifyDataSetChanged()
    }
}