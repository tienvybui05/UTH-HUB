package com.example.uth_hub.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
/**
* Factory để tạo OtherProfileViewModel với uid truyền từ NavGraph.
*/

class OtherProfileViewModelFactory(
    private val uid: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtherProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OtherProfileViewModel(uid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
