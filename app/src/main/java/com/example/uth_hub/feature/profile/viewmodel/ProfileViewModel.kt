package com.example.uth_hub.feature.profile.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.auth.domain.model.AppUser
import com.example.uth_hub.feature.profile.data.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class ProfileViewModel : ViewModel() {

    private val repo = ProfileRepository(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )

    // giữ nguyên logic stateIn bạn cung cấp
    val ui: StateFlow<ProfileUiState> =
        repo.currentUserFlow()
            .map { user ->
                ProfileUiState(
                    loading = false,
                    user = user,
                    error = null
                )
            }
            .catch { e ->
                emit(
                    ProfileUiState(
                        loading = false,
                        user = null,
                        error = e.message
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProfileUiState(loading = true)
            )

    /** Đăng xuất */
    fun signOut() {
        repo.signOut()
    }

    // ============================
    //      🔥 AVATAR FUNCTIONS
    // ============================

    /** Cập nhật avatar từ thư viện (URI) */
    fun updateAvatarFromUri(uri: Uri) {
        viewModelScope.launch {
            try {
                repo.updateAvatarFromUri(uri)
            } catch (_: Exception) {
                // không thay đổi state, không đụng UI State
            }
        }
    }

    /** Cập nhật avatar từ ảnh chụp (Bitmap) */
    fun updateAvatarFromBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                repo.updateAvatarFromBitmap(bitmap)
            } catch (_: Exception) {
            }
        }
    }

    /** Reset avatar về avatar mặc định từ Google */
    fun resetAvatarToGoogleDefault() {
        viewModelScope.launch {
            try {
                repo.resetAvatarToGoogleDefault()
            } catch (_: Exception) {
            }
        }
    }
    fun updateUserProfile(
        mssv: String,
        phone: String,
        institute: String,
        classCode: String
    ) {
        viewModelScope.launch {
            try {
                repo.updateUserProfile(mssv, phone, institute, classCode)
            } catch (_: Exception) {}
        }
    }

}
