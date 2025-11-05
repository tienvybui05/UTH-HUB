package com.example.uth_hub.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.auth.domain.model.AppUser
import com.example.uth_hub.feature.profile.data.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val loading: Boolean = true,
    val user: AppUser? = null,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {
    private val repo = ProfileRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())

    private val _ui = MutableStateFlow(ProfileUiState())
    val ui: StateFlow<ProfileUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repo.currentUserFlow().collect { u ->
                _ui.value = ProfileUiState(loading = false, user = u, error = null)
            }
        }
    }

    fun signOut() = repo.signOut()
}
