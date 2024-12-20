package com.bangkit.bioface.main.fitur

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bangkit.bioface.R
import com.bangkit.bioface.databinding.FragmentScanBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.File

class ScanFragment : Fragment() {
    private lateinit var binding: FragmentScanBinding
    private lateinit var faceDetector: FaceDetector
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private val requestCodeCamera = 1
    private val requestCodeGallery = 2
    private lateinit var imageCapture: ImageCapture

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFaceDetection()
        checkCameraPermission()

        binding.switchCameraButton.setOnClickListener {
            toggleCamera()
        }
        binding.captureButton.setOnClickListener {
            captureImage()
        }
        binding.imageFromGallery.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestCodeGallery)
    }

    @OptIn(ExperimentalGetImage::class)
    private fun captureImage() {
        val file = createImageFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = outputFileResults.savedUri ?: Uri.fromFile(file)
                    navigateToPreviewFragment(uri)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("ScanFragment", "Error capturing image: ${exception.message}")
                    Toast.makeText(requireContext(), "Failed to take picture", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun createImageFile(): File {
        val storageDir = requireContext().getExternalFilesDir(null)
        return File.createTempFile("captured_image_", ".jpg", storageDir)
    }

    private fun navigateToPreviewFragment(imageUri: Uri) {
        val previewFragment = PreviewFragment.newInstance(imageUri)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, previewFragment)
            .addToBackStack(null)
            .commit()
    }


    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY) // Atur mode untuk kualitas maksimal
                .setTargetAspectRatio(AspectRatio.RATIO_16_9) // Atur rasio aspek
                .build() // Hapus setTargetResolution

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )
                    faceDetector.process(inputImage)
                        .addOnSuccessListener { faces ->
                            if (faces.isNotEmpty()) {
                                binding.instructionText.text = "Face detected! 😊"
                                binding.instructionText.setBackgroundResource(R.drawable.text_background_success)
                            } else {
                                binding.instructionText.text = "Place your face into the box"
                                binding.instructionText.setBackgroundResource(R.drawable.text_background)
                            }
                        }
                        .addOnCompleteListener {
                            imageProxy.close()
                        }
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageAnalysis, imageCapture)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ScanFragment", "Error starting camera", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun setupFaceDetection() {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .build()

        faceDetector = FaceDetection.getClient(options)
    }

    private fun toggleCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }
        startCamera()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.CAMERA), requestCodeCamera
            )
        } else {
            startCamera()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeGallery && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                navigateToPreviewFragment(uri)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCamera) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required for this app", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
