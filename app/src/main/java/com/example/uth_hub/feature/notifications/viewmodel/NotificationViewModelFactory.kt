package com.example.uth_hub.feature.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.uth_hub.feature.notifications.data.NotificationRepository

class NotificationViewModelFactory(
    private val repo: NotificationRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotificationViewModel(repo) as T
    }

}
