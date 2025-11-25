package com.example.uth_hub.feature.profile.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.auth.AuthConst
import com.example.uth_hub.feature.auth.domain.model.AppUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class OtherProfileUiState(
    val loading: Boolean = true,
    val user: AppUser? = null,
    val error: String? = null
)

class OtherProfileViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _ui = MutableStateFlow(OtherProfileUiState())
    val ui: StateFlow<OtherProfileUiState> = _ui.asStateFlow()

    init {
        // uid được truyền từ route: Routes.OtherProfile/{uid}
        val uid: String? = savedStateHandle["uid"]
        if (uid.isNullOrBlank()) {
            _ui.value = OtherProfileUiState(
                loading = false,
                user = null,
                error = "Không tìm thấy uid người dùng."
            )
        } else {
            loadUser(uid)
        }
    }

    private fun loadUser(uid: String) {
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
     * Hiện tại để trống, sau này bạn có thể:
     *  - sinh deep link (link profile)
     *  - đưa link đó vào Intent.ACTION_SEND trong layer UI.
     */
    fun shareProfile(user: AppUser?) {
        // TODO: implement chia sẻ trang cá nhân (deep link / dynamic link / hosting)
    }
}
