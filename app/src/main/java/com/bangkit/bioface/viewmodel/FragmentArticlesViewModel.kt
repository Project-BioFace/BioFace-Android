package com.bangkit.bioface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.ArticlesItem
import com.bangkit.bioface.network.response.ResponseArticlesList
import kotlinx.coroutines.launch
import retrofit2.Response

class FragmentArticlesViewModel : ViewModel() {

    private val _allArticles = MutableLiveData<List<ArticlesItem>>() // Data asli
    private val _filteredArticles = MutableLiveData<List<ArticlesItem>>() // Data hasil filter
    private val _errorMessage = MutableLiveData<String>() // Pesan error
    val articles: LiveData<List<ArticlesItem>> = _filteredArticles // Artikel yang ditampilkan
    val errorMessage: LiveData<String> = _errorMessage // Pesan error

    fun getArticles() {
        viewModelScope.launch {
            try {
                val response: Response<ResponseArticlesList> = ApiClient.apiService.getArticles()
                if (response.isSuccessful && response.body() != null) {
                    val articlesList = response.body()?.data ?: emptyList()
                    if (articlesList.isEmpty()) {
                        _errorMessage.value = "Article not found"
                    } else {
                        _allArticles.value = articlesList
                        _filteredArticles.value = articlesList // Menampilkan artikel setelah diambil dari API
                    }
                } else {
                    _errorMessage.value = "Failed to load articles"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    fun filterArticles(query: String) {
        val articles = _allArticles.value ?: emptyList()
        _filteredArticles.value = if (query.isBlank()) {
            articles // Tampilkan semua artikel jika query kosong
        } else {
            articles.filter { article ->
                article.title?.contains(query, ignoreCase = true) == true ||
                        article.source?.contains(query, ignoreCase = true) == true
            }
        }
    }
}
