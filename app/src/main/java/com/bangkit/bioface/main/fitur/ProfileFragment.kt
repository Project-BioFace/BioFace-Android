package com.bangkit.bioface.main.fitur

import android.app.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.bioface.R
import com.bangkit.bioface.main.LandingActivity
import com.bangkit.bioface.main.adapter.HistoryAdapter
import com.bangkit.bioface.main.fitur.detail.DetailHistoryFragment
import com.bangkit.bioface.viewmodel.HistoryViewModel
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profilePicture: CircleImageView
    private lateinit var editIcon: ImageView
    private lateinit var btnLogout: Button
    private lateinit var shimmerNameLayout: ShimmerFrameLayout
    private lateinit var shimmerEmailLayout: ShimmerFrameLayout
    private lateinit var shimmerProfilePictureLayout: ShimmerFrameLayout

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var noHistoryTextView: TextView

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val GALLERY_PERMISSION_CODE = 101
        private const val GALLERY_REQUEST_CODE = 1000
        private const val CAMERA_REQUEST_CODE = 1001
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Inisialisasi RecyclerView dan TextView
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView)
        noHistoryTextView = view.findViewById(R.id.noHistoryTextView)

        // Inisialisasi ViewModel
        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        // Setup RecyclerView
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = HistoryAdapter(emptyList(), { predictionId ->
            // Handle item click
            val detailFragment = DetailHistoryFragment.newInstance(predictionId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }, { predictionId ->
            // Handle delete click
            historyViewModel.deleteHistory(predictionId, {
                // Callback onSuccess
                Toast.makeText(requireContext(), "History dengan ID $predictionId telah dihapus", Toast.LENGTH_SHORT).show()
                historyViewModel.fetchHistory() // Memperbarui daftar history setelah dihapus
            }, { errorMessage ->
                // Callback onFailure
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            })
        })

        historyRecyclerView.adapter = historyAdapter

        historyViewModel.historyList.observe(viewLifecycleOwner) { predictions ->
            if (predictions.isEmpty()) {
                noHistoryTextView.visibility = View.VISIBLE
                historyRecyclerView.visibility = View.GONE
            } else {
                noHistoryTextView.visibility = View.GONE
                historyRecyclerView.visibility = View.VISIBLE
                historyAdapter.updateData(predictions) // Perbarui data adapter
            }
        }

        // Memanggil fetchHistory untuk mendapatkan data awal
        historyViewModel.fetchHistory()

        // Tambahkan listener untuk menghapus semua history
        view.findViewById<TextView>(R.id.tvHapusHistory).setOnClickListener {
            historyViewModel.deleteAllHistory({
                Toast.makeText(requireContext(), "Semua history telah dihapus", Toast.LENGTH_SHORT).show()
                // Panggil fetchHistory untuk memperbarui tampilan setelah penghapusan
                historyViewModel.fetchHistory()
            }, { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            })
        }



        // Initialize UI components
        profileName = view.findViewById(R.id.profileName)
        profileEmail = view.findViewById(R.id.profileEmail)
        profilePicture = view.findViewById(R.id.profilePicture)
        editIcon = view.findViewById(R.id.editIcon)
        btnLogout = view.findViewById(R.id.btnLogout)

        shimmerNameLayout = view.findViewById(R.id.shimmerNameLayout)
        shimmerEmailLayout = view.findViewById(R.id.shimmerEmailLayout)
        shimmerProfilePictureLayout = view.findViewById(R.id.shimmerProfilePictureLayout)

        // Show shimmer loading effects initially
        shimmerNameLayout.startShimmer()
        shimmerEmailLayout.startShimmer()
        shimmerProfilePictureLayout.startShimmer()

        btnLogout.setOnClickListener { logout() }

        // Load user data and profile picture
        fetchUserData()

        // Click to change profile picture
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

                        // Display the fetched data
                        profileName.text = name ?: "Nama Tidak Ditemukan"
                        profileEmail.text = email ?: "Email Tidak Ditemukan"

                        // Hide shimmer and show the profile content
                        stopShimmerEffect()

                        // Display profile content
                        profilePicture.visibility = View.VISIBLE
                        editIcon.visibility = View.VISIBLE
                        profileName.visibility = View.VISIBLE
                        profileEmail.visibility = View.VISIBLE

                        // Load profile picture
                        loadProfilePicture()
                    } else {
                        handleDataLoadError("Dokumen Tidak Ditemukan")
                    }
                }
                .addOnFailureListener { exception ->
                    handleDataLoadError("Error: ${exception.message}")
                }
        } else {
            handleDataLoadError("Pengguna Tidak Login")
        }
    }



    private fun loadProfilePicture() {
        // Start shimmer effect before loading image
        shimmerProfilePictureLayout.startShimmer()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            // Get the user's profile picture URL from Firestore
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.contains("profilePictureUrl")) {
                        val profilePictureUrl = document.getString("profilePictureUrl")

                        // If there is a profile picture URL, load it using Glide
                        if (!profilePictureUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profilePictureUrl)
                                .placeholder(R.drawable.ic_pictprofile)
                                .into(profilePicture)

                            // Stop shimmer after the image has been loaded
                            shimmerProfilePictureLayout.stopShimmer()
                            shimmerProfilePictureLayout.visibility = View.GONE
                            profilePicture.visibility = View.VISIBLE
                        }
                    } else {
                        // Stop shimmer and set default image if no URL in Firestore
                        shimmerProfilePictureLayout.stopShimmer()
                        shimmerProfilePictureLayout.visibility = View.GONE
                        profilePicture.setImageResource(R.drawable.ic_pictprofile)
                        profilePicture.visibility = View.VISIBLE
                    }
                }
                .addOnFailureListener { exception ->
                    // Stop shimmer and set default image in case of error
                    shimmerProfilePictureLayout.stopShimmer()
                    shimmerProfilePictureLayout.visibility = View.GONE
                    Log.e("ProfileFragment", "Failed to load profile picture: ${exception.message}")
                    profilePicture.setImageResource(R.drawable.ic_pictprofile)
                    profilePicture.visibility = View.VISIBLE
                }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int): Boolean {
        return if (requireContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestPermissions(arrayOf(permission), requestCode)
            false
        }
    }




    private fun stopShimmerEffect() {
        shimmerNameLayout.stopShimmer()
        shimmerEmailLayout.stopShimmer()
        shimmerProfilePictureLayout.stopShimmer()
        shimmerNameLayout.visibility = View.GONE
        shimmerEmailLayout.visibility = View.GONE
        shimmerProfilePictureLayout.visibility = View.GONE
    }

    private fun handleDataLoadError(message: String) {
        // Stop shimmer effect
        stopShimmerEffect()

        // Display error message
        profileName.text = message
        profileEmail.text = message

        // Display profile content with error message
        profilePicture.visibility = View.VISIBLE
        profileName.visibility = View.VISIBLE
        profileEmail.visibility = View.VISIBLE
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
        if (checkPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, GALLERY_PERMISSION_CODE)) {
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(pickImageIntent, GALLERY_REQUEST_CODE)
        }
    }
    private fun openCamera() {
        if (requireContext().checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera() // Jika izin diberikan, buka kamera
            } else {
                Toast.makeText(requireContext(), "Izin kamera diperlukan untuk mengambil foto", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_image.jpg"))
        val options = UCrop.Options()
        options.setCircleDimmedLayer(true)
        options.setShowCropGrid(false)
        options.setHideBottomControls(true)
        options.setCompressionQuality(90)

        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withOptions(options)
            .start(requireContext(), this)
    }

    private fun updateProfilePicture(uri: Uri) {
        uploadImageToFirebaseStorage(uri) { downloadUrl ->
            val currentUser = FirebaseAuth.getInstance().currentUser
            val db = FirebaseFirestore.getInstance()
            val userId = currentUser?.uid ?: return@uploadImageToFirebaseStorage

            db.collection("users").document(userId)
                .update("profilePictureUrl", downloadUrl)
                .addOnSuccessListener {
                    Glide.with(this).load(downloadUrl).into(profilePicture)
                    Log.d("ProfileFragment", "Gambar profil berhasil diperbarui.")
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileFragment", "Gagal menyimpan URL gambar: ${e.message}")
                }
        }
    }

    private fun uploadImageToFirebaseStorage(uri: Uri, onSuccess: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    onSuccess(downloadUrl.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProfileFragment", "Gagal mengunggah gambar: ${e.message}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                1000 -> {
                    val selectedImageUri = data.data
                    if (selectedImageUri != null) {
                        startCrop(selectedImageUri)
                    }
                }
                1001 -> {
                    val bitmap = data.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        val uri = saveBitmapToUri(bitmap)
                        if (uri != null) {
                            startCrop(uri)
                        }
                    }
                }
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data)
                    if (resultUri != null) {
                        updateProfilePicture(resultUri)
                    }
                }
            }
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

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(activity, LandingActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}
