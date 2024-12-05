package com.bangkit.bioface.main.fitur

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bangkit.bioface.R
import com.bangkit.bioface.databinding.FragmentPreviewBinding
import com.bangkit.bioface.network.api.ApiClient
import com.bangkit.bioface.network.response.PredictionResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class PreviewFragment : Fragment() {
    private lateinit var binding: FragmentPreviewBinding
    private var capturedImage: Bitmap? = null
    private var currentImageUri: Uri? = null // Menyimpan URI gambar

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
        currentImageUri = arguments?.getParcelable(ARG_IMAGE_URI) // Ambil URI jika ada

        // Tampilkan gambar di ImageView
        binding.imageView.setImageBitmap(capturedImage)

        // Set up click listeners
        binding.retakeButton.setOnClickListener {
            // Kembali ke ScanFragment
            parentFragmentManager.popBackStack() // Kembali ke fragmen sebelumnya
        }

        binding.scanButton.setOnClickListener {
            // Proses gambar menggunakan model AI
            processImage(currentImageUri)
        }
    }

    private fun processImage(imageUri: Uri?) {
        if (imageUri != null) {
            // Tampilkan ProgressBar dan TextView
            binding.progressBar.visibility = View.VISIBLE
            binding.progressText.visibility = View.VISIBLE
            binding.progressText.text = "Memproses gambar..."

            // Konversi URI gambar ke file
            val imageFile = uriToFile(imageUri, requireContext()).reduceFileImage()

            // Tambahkan log untuk memeriksa file gambar
            Log.d("PreviewFragment", "Image File Path: ${imageFile.absolutePath}, Size: ${imageFile.length()} bytes")

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("image", imageFile.name, requestImageFile)


            // Mendapatkan token dari pengguna yang sedang login
            FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result?.token
                        // Kirim permintaan ke API dengan token
                        sendImageToApi(token, multipartBody)
                    } else {
                        // Tangani kesalahan jika tidak dapat mendapatkan token
                        Log.e("Auth", "Failed to get token: ${task.exception?.message}")
                        Toast.makeText(requireContext(), "Gagal mendapatkan token", Toast.LENGTH_SHORT).show()
                        // Sembunyikan ProgressBar
                        binding.progressBar.visibility = View.GONE
                        binding.progressText.visibility = View.GONE
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Gambar tidak tersedia untuk diproses", Toast.LENGTH_SHORT).show()
        }
    }


    private fun sendImageToApi(token: String?, multipartBody: MultipartBody.Part) {
        if (token != null) {
            lifecycleScope.launch {
                try {
                    val apiService = ApiClient.apiService()
                    val predictionResponse = apiService.uploadImage("Bearer $token", multipartBody)

                    // Navigasi ke ResultFragment dengan hasil
                    navigateToResultFragment(predictionResponse)
                } catch (e: HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    Log.e("PreviewFragment", "Error response: $errorResponse")
                    Toast.makeText(requireContext(), "Error: $errorResponse", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Token tidak valid", Toast.LENGTH_SHORT).show()
        }
    }





    private fun navigateToResultFragment(predictionResponse: PredictionResponse) {
        val resultFragment = ResultFragment.newInstance(capturedImage, predictionResponse)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, resultFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile("temp_image", ".jpg", filesDir)
    }

    private fun File.reduceFileImage(): File {
        val file = this
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        val MAXIMAL_SIZE = 1000000 // 1 MB
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    companion object {
        private const val ARG_IMAGE = "arg_image"
        private const val ARG_IMAGE_URI = "arg_image_uri" // Tambahkan argumen untuk URI

        fun newInstance(image: Bitmap?, imageUri: Uri?): PreviewFragment {
            val fragment = PreviewFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMAGE, image)
            args.putParcelable(ARG_IMAGE_URI, imageUri) // Simpan URI
            fragment.arguments = args
            return fragment
        }
    }
}
