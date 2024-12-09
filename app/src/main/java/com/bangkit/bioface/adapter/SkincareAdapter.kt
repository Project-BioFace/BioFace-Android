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

    interface OnSkincareClickListener {
        fun onSkincareClick(skincare: SkincareItem)
    }


    interface OnSkincareLongClickListener {
        fun onSkincareLongClick(skincare: SkincareItem): Boolean
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkincareViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_skincare, parent, false)
        return SkincareViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkincareViewHolder, position: Int) {
        val skincare = getItem(position)

        if (skincare.name == "Not Found Skincare Item") {
            holder.bindNotFound()
        } else {
            holder.bind(skincare)
        }

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


    inner class SkincareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageSkincare: ImageView = itemView.findViewById(R.id.skincareImage)
        private val titleSkincare: TextView = itemView.findViewById(R.id.skincareTitle)
        private val benefitSkincare: TextView = itemView.findViewById(R.id.skincareBenefit)


        fun bindNotFound() {
            titleSkincare.text = "Not Found Skincare Item"
            benefitSkincare.text = "No benefits available"
            imageSkincare.setImageResource(R.drawable.ic_placeholder)
        }



        fun bind(skincare: SkincareItem) {

            if (skincare.name == "Not Found Skincare Item") {
                titleSkincare.text = skincare.name
                benefitSkincare.text = "No benefits available"
                imageSkincare.setImageResource(R.drawable.ic_placeholder)
                itemView.isClickable = false
            } else {
                titleSkincare.text = skincare.name ?: "Judul Tidak Tersedia"

                // Set sumber artikel
                benefitSkincare.text = limitWords(skincare.description, 10)

                // Load gambar dengan Glide
                Glide.with(itemView.context)
                    .load(skincare.image)
                    .transition(DrawableTransitionOptions.withCrossFade())
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