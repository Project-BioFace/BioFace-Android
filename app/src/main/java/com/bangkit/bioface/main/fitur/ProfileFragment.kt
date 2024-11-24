package com.bangkit.bioface

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bangkit.bioface.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profilePicture: CircleImageView
    private lateinit var editIcon: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi UI
        profileName = view.findViewById(R.id.profileName)
        profileEmail = view.findViewById(R.id.profileEmail)
        profilePicture = view.findViewById(R.id.profilePicture)
        editIcon = view.findViewById(R.id.editIcon)

        // Muat data pengguna dan gambar profil
        fetchUserData()

        // Klik untuk mengganti foto profil
        editIcon.setOnClickListener { showImagePickerOptions() }
    }

    private fun fetchUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val profilePicturePath = document.getString("profilePicturePath")

                        // Tampilkan di UI
                        profileName.text = name ?: "Nama Tidak Ditemukan"
                        profileEmail.text = email ?: "Email Tidak Ditemukan"

                        // Muat gambar profil dari penyimpanan internal
                        if (!profilePicturePath.isNullOrEmpty()) {
                            val file = File(profilePicturePath)
                            if (file.exists()) {
                                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                                profilePicture.setImageBitmap(bitmap)
                            } else {
                                Log.e("ProfileFragment", "File gambar tidak ditemukan: $profilePicturePath")
                            }
                        }
                    } else {
                        profileName.text = "Dokumen Tidak Ditemukan"
                        profileEmail.text = "Dokumen Tidak Ditemukan"
                    }
                }
                .addOnFailureListener { exception ->
                    profileName.text = "Error: ${exception.message}"
                    profileEmail.text = "Error: ${exception.message}"
                }
        } else {
            profileName.text = "Pengguna Tidak Login"
            profileEmail.text = "Pengguna Tidak Login"
        }
    }

    private fun showImagePickerOptions() {
        val options = arrayOf("Pilih dari Galeri", "Ambil Foto")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Ganti Foto Profil")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openGallery()
                1 -> openCamera()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickImageIntent, 1000)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 1001)
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg"))
        val options = UCrop.Options()
        options.setCircleDimmedLayer(true) // Membuat crop area melingkar
        options.setShowCropGrid(false) // Hilangkan grid di crop area
        options.setHideBottomControls(true) // Sembunyikan kontrol bawah
        options.setCompressionQuality(90) // Kualitas kompresi gambar

        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f) // Aspect ratio 1:1
            .withOptions(options)
            .start(requireContext(), this)
    }

    private fun updateProfilePicture(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Simpan ke penyimpanan internal
            val savedPath = saveImageToInternalStorage(bitmap)

            if (savedPath != null) {
                // Simpan path ke Firestore
                saveProfilePicturePathToFirestore(savedPath)

                // Tampilkan di UI
                profilePicture.setImageBitmap(bitmap)

                Log.d("ProfileFragment", "Gambar berhasil diperbarui.")
            } else {
                Log.e("ProfileFragment", "Gagal menyimpan gambar ke penyimpanan internal.")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("ProfileFragment", "Error memproses gambar: ${e.message}")
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        val filename = "profile_picture_${System.currentTimeMillis()}.jpg"
        val file = File(requireContext().filesDir, filename)

        return try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun saveProfilePicturePathToFirestore(path: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            val userId = currentUser.uid

            val updates = mapOf("profilePicturePath" to path)

            db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener {
                    Log.d("ProfileFragment", "Path gambar berhasil disimpan ke Firestore.")
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileFragment", "Gagal menyimpan path gambar: ${exception.message}")
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                1000 -> { // Galeri
                    val selectedImageUri = data.data
                    if (selectedImageUri != null) {
                        startCrop(selectedImageUri)
                    } else {
                        Log.e("ProfileFragment", "Uri dari galeri null")
                    }
                }
                1001 -> { // Kamera
                    val bitmap = data.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        val uri = saveBitmapToUri(bitmap)
                        if (uri != null) {
                            startCrop(uri)
                        } else {
                            Log.e("ProfileFragment", "Gagal menyimpan bitmap dari kamera")
                        }
                    } else {
                        Log.e("ProfileFragment", "Bitmap dari kamera null")
                    }
                }
                UCrop.REQUEST_CROP -> { // UCrop selesai
                    val resultUri = UCrop.getOutput(data)
                    if (resultUri != null) {
                        updateProfilePicture(resultUri)
                    } else {
                        Log.e("ProfileFragment", "Hasil cropping null")
                    }
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.printStackTrace()
        }
    }

    private fun saveBitmapToUri(bitmap: Bitmap): Uri? {
        val file = File(requireContext().cacheDir, "temp_image.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.flush()
        }
        return Uri.fromFile(file)
    }
}
