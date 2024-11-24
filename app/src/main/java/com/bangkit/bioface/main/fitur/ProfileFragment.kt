package com.bangkit.bioface

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.bangkit.bioface.R

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi UI
        profileName = view.findViewById(R.id.profileName)
        profileEmail = view.findViewById(R.id.profileEmail)

        // Ambil data dari Firestore
        fetchUserData()
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

                        // Tampilkan di UI
                        profileName.text = name ?: "Nama Tidak Ditemukan"
                        profileEmail.text = email ?: "Email Tidak Ditemukan"
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
}
