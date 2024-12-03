package com.bangkit.bioface.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.databinding.ItemArticleBinding
import com.bangkit.bioface.network.response.ArticlesItem
import com.bumptech.glide.Glide

class ArticleAdapter : ListAdapter<ArticlesItem, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticlesItem) {
            binding.apply {
                // Menggunakan Glide untuk memuat gambar
                Glide.with(itemView)
                    .load(article.image)
                    .into(articleImage) // Pastikan nama id sesuai dengan layout

                articleTitle.text = article.title
                articleSource.text = article.source
            }
        }
    }

    // Membandingkan item lama dan baru
    class ArticleDiffCallback : DiffUtil.ItemCallback<ArticlesItem>() {
        override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem == newItem
        }
    }
}
