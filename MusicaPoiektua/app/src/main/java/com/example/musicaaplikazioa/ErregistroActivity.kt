package com.example.musicaaplikazioa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicaaplikazioa.databinding.ActivityErregistroBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp


class ErregistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityErregistroBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityErregistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Datu guztiak sartu", Toast.LENGTH_SHORT).show()
            } else {
                checkAndRegisterUser(email, password, username)
            }
        }
    }

    private fun checkAndRegisterUser(email: String, password: String, userName: String) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
                } else {
                    val user = hashMapOf(
                        "createdAt" to Timestamp.now(),
                        "email" to email,
                        "password" to password,
                        "profileUrl" to "",
                        "spotifyId" to "",
                        "userName" to userName
                    )

                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar usuario: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
