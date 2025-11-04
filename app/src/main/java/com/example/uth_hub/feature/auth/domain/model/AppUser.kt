package com.example.uth_hub.feature.auth.domain.model

data class AppUser(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val mssv: String? = null,
    val phone: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
