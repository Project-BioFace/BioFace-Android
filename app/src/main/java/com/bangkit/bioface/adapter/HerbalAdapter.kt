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
import com.bangkit.bioface.network.response.DictItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class HerbalAdapter(
    private val listener: OnDictClickListener,
    private val onLongClickListener: OnDictLongClickListener? = null
) : ListAdapter<DictItem, HerbalAdapter.DictViewHolder>(DictDiffCallback()) {

    // Interface untuk click listener
    interface OnDictClickListener {
        fun onDictClick(dict: DictItem)
    }

    // Interface untuk long click listener (opsional)
    interface OnDictLongClickListener {
        fun onDictLongClick(Dict: DictItem): Boolean
    }

    // Membuat ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DictViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_herbal, parent, false)
        return DictViewHolder(view)
    }

    // Mengikat data ke ViewHolder
    override fun onBindViewHolder(holder: DictViewHolder, position: Int) {
        val dict = getItem(position)
        holder.bind(dict)

        // Set click listener
        holder.itemView.setOnClickListener {
            dict?.let { listener.onDictClick(it) }
        }

        // Set long click listener jika tersedia
        onLongClickListener?.let { longClickListener ->
            holder.itemView.setOnLongClickListener {
                dict?.let { longClickListener.onDictLongClick(it) } ?: false
            }
        }
    }

    private fun limitWords(text: String?, wordLimit: Int): String {
        if (text.isNullOrBlank()) return "Benefit tidak tersedia"
        val words = text.split(" ")
        return if (words.size <= wordLimit) text else words.take(wordLimit).joinToString(" ") + "..."
    }


    // ViewHolder untuk item artikel
    inner class DictViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Deklarasi view dalam item
        private val imageHerbal: ImageView = itemView.findViewById(R.id.herbalImage)
        private val titleHerbal: TextView = itemView.findViewById(R.id.herbalTitle)
        private val benefitHerbal: TextView = itemView.findViewById(R.id.herbalBenefit)

        // Metode untuk mengikat data ke view
        fun bind(Dict: DictItem) {
            // Set judul artikel
            titleHerbal.text = Dict?.name ?: "Judul Tidak Tersedia"

            // Set sumber artikel
            benefitHerbal.text = "Benefit: ${limitWords(Dict?.benefit, 10)}"

            // Load gambar dengan Glide
            Glide.with(itemView.context)
                .load(Dict?.image)
                .transition(DrawableTransitionOptions.withCrossFade()) // Efek transisi
                .into(imageHerbal)
        }

    }

    // Callback untuk membandingkan item dalam list
    class DictDiffCallback : DiffUtil.ItemCallback<DictItem>() {
        override fun areItemsTheSame(oldItem: DictItem, newItem: DictItem): Boolean {
            // Bandingkan berdasarkan ID
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DictItem, newItem: DictItem): Boolean {
            // Bandingkan seluruh konten item
            return oldItem == newItem
        }
    }
}