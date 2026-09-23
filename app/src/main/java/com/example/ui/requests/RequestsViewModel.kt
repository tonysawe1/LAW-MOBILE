package com.example.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.*
import com.example.data.repository.MessageRepository
import com.example.data.repository.RequestRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NewRequestDraft(
  val clientName: String = "Anthony M. Sawe",
  val clientPhone: String = "+255 754 123 456",
  val clientEmail: String = "saweanthony9@gmail.com",
  val category: LegalCategory = LegalCategory.CORPORATE,
  val urgency: UrgencyLevel = UrgencyLevel.NORMAL,
  val title: String = "",
  val description: String = "",
  val opposingParty: String = "",
  val desiredOutcome: String = "",
  val attachedDocuments: List<AttachedDocument> = emptyList()
)

class RequestsViewModel(
  private val requestRepository: RequestRepository = LawChambersCoordinator.requestRepository,
  private val messageRepository: MessageRepository = LawChambersCoordinator.messageRepository
) : ViewModel() {

  val requests: StateFlow<List<LegalRequest>> = requestRepository.requests

  private val _selectedFilter = MutableStateFlow<String>("ALL")
  val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

  val filteredRequests: StateFlow<List<LegalRequest>> = combine(
    requests,
    _selectedFilter
  ) { list, filter ->
    when (filter) {
      "PAYMENT_DUE" -> list.filter { it.status == RequestStatus.PAYMENT_REQUIRED || it.status == RequestStatus.PAYMENT_PENDING }
      "PROCESSING" -> list.filter { it.status == RequestStatus.PAYMENT_APPROVED || it.status == RequestStatus.AWAITING_PROCESSING || it.status == RequestStatus.PAYMENT_SUBMITTED }
      "ACCEPTED" -> list.filter { it.status == RequestStatus.ACCEPTED }
      else -> list
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _currentDraft = MutableStateFlow(NewRequestDraft())
  val currentDraft: StateFlow<NewRequestDraft> = _currentDraft.asStateFlow()

  private val _currentStep = MutableStateFlow(1) // Steps 1 to 5
  val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

  private val _isSubmitting = MutableStateFlow(false)
  val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

  fun setFilter(filter: String) {
    _selectedFilter.value = filter
  }

  fun setStep(step: Int) {
    _currentStep.value = step.coerceIn(1, 5)
  }

  fun nextStep() {
    _currentStep.value = (_currentStep.value + 1).coerceAtMost(5)
  }

  fun prevStep() {
    _currentStep.value = (_currentStep.value - 1).coerceAtLeast(1)
  }

  fun updateClientInfo(name: String, phone: String, email: String) {
    _currentDraft.update { it.copy(clientName = name, clientPhone = phone, clientEmail = email) }
  }

  fun updateCategoryAndUrgency(cat: LegalCategory, urg: UrgencyLevel) {
    _currentDraft.update { it.copy(category = cat, urgency = urg) }
  }

  fun updateDetails(title: String, desc: String, oppParty: String, outcome: String) {
    _currentDraft.update {
      it.copy(
        title = title,
        description = desc,
        opposingParty = oppParty,
        desiredOutcome = outcome
      )
    }
  }

  fun addSimulatedAttachment(name: String, size: Long, mime: String) {
    val doc = AttachedDocument(
      id = "DOC-ATT-${System.currentTimeMillis().toString().takeLast(4)}",
      name = name,
      sizeBytes = size,
      mimeType = mime,
      uploadDate = "Today"
    )
    _currentDraft.update { it.copy(attachedDocuments = it.attachedDocuments + doc) }
  }

  fun removeAttachment(id: String) {
    _currentDraft.update { draft ->
      draft.copy(attachedDocuments = draft.attachedDocuments.filterNot { it.id == id })
    }
  }

  fun submitRequest(onSuccess: (String) -> Unit) {
    viewModelScope.launch {
      _isSubmitting.value = true
      val draft = _currentDraft.value
      val result = requestRepository.createRequest(
        clientName = draft.clientName,
        clientPhone = draft.clientPhone,
        clientEmail = draft.clientEmail,
        category = draft.category,
        urgency = draft.urgency,
        title = draft.title.ifBlank { "${draft.category.title} Legal Intake" },
        description = draft.description,
        opposingParty = draft.opposingParty.ifBlank { null },
        desiredOutcome = draft.desiredOutcome.ifBlank { null },
        attachments = draft.attachedDocuments
      )
      _isSubmitting.value = false
      result.onSuccess { req ->
        _currentDraft.value = NewRequestDraft()
        _currentStep.value = 1
        onSuccess(req.id)
      }
    }
  }

  fun contactOfficeForRequest(requestId: String, title: String, onReady: (String) -> Unit) {
    viewModelScope.launch {
      val convId = messageRepository.getOrCreateConversationForRequest(requestId, title)
      onReady(convId)
    }
  }
}
