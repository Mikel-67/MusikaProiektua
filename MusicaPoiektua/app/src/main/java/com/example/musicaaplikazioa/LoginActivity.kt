package com.example.musicaaplikazioa


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicaaplikazioa.databinding.ActivityLoginBinding

import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Atal guztiak sartu", Toast.LENGTH_SHORT).show()
            } else{
                loginFirebase(email,password)
            }
        }

        binding.tvRegistro.setOnClickListener {
            startActivity(Intent(this, ErregistroActivity::class.java))
        }
    }
    private fun loginFirebase(email: String, password: String) {
        db.collection("users")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "User edo pasahitza okerrak", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Errorea datubasera konektatzen: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
