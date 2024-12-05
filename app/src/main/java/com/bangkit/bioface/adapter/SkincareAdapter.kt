package com.bangkit.bioface.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.R
import com.bangkit.bioface.network.response.SkincareItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class SkincareAdapter(
    private val listener: OnSkincareClickListener,
    private val onLongClickListener: OnSkincareLongClickListener? = null
) : ListAdapter<SkincareItem, SkincareAdapter.SkincareViewHolder>(SkincareDiffCallback()) {

    // Interface untuk click listener
    interface OnSkincareClickListener {
        fun onSkincareClick(skincare: SkincareItem)
    }

    // Interface untuk long click listener (opsional)
    interface OnSkincareLongClickListener {
        fun onSkincareLongClick(skincare: SkincareItem): Boolean
    }

    // Membuat ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkincareViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skincare, parent, false)
        return SkincareViewHolder(view)
    }

    // Mengikat data ke ViewHolder
    override fun onBindViewHolder(holder: SkincareViewHolder, position: Int) {
        val skincare = getItem(position)
        holder.bind(skincare)

        // Set click listener
        holder.itemView.setOnClickListener {
            skincare?.let { listener.onSkincareClick(it) }
        }

        // Set long click listener jika tersedia
        onLongClickListener?.let { longClickListener ->
            holder.itemView.setOnLongClickListener {
                skincare?.let { longClickListener.onSkincareLongClick(it) } ?: false
            }
        }
    }

    private fun limitWords(text: String?, wordLimit: Int): String {
        if (text.isNullOrBlank()) return "Benefit tidak tersedia"
        val words = text.split(" ")
        return if (words.size <= wordLimit) text else words.take(wordLimit).joinToString(" ") + "..."
    }

    // ViewHolder untuk item artikel
    inner class SkincareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Deklarasi view dalam item
        private val imageSkincare: ImageView = itemView.findViewById(R.id.skincareImage)
        private val titleSkincare: TextView = itemView.findViewById(R.id.skincareTitle)
        private val benefitSkincare: TextView = itemView.findViewById(R.id.skincareBenefit)

        // Metode untuk mengikat data ke view
        fun bind(Skincare: SkincareItem) {
            // Set judul artikel
            titleSkincare.text = Skincare?.name ?: "Judul Tidak Tersedia"

            // Set sumber artikel
            benefitSkincare.text = "Benefit: ${limitWords(Skincare?.benefit, 10)}"

            // Load gambar dengan Glide
            Glide.with(itemView.context)
                .load(Skincare?.image)
                .transition(DrawableTransitionOptions.withCrossFade()) // Efek transisi
                .into(imageSkincare)
        }

    }

    // Callback untuk membandingkan item dalam list
    class SkincareDiffCallback : DiffUtil.ItemCallback<SkincareItem>() {
        override fun areItemsTheSame(oldItem: SkincareItem, newItem: SkincareItem): Boolean {
            // Bandingkan berdasarkan ID
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SkincareItem, newItem: SkincareItem): Boolean {
            // Bandingkan seluruh konten item
            return oldItem == newItem
        }
    }
}