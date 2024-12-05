package com.bangkit.bioface.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.R
import com.bangkit.bioface.network.response.ArticlesItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class ArticleAdapter(
    private val listener: OnArticleClickListener,
    private val onLongClickListener: OnArticleLongClickListener? = null
) : ListAdapter<ArticlesItem, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    // Interface untuk click listener
    interface OnArticleClickListener {
        fun onArticleClick(article: ArticlesItem)
    }

    // Interface untuk long click listener (opsional)
    interface OnArticleLongClickListener {
        fun onArticleLongClick(article: ArticlesItem): Boolean
    }

    // Membuat ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    // Mengikat data ke ViewHolder
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)

        // Set click listener
        holder.itemView.setOnClickListener {
            article?.let { listener.onArticleClick(it) }
        }

        // Set long click listener jika tersedia
        onLongClickListener?.let { longClickListener ->
            holder.itemView.setOnLongClickListener {
                article?.let { longClickListener.onArticleLongClick(it) } ?: false
            }
        }
    }

    // ViewHolder untuk item artikel
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Deklarasi view dalam item
        private val imageArticle: ImageView = itemView.findViewById(R.id.articleImage)
        private val titleArticle: TextView = itemView.findViewById(R.id.articleTitle)
        private val sourceArticle: TextView = itemView.findViewById(R.id.articleSource)

        // Metode untuk mengikat data ke view
        fun bind(article: ArticlesItem?) {
            // Set judul artikel
            titleArticle.text = article?.title ?: "Judul Tidak Tersedia"

            // Set sumber artikel
            sourceArticle.text = "Sumber: ${article?.source ?: "Tidak Diketahui"}"

            // Load gambar dengan Glide
            Glide.with(itemView.context)
                .load(article?.image)
                .transition(DrawableTransitionOptions.withCrossFade()) // Efek transisi
                .into(imageArticle)
        }

        // Metode untuk memformat tanggal (bisa disesuaikan)
        private fun formatDate(dateString: String?): String {
            return try {
                // Contoh sederhana, bisa diganti dengan formatter yang lebih kompleks
                dateString?.substring(0, 10) ?: "Tanggal Tidak Tersedia"
            } catch (e: Exception) {
                "Tanggal Tidak Tersedia"
            }
        }
    }

    // Callback untuk membandingkan item dalam list
    class ArticleDiffCallback : DiffUtil.ItemCallback<ArticlesItem>() {
        override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            // Bandingkan berdasarkan ID
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            // Bandingkan seluruh konten item
            return oldItem == newItem
        }
    }

    // Metode untuk memfilter list artikel
    fun filterArticles(query: String) {
        val filteredList = currentList.filter { article ->
            article.title?.contains(query, ignoreCase = true) == true ||
                    article.source?.contains(query, ignoreCase = true) == true
        }
        submitList(filteredList)
    }

    // Metode untuk mendapatkan total artikel
    fun getArticleCount(): Int {
        return currentList.size
    }
}