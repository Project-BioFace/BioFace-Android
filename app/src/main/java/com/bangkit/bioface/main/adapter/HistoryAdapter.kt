package com.bangkit.bioface.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val scanResults: List<ResultItem>,
    private val onItemClick: (ResultItem) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.item_image)
        private val conditionTextView: TextView = itemView.findViewById(R.id.item_condition)
        private val recommendationTextView: TextView = itemView.findViewById(R.id.item_recommendation)
        private val timestampTextView: TextView = itemView.findViewById(R.id.item_timestamp)

        fun bind(scanResult: ResultItem) {
            imageView.setImageBitmap(scanResult.image)
            conditionTextView.text = scanResult.condition
            recommendationTextView.text = scanResult.recommendation
            timestampTextView.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
                Date(scanResult.timestamp)
            )

            itemView.setOnClickListener {
                onItemClick(scanResult)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(scanResults[position])
    }

    override fun getItemCount(): Int = scanResults.size
}