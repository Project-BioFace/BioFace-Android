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

    private val _allArticles = MutableLiveData<List<ArticlesItem>>()
    private val _filteredArticles = MutableLiveData<List<ArticlesItem>>()
    private val _isLoading = MutableLiveData<Boolean>()
    val articles: LiveData<List<ArticlesItem>> = _filteredArticles
    val isLoading: LiveData<Boolean> = _isLoading

    fun getArticles() {
        _isLoading.value = true // Menampilkan ProgressBar
        viewModelScope.launch {
            try {
                val response: Response<ResponseArticlesList> = ApiClient.apiService.getArticles()
                if (response.isSuccessful && response.body() != null) {
                    val articlesList = response.body()?.data ?: emptyList()
                    _allArticles.value = articlesList
                    _filteredArticles.value = articlesList
                }
            } catch (e: Exception) {
                // Log atau atur error handling
            } finally {
                _isLoading.value = false // Sembunyikan ProgressBar
            }
        }
    }

    fun filterArticles(query: String) {
        val articles = _allArticles.value ?: emptyList()
        _filteredArticles.value = if (query.isBlank()) {
            articles
        } else {
            articles.filter { article ->
                article.title?.contains(query, ignoreCase = true) == true ||
                        article.source?.contains(query, ignoreCase = true) == true
            }
        }
    }
}
