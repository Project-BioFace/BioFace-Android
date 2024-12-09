package com.bangkit.bioface.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.ResponseSkincareDetail
import com.bangkit.bioface.network.response.SkincareItem
import kotlinx.coroutines.launch
import retrofit2.Response

class DetailSkincareViewModel : ViewModel() {

    // LiveData untuk menyimpan skincare yang diterima
    private val _skincare = MutableLiveData<SkincareItem?>()
    val skincare: LiveData<SkincareItem?> get() = _skincare

    // LiveData untuk status loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData untuk error message
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Fungsi untuk mendapatkan skincare berdasarkan ID
    fun getSkincareById(id: Int) {
        _isLoading.value = true // Menandakan bahwa data sedang dimuat
        viewModelScope.launch {
            try {
                // Memanggil API untuk mendapatkan skincare berdasarkan ID
                val response: Response<ResponseSkincareDetail> = ApiClient.apiService.getSkincareItemById(id)

                // Mengecek apakah response berhasil dan body tidak null
                if (response.isSuccessful && response.body() != null) {
                    val skincare = response.body()?.data // Ambil data skincare

                    // Jika skincare tidak null, set skincare ke LiveData
                    if (skincare != null) {
                        _skincare.value = skincare
                    } else {
                        _error.value = "Skincare data is null"
                    }

                    _isLoading.value = false // Mengubah status loading menjadi false
                    Log.d("DetailSkincareViewModel", "Skincare data received: $skincare")
                } else {
                    // Jika response tidak berhasil, beri pesan error
                    _error.value = "Failed to load Detail Skincare"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                // Menangani kesalahan jika ada
                _error.value = "Error: ${e.message}"
                _isLoading.value = false
                Log.e("DetailSkincareViewModel", "Error: ${e.message}")
            }
        }
    }
}
