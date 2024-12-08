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

    override fun onBindViewHolder(holder: SkincareViewHolder, position: Int) {
        val skincare = getItem(position)

        // Tangani item khusus untuk "Not Found Skincare Item"
        if (skincare.name == "Not Found Skincare Item") {
            holder.bindNotFound()
        } else {
            holder.bind(skincare)
        }

        // Nonaktifkan klik jika item adalah dummy
        if (skincare.name == "Not Found Skincare Item") {
            holder.itemView.setOnClickListener(null)
            holder.itemView.setOnLongClickListener(null)
        } else {
            holder.itemView.setOnClickListener {
                skincare?.let { listener.onSkincareClick(it) }
            }
            onLongClickListener?.let { longClickListener ->
                holder.itemView.setOnLongClickListener {
                    skincare?.let { longClickListener.onSkincareLongClick(it) } ?: false
                }
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


        fun bindNotFound() {
            titleSkincare.text = "Not Found Skincare Item"
            benefitSkincare.text = "No benefits available"
            imageSkincare.setImageResource(R.drawable.ic_placeholder) // Gambar placeholder
        }


        // Metode untuk mengikat data ke view
        fun bind(skincare: SkincareItem) {
            // Jika item memiliki nama "Not Found Skincare Item", tampilkan pesan khusus
            if (skincare.name == "Not Found Skincare Item") {
                titleSkincare.text = skincare.name
                benefitSkincare.text = "No benefits available"
                imageSkincare.setImageResource(R.drawable.ic_placeholder) // Gambar placeholder
                itemView.isClickable = false // Nonaktifkan klik untuk item ini
            } else {
                // Set judul artikel
                titleSkincare.text = skincare.name ?: "Judul Tidak Tersedia"

                // Set sumber artikel
                benefitSkincare.text = limitWords(skincare.description, 10)

                // Load gambar dengan Glide
                Glide.with(itemView.context)
                    .load(skincare.image)
                    .transition(DrawableTransitionOptions.withCrossFade()) // Efek transisi
                    .into(imageSkincare)
            }
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