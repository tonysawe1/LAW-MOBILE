package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
  val currentUser: ClientUser? = null,
  val activeRequestsCount: Int = 0,
  val activeMattersCount: Int = 0,
  val pendingInvoicesCount: Int = 0,
  val pendingInvoicesTotalTzs: Long = 0,
  val unreadNotificationsCount: Int = 0,
  val unreadMessagesCount: Int = 0,
  val upcomingAppointment: ConsultationAppointment? = null,
  val recentRequests: List<LegalRequest> = emptyList(),
  val activeMatters: List<LegalMatter> = emptyList(),
  val outstandingInvoices: List<LegalInvoice> = emptyList()
)

class HomeViewModel : ViewModel() {
  private val authRepo = LawChambersCoordinator.authRepository
  private val requestRepo = LawChambersCoordinator.requestRepository
  private val matterRepo = LawChambersCoordinator.matterRepository
  private val invoiceRepo = LawChambersCoordinator.invoiceRepository
  private val appointmentRepo = LawChambersCoordinator.appointmentRepository
  private val notificationRepo = LawChambersCoordinator.notificationRepository
  private val messageRepo = LawChambersCoordinator.messageRepository

  private val primaryDataFlow = combine(
    authRepo.currentUser,
    requestRepo.requests,
    matterRepo.matters,
    invoiceRepo.invoices,
    appointmentRepo.appointments
  ) { user, requests, matters, invoices, appointments ->
    val activeReqs = requests.filter { it.status != RequestStatus.ACCEPTED && it.status != RequestStatus.REJECTED }
    val unpaidInvoices = invoices.filter { it.status == InvoiceStatus.UNPAID || it.status == InvoiceStatus.REJECTED }
    val nextAppointment = appointments.firstOrNull { it.status == AppointmentStatus.UPCOMING || it.status == AppointmentStatus.PAYMENT_REQUIRED }

    Triple(user, Triple(activeReqs, matters, unpaidInvoices), Pair(nextAppointment, Pair(requests, invoices)))
  }

  val uiState: StateFlow<HomeUiState> = combine(
    primaryDataFlow,
    notificationRepo.notifications,
    messageRepo.conversations
  ) { primary, notifications, conversations ->
    val user = primary.first
    val (activeReqs, matters, unpaidInvoices) = primary.second
    val nextAppointment = primary.third.first
    val (requests, invoices) = primary.third.second

    val unreadNotifs = notifications.count { !it.isRead }
    val unreadMsgs = conversations.sumOf { it.unreadCount }

    HomeUiState(
      currentUser = user,
      activeRequestsCount = activeReqs.size,
      activeMattersCount = matters.size,
      pendingInvoicesCount = unpaidInvoices.size,
      pendingInvoicesTotalTzs = unpaidInvoices.sumOf { it.totalTzs },
      unreadNotificationsCount = unreadNotifs,
      unreadMessagesCount = unreadMsgs,
      upcomingAppointment = nextAppointment,
      recentRequests = requests.take(3),
      activeMatters = matters.take(2),
      outstandingInvoices = unpaidInvoices.take(2)
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

  fun getOrCreateGeneralOfficeChat(onReady: (String) -> Unit) {
    viewModelScope.launch {
      val convId = messageRepo.getOrCreateConversationForRequest("GENERAL-OFFICE", "Et Cetra Legal Chambers")
      onReady(convId)
    }
  }
}
