package com.example.uth_hub.feature.post.di

import com.example.uth_hub.feature.post.data.PostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object PostDI {
    // Có thể dùng lại các instance này cho nhiều màn
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    // chưa bật Storage, getInstance() vẫn OK.
    // Chỉ cần đảm bảo không upload (images = emptyList()).
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    fun providePostRepository(): PostRepository =
        PostRepository(auth = auth, db = db, storage = storage)
}
