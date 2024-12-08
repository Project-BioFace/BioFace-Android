package com.bangkit.bioface.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.R
import com.bangkit.bioface.network.response.PredictionHistory

class HistoryAdapter(
    private var predictions: List<PredictionHistory>,
    private val onItemClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val prediction = predictions[position]
        holder.bind(prediction)

        // Set click listener untuk item
        holder.itemView.setOnClickListener {
            onItemClick(prediction.id) // Panggil callback dengan ID
        }

        // Set click listener untuk delete icon
        holder.itemView.findViewById<ImageView>(R.id.icDelete).setOnClickListener {
            onDeleteClick(prediction.id) // Panggil callback untuk delete
        }
    }

    override fun getItemCount(): Int = predictions.size

    fun updateData(newPredictions: List<PredictionHistory>) {
        predictions = newPredictions
        notifyDataSetChanged() // Memperbarui tampilan
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(prediction: PredictionHistory) {
            itemView.findViewById<TextView>(R.id.faceDiseaseText).text = prediction.faceDisease
            itemView.findViewById<TextView>(R.id.timestampText).text = prediction.timestamp
        }
    }
}


