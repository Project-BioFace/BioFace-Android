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
    private var currentImageUri: Uri? = null

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

        currentImageUri = arguments?.getParcelable(ARG_IMAGE_URI)

        // Tampilkan gambar di ImageView
        currentImageUri?.let { uri ->
            binding.imageView.setImageURI(uri)
        }

        // Set up click listeners
        binding.retakeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.scanButton.setOnClickListener {
            processImage(currentImageUri)
        }
    }

    private fun processImage(imageUri: Uri?) {
        if (imageUri != null) {
            binding.progressBar.visibility = View.VISIBLE
            binding.progressText.visibility = View.VISIBLE
            binding.progressText.text = "Processing images..."

            val imageFile = uriToFile(imageUri, requireContext()).reduceFileImage()

            Log.d("PreviewFragment", "Image File Path: ${imageFile.absolutePath}, Size: ${imageFile.length()} bytes")

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData("image", imageFile.name, requestImageFile)

            FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result?.token
                        sendImageToApi(token, multipartBody)
                    } else {
                        Log.e("Auth", "Failed to get token: ${task.exception?.message}")
                        Toast.makeText(requireContext(), "Failed to get token", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                        binding.progressText.visibility = View.GONE
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Image not available for processing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendImageToApi(token: String?, multipartBody: MultipartBody.Part) {
        if (token != null) {
            lifecycleScope.launch {
                try {
                    val apiService = ApiClient.apiService1()
                    val predictionResponse = apiService.uploadImage("Bearer $token", multipartBody)

                    navigateToResultFragment(predictionResponse)
                } catch (e: HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    Log.e("PreviewFragment", "Error response: $errorResponse")
                    Toast.makeText(requireContext(), "Error: $errorResponse", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Something went wrong: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    binding.progressBar.visibility = View.GONE
                    binding.progressText.visibility = View.GONE
                }
            }
        } else {
            Toast.makeText(requireContext(), "Failed to get token", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToResultFragment(predictionResponse: PredictionResponse) {
        val resultFragment = ResultFragment.newInstance(currentImageUri, predictionResponse)
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
        private const val ARG_IMAGE_URI = "arg_image_uri"

        fun newInstance(imageUri: Uri): PreviewFragment {
            val fragment = PreviewFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMAGE_URI, imageUri)
            fragment.arguments = args
            return fragment
        }
    }
}
