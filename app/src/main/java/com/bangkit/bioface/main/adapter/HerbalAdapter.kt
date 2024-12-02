package com.bangkit.bioface.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.R
import com.bumptech.glide.Glide

class HerbalAdapter(
    private val items: List<HerbalItem>,
    private val onFavoriteClick: (HerbalItem) -> Unit
) : RecyclerView.Adapter<HerbalAdapter.HerbsViewHolder>() {

    inner class HerbsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgHerbal: ImageView = itemView.findViewById(R.id.img_herbal)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val ivFavorite: ImageView = itemView.findViewById(R.id.iv_favorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HerbsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return HerbsViewHolder(view)
    }

    override fun onBindViewHolder(holder: HerbsViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvDescription.text = item.description
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.imgHerbal)

        // Handle favorite icon state
        holder.ivFavorite.setImageResource(
            if (item.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
        holder.ivFavorite.setOnClickListener { onFavoriteClick(item) }
    }

    override fun getItemCount() = items.size
}