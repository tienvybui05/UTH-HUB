package com.example.uth_hub.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.auth.AuthConst
import com.example.uth_hub.feature.auth.domain.model.AppUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


/**
 * ViewModel hiển thị trang cá nhân của người KHÁC.
 * uid được truyền trực tiếp từ NavGraph thông qua Factory
 */
class OtherProfileViewModel(
    private val uid: String
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(OtherProfileUiState())
    val ui: StateFlow<OtherProfileUiState> = _ui.asStateFlow()

    init {
        if (uid.isBlank()) {
            _ui.value = OtherProfileUiState(
                loading = false,
                user = null,
                error = "Không tìm thấy uid người dùng."
            )
        } else {
            loadUser()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            try {
                val snap = db.collection(AuthConst.USERS)
                    .document(uid)
                    .get()
                    .await()

                val user = snap.toObject(AppUser::class.java)

                _ui.value = OtherProfileUiState(
                    loading = false,
                    user = user,
                    error = null
                )
            } catch (e: Exception) {
                _ui.value = OtherProfileUiState(
                    loading = false,
                    user = null,
                    error = e.message
                )
            }
        }
    }

    /**
     * Chia sẻ trang cá nhân người dùng khác.
     */
    fun shareProfile(user: AppUser?) {
        // TODO: implement chia sẻ trang cá nhân (deep link / dynamic link / hosting)
    }
}

