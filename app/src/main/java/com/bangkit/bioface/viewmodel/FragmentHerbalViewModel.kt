package com.bangkit.bioface.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.DictItem
import com.bangkit.bioface.network.response.ResponseDictList
import kotlinx.coroutines.launch
import retrofit2.Response

class FragmentHerbalViewModel : ViewModel() {
    private val _allHerbal = MutableLiveData<List<DictItem>>()
    private val _filteredHerbal = MutableLiveData<List<DictItem>>()
    private val _errorMessage = MutableLiveData<String>()

    val herbal: LiveData<List<DictItem>> = _filteredHerbal
    val errorMessage: LiveData<String> = _errorMessage

    fun getHerbal() {
        viewModelScope.launch {
            try {
                val response: Response<ResponseDictList> = ApiClient.apiService.getDictItems()
                Log.d("HerbalViewModel", "Response code: ${response.code()}")
                Log.d("HerbalViewModel", "Response body: ${response.body()}")
                Log.d("HerbalViewModel", "Response error: ${response.errorBody()?.string()}")
                if (response.isSuccessful && response.body() != null) {
                    val herbalList = response.body()?.data ?: emptyList()
                    if (herbalList.isEmpty()) {
                        _errorMessage.value = "Herbal not found"
                    } else {
                        _allHerbal.value = herbalList
                        _filteredHerbal.value = herbalList
                    }
                } else {
                    _errorMessage.value = "Failed to load Herbal"
                }
            } catch (e: Exception) {
                Log.e("HerbalViewModel", "Error: ${e.localizedMessage}", e)
                _errorMessage.value = "Error: ${e.localizedMessage}"
            }
        }
    }

    // Tambahkan fungsi untuk melakukan filter
    fun searchHerbal(query: String) {
        val currentList = _allHerbal.value ?: emptyList()
        val filteredList = currentList.filter {
            it.name?.contains(query, ignoreCase = true) == true
        }
        _filteredHerbal.value = filteredList
    }
}
