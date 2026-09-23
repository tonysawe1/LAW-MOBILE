package com.example.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.ClientUser
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
  private val authRepository: AuthRepository = LawChambersCoordinator.authRepository
) : ViewModel() {

  val currentUser: StateFlow<ClientUser?> = authRepository.currentUser

  private val _isUpdating = MutableStateFlow(false)
  val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

  fun updateProfile(name: String, phone: String, org: String, address: String) {
    viewModelScope.launch {
      _isUpdating.value = true
      authRepository.updateProfile(name, phone, org, address)
      _isUpdating.value = false
    }
  }

  fun logout() {
    viewModelScope.launch {
      authRepository.logout()
    }
  }
}
