package com.example.data.model

// 1. User
data class ClientUser(
  val id: String,
  val fullName: String,
  val email: String,
  val phone: String,
  val nationalId: String,
  val organization: String? = null,
  val address: String = "Dar es Salaam, Tanzania",
  val avatarUrl: String? = null,
  val isVerified: Boolean = true
)

// 2. Request Models
enum class RequestStatus {
  DRAFT,
  SUBMITTED,
  PAYMENT_REQUIRED,
  PAYMENT_PENDING,
  PAYMENT_SUBMITTED,
  PAYMENT_APPROVED,
  AWAITING_PROCESSING,
  ACCEPTED,
  REJECTED
}

enum class LegalCategory(val title: String) {
  CORPORATE("Corporate & Commercial Advisory"),
  LITIGATION("Civil & Commercial Litigation"),
  CONVEYANCING("Real Estate & Conveyancing"),
  EMPLOYMENT("Labor & Employment Relations"),
  IP("Intellectual Property & Trademarks"),
  TAX("Tax Advisory & Revenue Law"),
  FAMILY("Family, Probate & Succession"),
  REGULATORY("Compliance & Regulatory Filings")
}

enum class UrgencyLevel(val title: String) {
  NORMAL("Standard (3-5 business days)"),
  URGENT("Urgent (Within 48 hours)"),
  CRITICAL("Emergency / Court Deadline Imminent")
}

data class AttachedDocument(
  val id: String,
  val name: String,
  val sizeBytes: Long,
  val mimeType: String,
  val uploadDate: String
)

data class LegalRequest(
  val id: String,
  val referenceNumber: String,
  val clientName: String,
  val clientPhone: String,
  val clientEmail: String,
  val category: LegalCategory,
  val urgency: UrgencyLevel,
  val title: String,
  val description: String,
  val opposingParty: String? = null,
  val desiredOutcome: String? = null,
  val status: RequestStatus,
  val submissionDate: String,
  val estimatedFeeTzs: Long = 500_000,
  val attachedDocuments: List<AttachedDocument> = emptyList(),
  val relatedInvoiceId: String? = null,
  val acceptedMatterId: String? = null,
  val statusNotes: String? = null
)

// 3. Payment Models
enum class PaymentMethodType {
  LIPA_NUMBER,
  QR_CODE,
  BANK_ACCOUNT
}

enum class PaymentVerificationStatus {
  PENDING_SUBMISSION,
  PROOF_SUBMITTED,
  UNDER_VERIFICATION,
  APPROVED,
  REJECTED
}

data class PaymentDetails(
  val id: String,
  val referenceNumber: String, // e.g., "ETA-PAY-2026-042"
  val targetType: String, // "REQUEST", "INVOICE", "CONSULTATION"
  val targetId: String,
  val amountTzs: Long,
  val method: PaymentMethodType = PaymentMethodType.LIPA_NUMBER,
  val status: PaymentVerificationStatus,
  val proofUploadedName: String? = null,
  val transactionReference: String? = null,
  val submittedAt: String? = null,
  val verifiedAt: String? = null,
  val rejectionReason: String? = null
)

// 4. Invoice Models
enum class InvoiceStatus {
  UNPAID,
  PAYMENT_SUBMITTED,
  PAID,
  REJECTED
}

data class InvoiceItem(
  val description: String,
  val quantity: Int = 1,
  val unitPriceTzs: Long,
  val totalTzs: Long
)

data class LegalInvoice(
  val id: String,
  val invoiceNumber: String,
  val title: String,
  val description: String,
  val items: List<InvoiceItem>,
  val subtotalTzs: Long,
  val vatTzs: Long,
  val totalTzs: Long,
  val issueDate: String,
  val dueDate: String,
  val status: InvoiceStatus,
  val relatedMatterId: String? = null,
  val relatedRequestId: String? = null,
  val relatedAppointmentId: String? = null
)

// 5. Matter Models
enum class MatterStatus {
  ACTIVE,
  IN_COURT,
  DISCOVERY,
  SETTLEMENT_NEGOTIATION,
  CONCLUDED
}

data class MatterMilestone(
  val id: String,
  val title: String,
  val date: String,
  val isCompleted: Boolean,
  val notes: String
)

data class LegalMatter(
  val id: String,
  val matterReference: String, // e.g. "ETA/LIT/2026/014"
  val title: String,
  val category: LegalCategory,
  val status: MatterStatus,
  val leadAdvocate: String,
  val courtForum: String,
  val opposingParty: String,
  val openedDate: String,
  val relatedRequestId: String,
  val milestones: List<MatterMilestone> = emptyList(),
  val activeNotes: String
)

// 6. Messages Models
data class ChatMessage(
  val id: String,
  val conversationId: String,
  val senderId: String,
  val senderName: String,
  val isFromClient: Boolean,
  val messageText: String,
  val timestamp: String,
  val attachedDocumentName: String? = null
)

data class Conversation(
  val id: String,
  val title: String,
  val subtitle: String,
  val relatedMatterId: String? = null,
  val relatedRequestId: String? = null,
  val lastMessageText: String,
  val lastMessageTimestamp: String,
  val unreadCount: Int = 0,
  val advocateName: String = "Et Cetra Legal Chambers"
)

// 7. Appointment Models
enum class AppointmentStatus {
  UPCOMING,
  PENDING_CONFIRMATION,
  PAYMENT_REQUIRED,
  COMPLETED,
  CANCELLED
}

enum class ConsultationMode {
  IN_PERSON_OFFICE,
  VIRTUAL_SECURE_VIDEO
}

data class ConsultationAppointment(
  val id: String,
  val advocateName: String,
  val advocateTitle: String,
  val practiceArea: LegalCategory,
  val mode: ConsultationMode,
  val appointmentDate: String,
  val timeSlot: String,
  val locationOrLink: String,
  val agenda: String,
  val feeTzs: Long = 150_000,
  val status: AppointmentStatus,
  val relatedInvoiceId: String? = null
)

// 8. Document Models
enum class DocumentCategory(val label: String) {
  BRIEFS("Legal Briefs & Memos"),
  PLEADINGS("Court Pleadings & Affidavits"),
  EVIDENCE("Contracts & Evidence"),
  KYC("Identity & Statutory Records"),
  INVOICES("Fee Notes & Receipts")
}

data class LegalDocument(
  val id: String,
  val name: String,
  val category: DocumentCategory,
  val sizeBytes: Long,
  val uploadedDate: String,
  val isVerifiedByFirm: Boolean,
  val relatedMatterId: String? = null,
  val relatedRequestId: String? = null,
  val downloadUrl: String? = null
)

// 9. Notification Models
enum class NotificationType {
  REQUEST,
  PAYMENT,
  MATTER,
  APPOINTMENT,
  MESSAGE,
  DOCUMENT
}

data class AppNotification(
  val id: String,
  val type: NotificationType,
  val title: String,
  val message: String,
  val timestamp: String,
  val isRead: Boolean = false,
  val targetScreenRoute: String? = null
)

// 10. Settings & Preferences
data class AppPreferences(
  val isDarkMode: Boolean = false,
  val language: String = "en", // "en" or "sw"
  val notificationsEnabled: Boolean = true,
  val biometricLoginEnabled: Boolean = false,
  val fontScale: Float = 1.0f,
  val reducedMotion: Boolean = false
)
