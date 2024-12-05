package com.bangkit.bioface.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.R
import com.bangkit.bioface.network.response.SkincareProduct
import com.bumptech.glide.Glide

class SkincareProductAdapter(private val products: List<SkincareProduct>) : RecyclerView.Adapter<SkincareProductAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.skincareProductName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.skincareProductDescription)
        val imageView: ImageView = itemView.findViewById(R.id.skincareProductImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_skincare_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.nameTextView.text = product.name
        // Jika ada deskripsi, Anda bisa menambahkannya di sini
        holder.descriptionTextView.text = "Deskripsi produk" // Ganti dengan deskripsi jika ada
        // Load image using a library like Glide or Picasso
        Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.imageView)
    }

    override fun getItemCount(): Int = products.size
}
