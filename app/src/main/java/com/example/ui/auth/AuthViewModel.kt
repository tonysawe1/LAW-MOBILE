package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.ClientUser
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
  object Idle : AuthUiState
  object Loading : AuthUiState
  data class Success(val user: ClientUser) : AuthUiState
  data class Error(val message: String) : AuthUiState
  data class ResetSent(val email: String) : AuthUiState
}

class AuthViewModel(
  private val authRepository: AuthRepository = LawChambersCoordinator.authRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
  val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

  val currentUser: StateFlow<ClientUser?> = authRepository.currentUser
  val isAuthenticated: StateFlow<Boolean> = authRepository.isAuthenticated

  fun login(email: String, pass: String) {
    viewModelScope.launch {
      _uiState.value = AuthUiState.Loading
      val result = authRepository.login(email, pass)
      result.fold(
        onSuccess = { user -> _uiState.value = AuthUiState.Success(user) },
        onFailure = { err -> _uiState.value = AuthUiState.Error(err.message ?: "Authentication failed") }
      )
    }
  }

  fun register(name: String, email: String, phone: String, nid: String, pass: String) {
    viewModelScope.launch {
      _uiState.value = AuthUiState.Loading
      val result = authRepository.register(name, email, phone, nid, pass)
      result.fold(
        onSuccess = { user -> _uiState.value = AuthUiState.Success(user) },
        onFailure = { err -> _uiState.value = AuthUiState.Error(err.message ?: "Registration failed") }
      )
    }
  }

  fun resetPassword(email: String) {
    viewModelScope.launch {
      _uiState.value = AuthUiState.Loading
      val result = authRepository.sendPasswordReset(email)
      result.fold(
        onSuccess = { _uiState.value = AuthUiState.ResetSent(email) },
        onFailure = { err -> _uiState.value = AuthUiState.Error(err.message ?: "Failed to send reset link") }
      )
    }
  }

  fun logout() {
    viewModelScope.launch {
      authRepository.logout()
      _uiState.value = AuthUiState.Idle
    }
  }

  fun clearError() {
    _uiState.value = AuthUiState.Idle
  }
}
