package com.example.uth_hub.feature.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uth_hub.feature.notifications.data.NotificationRepository
import com.example.uth_hub.feature.notifications.model.NotificationModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repo: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationModel>>(emptyList())
    val notifications = _notifications.asStateFlow()

    fun load(uid: String) {
        viewModelScope.launch {
            repo.getNotifications(uid).collect {
                android.util.Log.d("NOTI_VM", "ViewModel nhận list size = ${it.size}")
                _notifications.value = it
            }
        }
    }
    fun deleteNotification(id: String) {
        viewModelScope.launch {
            repo.deleteNotification(id)
        }
    }

}
