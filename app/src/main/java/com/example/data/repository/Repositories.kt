package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
  val currentUser: StateFlow<ClientUser?>
  val isAuthenticated: StateFlow<Boolean>

  suspend fun login(email: String, pass: String): Result<ClientUser>
  suspend fun register(fullName: String, email: String, phone: String, nationalId: String, pass: String): Result<ClientUser>
  suspend fun logout()
  suspend fun sendPasswordReset(email: String): Result<Boolean>
  suspend fun restoreSession(): Boolean
  suspend fun updateProfile(name: String, phone: String, address: String, org: String?): Result<ClientUser>
}

interface RequestRepository {
  val requests: StateFlow<List<LegalRequest>>

  suspend fun getRequestById(id: String): LegalRequest?
  suspend fun createRequest(
    clientName: String,
    clientPhone: String,
    clientEmail: String,
    category: LegalCategory,
    urgency: UrgencyLevel,
    title: String,
    description: String,
    opposingParty: String?,
    desiredOutcome: String?,
    attachments: List<AttachedDocument>
  ): Result<LegalRequest>
  suspend fun updateRequestStatus(id: String, status: RequestStatus, note: String? = null)
}

interface MatterRepository {
  val matters: StateFlow<List<LegalMatter>>

  suspend fun getMatterById(id: String): LegalMatter?
}

interface PaymentRepository {
  val payments: StateFlow<List<PaymentDetails>>

  suspend fun getPaymentByReference(ref: String): PaymentDetails?
  suspend fun getPaymentForTarget(targetType: String, targetId: String): PaymentDetails?
  suspend fun submitPaymentProof(paymentId: String, proofName: String, transactionRef: String): Result<PaymentDetails>
  suspend fun simulateOfficeDecision(paymentId: String, approve: Boolean, reason: String? = null): Result<PaymentDetails>
}

interface InvoiceRepository {
  val invoices: StateFlow<List<LegalInvoice>>

  suspend fun getInvoiceById(id: String): LegalInvoice?
  suspend fun markInvoicePaymentSubmitted(invoiceId: String)
  suspend fun markInvoicePaid(invoiceId: String)
  suspend fun markInvoiceRejected(invoiceId: String)
}

interface MessageRepository {
  val conversations: StateFlow<List<Conversation>>

  fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>>
  suspend fun sendMessage(convId: String, text: String, attachmentName: String? = null): Result<ChatMessage>
  suspend fun getOrCreateConversationForRequest(requestId: String, title: String): String
  suspend fun getOrCreateConversationForMatter(matterId: String, title: String): String
}

interface AppointmentRepository {
  val appointments: StateFlow<List<ConsultationAppointment>>

  suspend fun getAppointmentById(id: String): ConsultationAppointment?
  suspend fun bookConsultation(
    advocateName: String,
    advocateTitle: String,
    category: LegalCategory,
    mode: ConsultationMode,
    date: String,
    time: String,
    agenda: String
  ): Result<ConsultationAppointment>
  suspend fun updateAppointmentStatus(id: String, status: AppointmentStatus)
}

interface DocumentRepository {
  val documents: StateFlow<List<LegalDocument>>

  suspend fun uploadDocument(
    name: String,
    category: DocumentCategory,
    sizeBytes: Long,
    relatedMatterId: String?,
    relatedRequestId: String?
  ): Result<LegalDocument>
}

interface NotificationRepository {
  val notifications: StateFlow<List<AppNotification>>

  suspend fun markAsRead(id: String)
  suspend fun markAllAsRead()
  fun addNotification(type: NotificationType, title: String, message: String, route: String?)
}

interface SettingsRepository {
  val settings: StateFlow<AppPreferences>

  suspend fun updateDarkMode(enabled: Boolean)
  suspend fun updateLanguage(langCode: String)
  suspend fun updateNotifications(enabled: Boolean)
  suspend fun updateBiometric(enabled: Boolean)
  suspend fun updateFontScale(scale: Float)
  suspend fun updateReducedMotion(enabled: Boolean)
}
