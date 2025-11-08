package com.example.uth_hub.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.auth.domain.model.AppUser
import com.example.uth_hub.feature.profile.data.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileUiState(
    val loading: Boolean = true,
    val user: AppUser? = null,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    private val repo = ProfileRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    // Tối ưu: dùng stateIn
    val ui: StateFlow<ProfileUiState> =
        repo.currentUserFlow() // Flow<AppUser?>
            .map { user -> ProfileUiState(loading = false, user = user, error = null) }
            .catch { e -> emit(ProfileUiState(loading = false, user = null, error = e.message)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProfileUiState(loading = true)
            )

    fun signOut() {
        // Nếu repo.signOut() là suspend -> viewModelScope.launch { repo.signOut() }
        repo.signOut()
    }
}
