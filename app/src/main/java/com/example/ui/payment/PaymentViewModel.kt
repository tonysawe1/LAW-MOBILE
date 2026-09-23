package com.example.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.*
import com.example.data.repository.PaymentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PaymentUiState(
  val paymentDetails: PaymentDetails? = null,
  val selectedMethod: PaymentMethodType = PaymentMethodType.LIPA_NUMBER,
  val isSubmittingProof: Boolean = false,
  val showProofDialog: Boolean = false,
  val errorMessage: String? = null
)

class PaymentViewModel(
  private val paymentRepository: PaymentRepository = LawChambersCoordinator.paymentRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow(PaymentUiState())
  val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

  fun loadPayment(identifier: String) {
    viewModelScope.launch {
      // Find by id or reference or targetId
      val payments = paymentRepository.payments.value
      val p = payments.find {
        it.id == identifier || it.referenceNumber == identifier || it.targetId == identifier
      } ?: paymentRepository.getPaymentForTarget("REQUEST", identifier)
        ?: paymentRepository.getPaymentForTarget("INVOICE", identifier)
        ?: paymentRepository.getPaymentForTarget("CONSULTATION", identifier)

      _uiState.update { it.copy(paymentDetails = p) }
    }
  }

  fun selectMethod(method: PaymentMethodType) {
    _uiState.update { it.copy(selectedMethod = method) }
  }

  fun openProofDialog() {
    _uiState.update { it.copy(showProofDialog = true) }
  }

  fun closeProofDialog() {
    _uiState.update { it.copy(showProofDialog = false) }
  }

  fun submitProof(proofName: String, transactionRef: String) {
    val current = _uiState.value.paymentDetails ?: return
    viewModelScope.launch {
      _uiState.update { it.copy(isSubmittingProof = true) }
      val result = paymentRepository.submitPaymentProof(current.id, proofName, transactionRef)
      _uiState.update {
        it.copy(
          isSubmittingProof = false,
          showProofDialog = false,
          paymentDetails = result.getOrNull() ?: it.paymentDetails
        )
      }
    }
  }

  fun simulateOfficeApproval() {
    val current = _uiState.value.paymentDetails ?: return
    viewModelScope.launch {
      val result = paymentRepository.simulateOfficeDecision(current.id, approve = true)
      _uiState.update { it.copy(paymentDetails = result.getOrNull() ?: it.paymentDetails) }
    }
  }

  fun simulateOfficeRejection(reason: String = "Transaction reference did not reflect on merchant account. Please upload valid receipt.") {
    val current = _uiState.value.paymentDetails ?: return
    viewModelScope.launch {
      val result = paymentRepository.simulateOfficeDecision(current.id, approve = false, reason = reason)
      _uiState.update { it.copy(paymentDetails = result.getOrNull() ?: it.paymentDetails) }
    }
  }
}
