package com.bangkit.bioface.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.R
import com.bangkit.bioface.network.response.HerbalSolution
import com.bumptech.glide.Glide

class HerbalSolutionAdapter(private val solutions: List<HerbalSolution>) : RecyclerView.Adapter<HerbalSolutionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.herbalSolutionName)
        val benefitTextView: TextView = itemView.findViewById(R.id.herbalSolutionBenefit)
        val usageTextView: TextView = itemView.findViewById(R.id.herbalSolutionUsage)
        val imageView: ImageView = itemView.findViewById(R.id.herbalSolutionImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_herbal_solution, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val solution = solutions[position]
        holder.nameTextView.text = solution.name
        holder.benefitTextView.text = solution.benefit
        holder.usageTextView.text = solution.usage
        // Load image using a library like Glide or Picasso
        Glide.with(holder.itemView.context).load(solution.imageUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int = solutions.size
}
