package com.example.uth_hub.feature.profile.viewmodel

import com.example.uth_hub.feature.auth.domain.model.AppUser

data class OtherProfileUiState(
    val loading: Boolean = true,
    val user: AppUser? = null,
    val error: String? = null
)