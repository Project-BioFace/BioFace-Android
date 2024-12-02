package com.bangkit.bioface.main.fitur

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bangkit.bioface.R

import com.bangkit.bioface.databinding.FragmentPreviewBinding
import com.bangkit.bioface.main.adapter.ApiClient
import com.bangkit.bioface.main.adapter.ImageRequest
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream

class PreviewFragment : Fragment() {
    private lateinit var binding: FragmentPreviewBinding
    private var capturedImage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil gambar dari argumen
        capturedImage = arguments?.getParcelable(ARG_IMAGE)

        // Tampilkan gambar di ImageView
        binding.imageView.setImageBitmap(capturedImage)

        // Set up click listeners
        binding.retakeButton.setOnClickListener {
            // Kembali ke ScanFragment
            parentFragmentManager.popBackStack() // Kembali ke fragmen sebelumnya
        }

        binding.scanButton.setOnClickListener {
            // Proses gambar menggunakan model AI
            processImage(capturedImage)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun processImage(image: Bitmap?) {
        if (image != null) {
            // Tampilkan ProgressBar dan TextView
            binding.progressBar.visibility = View.VISIBLE
            binding.progressText.visibility = View.VISIBLE
            binding.progressText.text = "Memproses gambar..."

            // Konversi gambar ke base64
            val base64Image = bitmapToBase64(image)

            // Buat permintaan
            val imageRequest = ImageRequest(base64Image)

            // Kirim permintaan ke API
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.uploadImage(imageRequest).execute()
                    if (response.isSuccessful) {
                        // Tangani respons yang berhasil
                        val responseBody = response.body()
                        // Navigasi ke ResultFragment dengan hasil
                        navigateToResultFragment(responseBody)
                    } else {
                        // Tangani kesalahan
                        Toast.makeText(requireContext(), "Gagal mengirim gambar: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    // Sembunyikan ProgressBar
                    binding.progressBar.visibility = View.GONE
                    binding.progressText.visibility = View.GONE
                }
            }
        } else {
            Toast.makeText(requireContext(), "Gambar tidak tersedia untuk diproses", Toast.LENGTH_SHORT).show()
        }
    }


    private fun navigateToResultFragment(image: ResponseBody?) {
        val resultFragment = ResultFragment.newInstance(image) // Ganti dengan hasil pemrosesan
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, resultFragment) // Ganti dengan ID container yang sesuai
            .addToBackStack(null) // Tambahkan ke back stack jika ingin kembali
            .commit()
    }

    companion object {
        private const val ARG_IMAGE = "arg_image"

        fun newInstance(image: Bitmap?): PreviewFragment {
            val fragment = PreviewFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMAGE, image)
            fragment.arguments = args
            return fragment
        }
    }
}