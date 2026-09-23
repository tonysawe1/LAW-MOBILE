package com.example.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.AppNotification
import com.example.data.repository.NotificationRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(
  private val notificationRepository: NotificationRepository = LawChambersCoordinator.notificationRepository
) : ViewModel() {

  val notifications: StateFlow<List<AppNotification>> = notificationRepository.notifications

  fun markAsRead(id: String) {
    viewModelScope.launch {
      notificationRepository.markAsRead(id)
    }
  }

  fun markAllAsRead() {
    viewModelScope.launch {
      notificationRepository.markAllAsRead()
    }
  }
}
