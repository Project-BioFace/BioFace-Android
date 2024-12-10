package com.bangkit.bioface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.network.response.SkincareItem
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _herbalList = MutableLiveData<List<DictItem>>()
    val herbalList: LiveData<List<DictItem>> = _herbalList

    private val _skincareList = MutableLiveData<List<SkincareItem>>()
    val skincareList: LiveData<List<SkincareItem>> = _skincareList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        fetchHerbalItems()
        fetchSkincareItems()
    }

    private fun fetchHerbalItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val apiService = ApiClient.apiService2()
                val response = apiService.getDictItems()

                if (response.isSuccessful) {
                    // Limit to 5 items and handle potential null list
                    val herbalItems = response.body()?.data?.shuffled()?.take(5) ?: emptyList()
                    _herbalList.value = herbalItems
                } else {
                    _error.value = "Failed to load herbal items: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchSkincareItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val apiService = ApiClient.apiService2()
                val response = apiService.getSkincareItems()

                if (response.isSuccessful) {
                    val skincareItems = response.body()?.data?.shuffled()?.take(5) ?: emptyList()
                    _skincareList.value = skincareItems

                } else {
                    _error.value = "Failed to load skincare items: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}