package com.example.ui.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.AppointmentStatus
import com.example.data.model.ConsultationAppointment
import com.example.data.model.ConsultationMode
import com.example.data.model.LegalCategory
import com.example.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookConsultationDraft(
  val practiceArea: LegalCategory = LegalCategory.CORPORATE,
  val advocateName: String = "Adv. Sarah Mwangi",
  val advocateTitle: String = "Managing Partner • Corporate & Commercial",
  val mode: ConsultationMode = ConsultationMode.IN_PERSON_OFFICE,
  val appointmentDate: String = "Tomorrow, 10:00 AM",
  val timeSlot: String = "10:00 AM - 11:00 AM",
  val purpose: String = "",
  val feeTzs: Long = 150000
)

class AppointmentsViewModel(
  private val appointmentRepository: AppointmentRepository = LawChambersCoordinator.appointmentRepository
) : ViewModel() {

  val appointments: StateFlow<List<ConsultationAppointment>> = appointmentRepository.appointments

  private val _bookingDraft = MutableStateFlow(BookConsultationDraft())
  val bookingDraft: StateFlow<BookConsultationDraft> = _bookingDraft.asStateFlow()

  private val _isSubmitting = MutableStateFlow(false)
  val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

  fun updateDraft(
    area: LegalCategory? = null,
    advocate: String? = null,
    title: String? = null,
    mode: ConsultationMode? = null,
    date: String? = null,
    slot: String? = null,
    purpose: String? = null
  ) {
    _bookingDraft.update { current ->
      current.copy(
        practiceArea = area ?: current.practiceArea,
        advocateName = advocate ?: current.advocateName,
        advocateTitle = title ?: current.advocateTitle,
        mode = mode ?: current.mode,
        appointmentDate = date ?: current.appointmentDate,
        timeSlot = slot ?: current.timeSlot,
        purpose = purpose ?: current.purpose
      )
    }
  }

  fun bookConsultation(onSuccess: (ConsultationAppointment) -> Unit) {
    viewModelScope.launch {
      _isSubmitting.value = true
      val draft = _bookingDraft.value
      val result = appointmentRepository.bookConsultation(
        advocateName = draft.advocateName,
        advocateTitle = draft.advocateTitle,
        category = draft.practiceArea,
        mode = draft.mode,
        date = draft.appointmentDate,
        time = draft.timeSlot,
        agenda = draft.purpose.ifBlank { "Legal assessment and consultation on ${draft.practiceArea.title} matter." }
      )
      _isSubmitting.value = false
      result.onSuccess { appt ->
        _bookingDraft.value = BookConsultationDraft()
        onSuccess(appt)
      }
    }
  }

  fun cancelAppointment(id: String) {
    viewModelScope.launch {
      appointmentRepository.updateAppointmentStatus(id, AppointmentStatus.CANCELLED)
    }
  }
}
