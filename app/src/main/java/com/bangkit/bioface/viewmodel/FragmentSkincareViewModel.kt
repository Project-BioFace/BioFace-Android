package com.bangkit.bioface.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.ResponseSkincareList
import com.bangkit.bioface.network.response.SkincareItem
import kotlinx.coroutines.launch
import retrofit2.Response

class FragmentSkincareViewModel : ViewModel() {
    private val _allSkincare = MutableLiveData<List<SkincareItem>>()
    private val _filteredSkincare = MutableLiveData<List<SkincareItem>>()
    private val _errorMessage = MutableLiveData<String>()

    val skincare: LiveData<List<SkincareItem>> = _filteredSkincare
    val errorMessage: LiveData<String> = _errorMessage

    fun getSkincare() {
        viewModelScope.launch {
            try {
                val response: Response<ResponseSkincareList> = ApiClient.apiService.getSkincareItems()
                if (response.isSuccessful && response.body() != null) {
                    val skincareList = response.body()?.data ?: emptyList()
                    if (skincareList.isEmpty()) {
                        _errorMessage.value = "Skincare not found"
                    } else {
                        _allSkincare.value = skincareList
                        _filteredSkincare.value = skincareList
                    }
                } else {
                    _errorMessage.value = "Failed to load Skincare"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    // Tambahkan fungsi untuk melakukan filter
    fun searchSkincare(query: String) {
        val currentList = _allSkincare.value ?: emptyList()
        val filteredList = currentList.filter {
            it.name?.contains(query, ignoreCase = true) == true
        }
        _filteredSkincare.value = filteredList
    }
}
