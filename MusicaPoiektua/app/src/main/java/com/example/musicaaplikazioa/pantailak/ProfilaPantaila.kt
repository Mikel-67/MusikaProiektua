package com.example.musicaaplikazioa.pantailak

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.musicaaplikazioa.R
import com.example.musicaaplikazioa.adapter.PostAdapterGrid
import com.example.musicaaplikazioa.models.Post
import com.example.musicaaplikazioa.models.UserFirebase
import com.google.firebase.firestore.FirebaseFirestore

class ProfilaPantaila {
    private lateinit var ivProfilePic: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var rvUserPosts: RecyclerView

    private lateinit var db: FirebaseFirestore
    private var postList: ArrayList<Post> = ArrayList()
    private lateinit var adapter: PostAdapterGrid

    private var userId: String = "1"
    fun sortu(
        context: Context,
        ivProfilePic: ImageView,
        tvUserName: TextView,
        tvEmail: TextView,
        rvUserPosts: RecyclerView,
        userId: String
    ) {
        this.ivProfilePic = ivProfilePic
        this.tvUserName = tvUserName
        this.tvEmail = tvEmail
        this.rvUserPosts = rvUserPosts
        this.userId = userId

        db = FirebaseFirestore.getInstance()

        // Configurar RecyclerView
        adapter = PostAdapterGrid(postList)
        rvUserPosts.layoutManager = GridLayoutManager(context, 3)
        rvUserPosts.adapter = adapter

        // Cargar datos
        cargarUsuario(context)
        cargarPostsDelUsuario()
    }

    private fun cargarUsuario(context: Context) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    val user = doc.toObject(UserFirebase::class.java)
                    user?.let {
                        tvUserName.text = it.userName
                        tvEmail.text = it.email

                        Glide.with(context)
                            .load(it.profileUrl.ifEmpty { R.drawable.circle_background })
                            .circleCrop()
                            .into(ivProfilePic)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProfilaPantaila", "Error al cargar usuario", e)
            }
    }

    private fun cargarPostsDelUsuario() {
        db.collection("posts")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val post = doc.toObject(Post::class.java)
                    postList.add(post)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("ProfilaPantaila", "Error al cargar posts", e)
            }
    }
}