package com.example.data.mock

import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MockAuthRepository : AuthRepository {
  private val _currentUser = MutableStateFlow<ClientUser?>(
    ClientUser(
      id = "USR-CLIENT-001",
      fullName = "Anthony M. Sawe",
      email = "saweanthony9@gmail.com",
      phone = "+255 754 123 456",
      nationalId = "19880912-11105-00001-24",
      organization = "Kilimanjaro Holdings Limited",
      address = "Plot 42, Ali Hassan Mwinyi Road, Dar es Salaam",
      isVerified = true
    )
  )
  override val currentUser: StateFlow<ClientUser?> = _currentUser.asStateFlow()

  private val _isAuthenticated = MutableStateFlow(true)
  override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

  override suspend fun login(email: String, pass: String): Result<ClientUser> {
    if (email.isBlank() || pass.isBlank()) {
      return Result.failure(IllegalArgumentException("Email and password are required"))
    }
    val user = ClientUser(
      id = "USR-CLIENT-001",
      fullName = if (email.contains("@")) email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() } else "Anthony Sawe",
      email = email,
      phone = "+255 754 123 456",
      nationalId = "19880912-11105-00001-24",
      organization = "Kilimanjaro Holdings Limited",
      address = "Dar es Salaam, Tanzania"
    )
    _currentUser.value = user
    _isAuthenticated.value = true
    return Result.success(user)
  }

  override suspend fun register(
    fullName: String,
    email: String,
    phone: String,
    nationalId: String,
    pass: String
  ): Result<ClientUser> {
    if (fullName.isBlank() || email.isBlank() || pass.length < 6) {
      return Result.failure(IllegalArgumentException("Please complete all registration fields"))
    }
    val user = ClientUser(
      id = "USR-${UUID.randomUUID().toString().take(6).uppercase()}",
      fullName = fullName,
      email = email,
      phone = phone,
      nationalId = nationalId,
      address = "Dar es Salaam, Tanzania"
    )
    _currentUser.value = user
    _isAuthenticated.value = true
    return Result.success(user)
  }

  override suspend fun logout() {
    _currentUser.value = null
    _isAuthenticated.value = false
  }

  override suspend fun sendPasswordReset(email: String): Result<Boolean> {
    return if (email.contains("@")) Result.success(true) else Result.failure(IllegalArgumentException("Invalid email format"))
  }

  override suspend fun restoreSession(): Boolean {
    return _isAuthenticated.value && _currentUser.value != null
  }

  override suspend fun updateProfile(
    name: String,
    phone: String,
    address: String,
    org: String?
  ): Result<ClientUser> {
    val current = _currentUser.value ?: return Result.failure(IllegalStateException("Not authenticated"))
    val updated = current.copy(
      fullName = name,
      phone = phone,
      address = address,
      organization = org
    )
    _currentUser.value = updated
    return Result.success(updated)
  }
}

class MockRequestRepository(
  private val onPaymentRequiredCreated: (requestId: String, title: String, amount: Long) -> String,
  private val onNotificationTrigger: (NotificationType, String, String, String?) -> Unit
) : RequestRepository {

  private val _requests = MutableStateFlow<List<LegalRequest>>(
    listOf(
      LegalRequest(
        id = "REQ-2026-001",
        referenceNumber = "ETA-REQ-2026-001",
        clientName = "Anthony M. Sawe",
        clientPhone = "+255 754 123 456",
        clientEmail = "saweanthony9@gmail.com",
        category = LegalCategory.CONVEYANCING,
        urgency = UrgencyLevel.URGENT,
        title = "Commercial Lease Dispute & Tenant Breach",
        description = "Tenant in commercial building Plot 14 Victoria Plaza has defaulted on lease payments for 4 months and breached zoning covenants. Requires notice of termination, distraint for rent, and eviction proceedings.",
        opposingParty = "Horizon Retailers East Africa Ltd",
        desiredOutcome = "Recovery of TZS 48M in arrears and lawful possession of premises.",
        status = RequestStatus.PAYMENT_APPROVED,
        submissionDate = "Sep 18, 2026",
        estimatedFeeTzs = 500_000,
        statusNotes = "Payment verified. Currently awaiting lead counsel assignment in chambers.",
        relatedInvoiceId = "INV-2026-079",
        attachedDocuments = listOf(
          AttachedDocument("DOC-01", "Commercial_Lease_Contract.pdf", 2_450_000, "application/pdf", "Sep 18, 2026"),
          AttachedDocument("DOC-02", "Default_Notice_Demand_Letter.pdf", 850_000, "application/pdf", "Sep 18, 2026")
        )
      ),
      LegalRequest(
        id = "REQ-2026-002",
        referenceNumber = "ETA-REQ-2026-002",
        clientName = "Anthony M. Sawe",
        clientPhone = "+255 754 123 456",
        clientEmail = "saweanthony9@gmail.com",
        category = LegalCategory.IP,
        urgency = UrgencyLevel.NORMAL,
        title = "Trademark Infringement & Cease and Desist",
        description = "Unauthorized registration and commercial usage of client's registered trademark 'Serengeti Pure' by competitor in Arusha. Requesting urgent cease and desist letter followed by BRELA objection.",
        opposingParty = "Savannah Beverages Co.",
        desiredOutcome = "Immediate cessation of trademark infringement and formal written undertaking.",
        status = RequestStatus.PAYMENT_REQUIRED,
        submissionDate = "Sep 22, 2026",
        estimatedFeeTzs = 500_000,
        statusNotes = "Intake accepted. Payment required before assignment as formal Matter.",
        relatedInvoiceId = "INV-2026-089",
        attachedDocuments = listOf(
          AttachedDocument("DOC-03", "BRELA_Trademark_Certificate.pdf", 1_200_000, "application/pdf", "Sep 22, 2026")
        )
      ),
      LegalRequest(
        id = "REQ-2026-003",
        referenceNumber = "ETA-REQ-2026-003",
        clientName = "Anthony M. Sawe",
        clientPhone = "+255 754 123 456",
        clientEmail = "saweanthony9@gmail.com",
        category = LegalCategory.CORPORATE,
        urgency = UrgencyLevel.NORMAL,
        title = "Shareholders Agreement & Corporate Reorganization",
        description = "Preparation of multi-tier shareholder agreement, dilution safeguards, and governance charter for joint venture expansion.",
        opposingParty = "None (Collaborative Advisory)",
        desiredOutcome = "Final executed Shareholders Agreement and filed BRELA resolutions.",
        status = RequestStatus.ACCEPTED,
        submissionDate = "Aug 10, 2026",
        estimatedFeeTzs = 1_200_000,
        acceptedMatterId = "MAT-2026-019",
        statusNotes = "Accepted into chambers. Formal Matter ETA/CORP/2026/019 instituted."
      )
    )
  )
  override val requests: StateFlow<List<LegalRequest>> = _requests.asStateFlow()

  override suspend fun getRequestById(id: String): LegalRequest? {
    return _requests.value.find { it.id == id || it.referenceNumber == id }
  }

  override suspend fun createRequest(
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
  ): Result<LegalRequest> {
    val count = _requests.value.size + 1
    val reqId = "REQ-2026-00$count"
    val refNum = "ETA-REQ-2026-00$count"
    val fee = when (urgency) {
      UrgencyLevel.CRITICAL -> 750_000L
      UrgencyLevel.URGENT -> 600_000L
      UrgencyLevel.NORMAL -> 500_000L
    }

    val invoiceId = onPaymentRequiredCreated(reqId, title, fee)

    val newReq = LegalRequest(
      id = reqId,
      referenceNumber = refNum,
      clientName = clientName,
      clientPhone = clientPhone,
      clientEmail = clientEmail,
      category = category,
      urgency = urgency,
      title = title,
      description = description,
      opposingParty = opposingParty,
      desiredOutcome = desiredOutcome,
      status = RequestStatus.PAYMENT_REQUIRED,
      submissionDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
      estimatedFeeTzs = fee,
      attachedDocuments = attachments,
      relatedInvoiceId = invoiceId,
      statusNotes = "Intake submitted. The request will not be accepted as a Matter until the required payment has been completed and approved."
    )

    _requests.update { listOf(newReq) + it }
    onNotificationTrigger(
      NotificationType.REQUEST,
      "Intake Request Submitted: $refNum",
      "Your intake request has been registered. Initial legal assessment fee required to proceed.",
      "request_details/$reqId"
    )
    return Result.success(newReq)
  }

  override suspend fun updateRequestStatus(id: String, status: RequestStatus, note: String?) {
    _requests.update { list ->
      list.map { req ->
        if (req.id == id || req.referenceNumber == id) {
          req.copy(
            status = status,
            statusNotes = note ?: req.statusNotes
          )
        } else req
      }
    }
  }
}

class MockMatterRepository : MatterRepository {
  private val _matters = MutableStateFlow<List<LegalMatter>>(
    listOf(
      LegalMatter(
        id = "MAT-2026-019",
        matterReference = "ETA/CORP/2026/019",
        title = "Shareholders Agreement & Reorganization",
        category = LegalCategory.CORPORATE,
        status = MatterStatus.ACTIVE,
        leadAdvocate = "Adv. Anthony Kimaro (Partner)",
        courtForum = "Registrar of Companies (BRELA) / Non-Litigious",
        opposingParty = "None (Internal Structuring)",
        openedDate = "Aug 15, 2026",
        relatedRequestId = "REQ-2026-003",
        activeNotes = "Draft 3 of Shareholders Agreement circulated to all founding principals.",
        milestones = listOf(
          MatterMilestone("M1", "Intake & Legal Assessment Approval", "Aug 10, 2026", true, "Retainer cleared and conflict checks concluded."),
          MatterMilestone("M2", "Corporate Structuring Term Sheet", "Aug 20, 2026", true, "Key terms agreed between investors."),
          MatterMilestone("M3", "Draft Shareholders Agreement & Articles Review", "Sep 15, 2026", true, "Comprehensive review completed."),
          MatterMilestone("M4", "Execution & Board Filings at BRELA", "Oct 10, 2026", false, "Scheduled for final signing ceremony.")
        )
      ),
      LegalMatter(
        id = "MAT-2026-007",
        matterReference = "ETA/LIT/2026/007",
        title = "Et Cetra vs Coastal Shipping Ltd - Commercial Breach",
        category = LegalCategory.LITIGATION,
        status = MatterStatus.IN_COURT,
        leadAdvocate = "Adv. Beatrice Mbowe (Senior Litigation Counsel)",
        courtForum = "High Court of Tanzania (Commercial Division), Dar es Salaam",
        opposingParty = "Coastal Shipping Logistics Ltd",
        openedDate = "May 04, 2026",
        relatedRequestId = "REQ-2026-PREV",
        activeNotes = "Scheduling Conference set for preliminary objections hearing.",
        milestones = listOf(
          MatterMilestone("ML1", "Institution of Suit & Plaint Filing", "May 10, 2026", true, "Commercial Suit No. 118 of 2026 entered."),
          MatterMilestone("ML2", "Service of Summons & Written Statement of Defence", "Jun 14, 2026", true, "Defence and counterclaim received."),
          MatterMilestone("ML3", "Discovery & Inspection of Shipping Manifests", "Jul 28, 2026", true, "Full inspection concluded."),
          MatterMilestone("ML4", "Pre-Trial Conference & Hearing", "Oct 18, 2026", false, "Hon. Judge presiding at Courtroom 3.")
        )
      )
    )
  )
  override val matters: StateFlow<List<LegalMatter>> = _matters.asStateFlow()

  override suspend fun getMatterById(id: String): LegalMatter? {
    return _matters.value.find { it.id == id || it.matterReference == id }
  }
}

class MockPaymentRepository(
  private val onRequestStatusUpdated: suspend (requestId: String, status: RequestStatus, note: String?) -> Unit,
  private val onInvoiceStatusUpdated: suspend (invoiceId: String, status: InvoiceStatus) -> Unit,
  private val onAppointmentStatusUpdated: suspend (appointmentId: String, status: AppointmentStatus) -> Unit,
  private val onNotificationTrigger: (NotificationType, String, String, String?) -> Unit
) : PaymentRepository {

  private val _payments = MutableStateFlow<List<PaymentDetails>>(
    listOf(
      PaymentDetails(
        id = "PAY-2026-001",
        referenceNumber = "ETA-PAY-2026-001",
        targetType = "REQUEST",
        targetId = "REQ-2026-001",
        amountTzs = 500_000,
        method = PaymentMethodType.LIPA_NUMBER,
        status = PaymentVerificationStatus.APPROVED,
        proofUploadedName = "Mpesa_Receipt_QB829104.pdf",
        transactionReference = "QB82910491X",
        submittedAt = "Sep 19, 2026 11:20 AM",
        verifiedAt = "Sep 19, 2026 02:45 PM"
      ),
      PaymentDetails(
        id = "PAY-2026-002",
        referenceNumber = "ETA-PAY-2026-002",
        targetType = "REQUEST",
        targetId = "REQ-2026-002",
        amountTzs = 500_000,
        method = PaymentMethodType.LIPA_NUMBER,
        status = PaymentVerificationStatus.PENDING_SUBMISSION
      ),
      PaymentDetails(
        id = "PAY-2026-003",
        referenceNumber = "ETA-PAY-2026-003",
        targetType = "INVOICE",
        targetId = "INV-2026-074",
        amountTzs = 2_500_000,
        method = PaymentMethodType.BANK_ACCOUNT,
        status = PaymentVerificationStatus.APPROVED,
        proofUploadedName = "Stanbic_Wire_Transfer_Slip.pdf",
        transactionReference = "STB-TXN-8821940",
        submittedAt = "Jun 02, 2026",
        verifiedAt = "Jun 03, 2026"
      )
    )
  )
  override val payments: StateFlow<List<PaymentDetails>> = _payments.asStateFlow()

  override suspend fun getPaymentByReference(ref: String): PaymentDetails? {
    return _payments.value.find { it.referenceNumber == ref || it.id == ref }
  }

  override suspend fun getPaymentForTarget(targetType: String, targetId: String): PaymentDetails? {
    return _payments.value.find { it.targetType == targetType && it.targetId == targetId }
  }

  fun createPayment(targetType: String, targetId: String, amount: Long): PaymentDetails {
    val count = _payments.value.size + 1
    val pay = PaymentDetails(
      id = "PAY-2026-00$count",
      referenceNumber = "ETA-PAY-2026-00$count",
      targetType = targetType,
      targetId = targetId,
      amountTzs = amount,
      status = PaymentVerificationStatus.PENDING_SUBMISSION
    )
    _payments.update { listOf(pay) + it }
    return pay
  }

  override suspend fun submitPaymentProof(
    paymentId: String,
    proofName: String,
    transactionRef: String
  ): Result<PaymentDetails> {
    var updated: PaymentDetails? = null
    _payments.update { list ->
      list.map { p ->
        if (p.id == paymentId || p.referenceNumber == paymentId) {
          val now = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date())
          val up = p.copy(
            proofUploadedName = proofName,
            transactionReference = transactionRef,
            status = PaymentVerificationStatus.PROOF_SUBMITTED,
            submittedAt = now,
            rejectionReason = null
          )
          updated = up
          up
        } else p
      }
    }

    val res = updated ?: return Result.failure(IllegalArgumentException("Payment not found"))

    // Update target entities accordingly
    when (res.targetType) {
      "REQUEST" -> {
        onRequestStatusUpdated(res.targetId, RequestStatus.PAYMENT_SUBMITTED, "Payment proof submitted ($transactionRef). Under office review.")
      }
      "INVOICE" -> {
        onInvoiceStatusUpdated(res.targetId, InvoiceStatus.PAYMENT_SUBMITTED)
      }
      "CONSULTATION" -> {
        onAppointmentStatusUpdated(res.targetId, AppointmentStatus.PENDING_CONFIRMATION)
      }
    }

    onNotificationTrigger(
      NotificationType.PAYMENT,
      "Payment Proof Uploaded",
      "Proof of payment for ${res.referenceNumber} has been received and routed to Accounts for verification.",
      "payment_details/${res.id}"
    )

    return Result.success(res)
  }

  override suspend fun simulateOfficeDecision(
    paymentId: String,
    approve: Boolean,
    reason: String?
  ): Result<PaymentDetails> {
    var updated: PaymentDetails? = null
    _payments.update { list ->
      list.map { p ->
        if (p.id == paymentId || p.referenceNumber == paymentId) {
          val now = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date())
          val up = if (approve) {
            p.copy(
              status = PaymentVerificationStatus.APPROVED,
              verifiedAt = now,
              rejectionReason = null
            )
          } else {
            p.copy(
              status = PaymentVerificationStatus.REJECTED,
              verifiedAt = now,
              rejectionReason = reason ?: "Transaction reference does not match bank/mobile statement. Please upload clear receipt."
            )
          }
          updated = up
          up
        } else p
      }
    }

    val res = updated ?: return Result.failure(IllegalArgumentException("Payment record not found"))

    if (approve) {
      when (res.targetType) {
        "REQUEST" -> {
          onRequestStatusUpdated(
            res.targetId,
            RequestStatus.PAYMENT_APPROVED,
            "Payment Approved. Request is currently awaiting office processing by Et Cetra chambers."
          )
        }
        "INVOICE" -> {
          onInvoiceStatusUpdated(res.targetId, InvoiceStatus.PAID)
        }
        "CONSULTATION" -> {
          onAppointmentStatusUpdated(res.targetId, AppointmentStatus.UPCOMING)
        }
      }
      onNotificationTrigger(
        NotificationType.PAYMENT,
        "Payment Approved: ${res.referenceNumber}",
        "Your payment has been verified by the Accounts Dept. Et Cetra Advocates is processing your file.",
        "payment_details/${res.id}"
      )
    } else {
      // Rejection: Return to repayable state!
      when (res.targetType) {
        "REQUEST" -> {
          onRequestStatusUpdated(
            res.targetId,
            RequestStatus.PAYMENT_REQUIRED,
            "Payment proof was rejected: ${res.rejectionReason}. Please re-submit proof or make payment."
          )
        }
        "INVOICE" -> {
          onInvoiceStatusUpdated(res.targetId, InvoiceStatus.REJECTED)
        }
        "CONSULTATION" -> {
          onAppointmentStatusUpdated(res.targetId, AppointmentStatus.PAYMENT_REQUIRED)
        }
      }
      onNotificationTrigger(
        NotificationType.PAYMENT,
        "Payment Verification Rejected",
        "Verification for ${res.referenceNumber} was unsuccessful. You may re-submit proof or repay.",
        "payment_details/${res.id}"
      )
    }

    return Result.success(res)
  }
}

class MockInvoiceRepository : InvoiceRepository {
  private val _invoices = MutableStateFlow<List<LegalInvoice>>(
    listOf(
      LegalInvoice(
        id = "INV-2026-089",
        invoiceNumber = "ETA/INV/2026/089",
        title = "Initial Legal Assessment & Trademark Advisory",
        description = "Intake assessment fee for trademark infringement cease & desist proceedings (Ref: REQ-2026-002).",
        items = listOf(
          InvoiceItem("Intake & Conflict Check Fee", 1, 300_000, 300_000),
          InvoiceItem("Preliminary Legal Search & Analysis", 1, 200_000, 200_000)
        ),
        subtotalTzs = 500_000,
        vatTzs = 0,
        totalTzs = 500_000,
        issueDate = "Sep 22, 2026",
        dueDate = "Sep 29, 2026",
        status = InvoiceStatus.UNPAID,
        relatedRequestId = "REQ-2026-002"
      ),
      LegalInvoice(
        id = "INV-2026-079",
        invoiceNumber = "ETA/INV/2026/079",
        title = "Commercial Lease Legal Assessment Fee",
        description = "Intake assessment and preliminary notice preparation (Ref: REQ-2026-001).",
        items = listOf(
          InvoiceItem("Initial Lease Dispute Assessment", 1, 500_000, 500_000)
        ),
        subtotalTzs = 500_000,
        vatTzs = 0,
        totalTzs = 500_000,
        issueDate = "Sep 18, 2026",
        dueDate = "Sep 25, 2026",
        status = InvoiceStatus.PAID,
        relatedRequestId = "REQ-2026-001"
      ),
      LegalInvoice(
        id = "INV-2026-074",
        invoiceNumber = "ETA/INV/2026/074",
        title = "Commercial Litigation Retainer & Court Filing",
        description = "Formal advocacy retainer fee for High Court Commercial Suit No. 118 of 2026 (Matter: ETA/LIT/2026/007).",
        items = listOf(
          InvoiceItem("Advocate Litigation Retainer Fee", 1, 2_000_000, 2_000_000),
          InvoiceItem("Court Filing Fees & Disbursements", 1, 500_000, 500_000)
        ),
        subtotalTzs = 2_500_000,
        vatTzs = 0,
        totalTzs = 2_500_000,
        issueDate = "May 06, 2026",
        dueDate = "May 20, 2026",
        status = InvoiceStatus.PAID,
        relatedMatterId = "MAT-2026-007"
      ),
      LegalInvoice(
        id = "INV-2026-061",
        invoiceNumber = "ETA/INV/2026/061",
        title = "Partner Advisory Consultation Fee",
        description = "Advisory session on Corporate Structuring & Compliance.",
        items = listOf(
          InvoiceItem("Senior Partner Legal Consultation (1 hr)", 1, 150_000, 150_000)
        ),
        subtotalTzs = 150_000,
        vatTzs = 0,
        totalTzs = 150_000,
        issueDate = "Aug 02, 2026",
        dueDate = "Aug 05, 2026",
        status = InvoiceStatus.PAID
      )
    )
  )
  override val invoices: StateFlow<List<LegalInvoice>> = _invoices.asStateFlow()

  override suspend fun getInvoiceById(id: String): LegalInvoice? {
    return _invoices.value.find { it.id == id || it.invoiceNumber == id }
  }

  fun createInvoiceForRequest(requestId: String, title: String, amount: Long): String {
    val count = _invoices.value.size + 1
    val invId = "INV-2026-09$count"
    val inv = LegalInvoice(
      id = invId,
      invoiceNumber = "ETA/INV/2026/09$count",
      title = "Legal Assessment Fee: $title",
      description = "Required legal assessment fee before proceeding to matter creation.",
      items = listOf(InvoiceItem("Intake & Conflict Check Assessment", 1, amount, amount)),
      subtotalTzs = amount,
      vatTzs = 0,
      totalTzs = amount,
      issueDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
      dueDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(System.currentTimeMillis() + 7L * 86400000L)),
      status = InvoiceStatus.UNPAID,
      relatedRequestId = requestId
    )
    _invoices.update { listOf(inv) + it }
    return invId
  }

  fun createInvoiceForConsultation(appointmentId: String, advocateName: String, fee: Long): String {
    val count = _invoices.value.size + 1
    val invId = "INV-2026-CON-$count"
    val inv = LegalInvoice(
      id = invId,
      invoiceNumber = "ETA/INV/2026/C$count",
      title = "Consultation Fee - $advocateName",
      description = "Official consultation fee for advisory booking.",
      items = listOf(InvoiceItem("Advocate Advisory Consultation", 1, fee, fee)),
      subtotalTzs = fee,
      vatTzs = 0,
      totalTzs = fee,
      issueDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date()),
      dueDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(System.currentTimeMillis() + 3L * 86400000L)),
      status = InvoiceStatus.UNPAID,
      relatedAppointmentId = appointmentId
    )
    _invoices.update { listOf(inv) + it }
    return invId
  }

  override suspend fun markInvoicePaymentSubmitted(invoiceId: String) {
    _invoices.update { list ->
      list.map { if (it.id == invoiceId) it.copy(status = InvoiceStatus.PAYMENT_SUBMITTED) else it }
    }
  }

  override suspend fun markInvoicePaid(invoiceId: String) {
    _invoices.update { list ->
      list.map { if (it.id == invoiceId) it.copy(status = InvoiceStatus.PAID) else it }
    }
  }

  override suspend fun markInvoiceRejected(invoiceId: String) {
    _invoices.update { list ->
      list.map { if (it.id == invoiceId) it.copy(status = InvoiceStatus.REJECTED) else it }
    }
  }
}

class MockMessageRepository(
  private val onNotificationTrigger: (NotificationType, String, String, String?) -> Unit
) : MessageRepository {

  private val _conversations = MutableStateFlow<List<Conversation>>(
    listOf(
      Conversation(
        id = "CONV-001",
        title = "Et Cetra Chambers - Commercial Lease Dispute",
        subtitle = "Related to Request REQ-2026-001 (Payment Approved)",
        relatedRequestId = "REQ-2026-001",
        lastMessageText = "We have received your approved payment proof. Adv. Beatrice Mbowe is reviewing the tenancy covenants.",
        lastMessageTimestamp = "Sep 19, 15:30",
        unreadCount = 1,
        advocateName = "Adv. Beatrice Mbowe"
      ),
      Conversation(
        id = "CONV-002",
        title = "ETA/CORP/2026/019 - Shareholder Structuring",
        subtitle = "Formal Matter Workspace",
        relatedMatterId = "MAT-2026-019",
        lastMessageText = "Please review the minority shareholder clause in section 4.2 before tomorrow's filing.",
        lastMessageTimestamp = "Yesterday",
        unreadCount = 0,
        advocateName = "Adv. Anthony Kimaro (Partner)"
      ),
      Conversation(
        id = "CONV-003",
        title = "Chambers General Client Helpdesk",
        subtitle = "Et Cetra Advocates Company Limited",
        lastMessageText = "Welcome to the Et Cetra legal client portal. Let us know if you need assistance.",
        lastMessageTimestamp = "Sep 10",
        unreadCount = 0,
        advocateName = "Chambers Registry"
      )
    )
  )
  override val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

  private val _messagesMap = MutableStateFlow<Map<String, List<ChatMessage>>>(
    mapOf(
      "CONV-001" to listOf(
        ChatMessage("M1", "CONV-001", "USR-CLIENT-001", "Anthony Sawe", true, "Hello Et Cetra team, I have completed the payment for REQ-2026-001 lease dispute.", "Sep 19, 11:25 AM"),
        ChatMessage("M2", "CONV-001", "ADV-001", "Adv. Beatrice Mbowe", false, "Good day Mr. Sawe. The accounts desk has verified the transaction. We are compiling the formal Notice of Distraint.", "Sep 19, 02:50 PM"),
        ChatMessage("M3", "CONV-001", "ADV-001", "Adv. Beatrice Mbowe", false, "We have received your approved payment proof. Adv. Beatrice Mbowe is reviewing the tenancy covenants.", "Sep 19, 03:30 PM")
      ),
      "CONV-002" to listOf(
        ChatMessage("M4", "CONV-002", "ADV-002", "Adv. Anthony Kimaro", false, "Draft 3 of the Shareholders Agreement has been uploaded to the document repository.", "Yesterday, 10:15 AM"),
        ChatMessage("M5", "CONV-002", "USR-CLIENT-001", "Anthony Sawe", true, "Thank you Counsel. I will review the dilution safeguard paragraphs.", "Yesterday, 11:40 AM"),
        ChatMessage("M6", "CONV-002", "ADV-002", "Adv. Anthony Kimaro", false, "Please review the minority shareholder clause in section 4.2 before tomorrow's filing.", "Yesterday, 04:12 PM")
      ),
      "CONV-003" to listOf(
        ChatMessage("M7", "CONV-003", "REG-001", "Chambers Registry", false, "Welcome to the Et Cetra legal client portal. Let us know if you need assistance.", "Sep 10, 09:00 AM")
      )
    )
  )

  override fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>> {
    return _messagesMap.map { it[convId] ?: emptyList() }
  }

  override suspend fun sendMessage(
    convId: String,
    text: String,
    attachmentName: String?
  ): Result<ChatMessage> {
    val now = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    val newMsg = ChatMessage(
      id = "MSG-${UUID.randomUUID().toString().take(6)}",
      conversationId = convId,
      senderId = "USR-CLIENT-001",
      senderName = "Anthony Sawe",
      isFromClient = true,
      messageText = text,
      timestamp = now,
      attachedDocumentName = attachmentName
    )

    val currentList = _messagesMap.value[convId] ?: emptyList()
    val updatedList = currentList + newMsg
    _messagesMap.update { map -> map + (convId to updatedList) }

    _conversations.update { list ->
      list.map { conv ->
        if (conv.id == convId) {
          conv.copy(
            lastMessageText = text,
            lastMessageTimestamp = "Just now"
          )
        } else conv
      }
    }

    return Result.success(newMsg)
  }

  override suspend fun getOrCreateConversationForRequest(requestId: String, title: String): String {
    val existing = _conversations.value.find { it.relatedRequestId == requestId }
    if (existing != null) return existing.id

    val convId = "CONV-REQ-${requestId.takeLast(3)}"
    val newConv = Conversation(
      id = convId,
      title = "Legal Counsel - $title",
      subtitle = "Case inquiry for Request $requestId",
      relatedRequestId = requestId,
      lastMessageText = "Chat opened regarding request $requestId.",
      lastMessageTimestamp = "Just now",
      unreadCount = 0,
      advocateName = "Et Cetra Legal Chambers"
    )
    _conversations.update { listOf(newConv) + it }
    _messagesMap.update { map ->
      map + (convId to listOf(
        ChatMessage("INIT", convId, "ADV-CHAMBERS", "Chambers Registry", false, "Hello, you are connected to Et Cetra Advocates regarding Request $requestId. How can we assist?", "Just now")
      ))
    }
    return convId
  }

  override suspend fun getOrCreateConversationForMatter(matterId: String, title: String): String {
    val existing = _conversations.value.find { it.relatedMatterId == matterId }
    if (existing != null) return existing.id

    val convId = "CONV-MAT-${matterId.takeLast(3)}"
    val newConv = Conversation(
      id = convId,
      title = title,
      subtitle = "Matter Workspace",
      relatedMatterId = matterId,
      lastMessageText = "Chambers consultation channel opened for matter $matterId.",
      lastMessageTimestamp = "Just now",
      unreadCount = 0,
      advocateName = "Lead Advocate"
    )
    _conversations.update { listOf(newConv) + it }
    return convId
  }
}

class MockAppointmentRepository(
  private val onConsultationInvoiceCreated: (appointmentId: String, advocateName: String, fee: Long) -> String,
  private val onNotificationTrigger: (NotificationType, String, String, String?) -> Unit
) : AppointmentRepository {

  private val _appointments = MutableStateFlow<List<ConsultationAppointment>>(
    listOf(
      ConsultationAppointment(
        id = "APT-2026-001",
        advocateName = "Adv. Beatrice Mbowe",
        advocateTitle = "Senior Partner - Commercial Litigation",
        practiceArea = LegalCategory.CONVEYANCING,
        mode = ConsultationMode.IN_PERSON_OFFICE,
        appointmentDate = "Oct 05, 2026",
        timeSlot = "10:00 AM - 11:00 AM",
        locationOrLink = "Et Cetra Chambers, 7th Floor, Boardroom A, Posta House, Dar es Salaam",
        agenda = "Strategy briefing on commercial lease default and imminent court filings.",
        feeTzs = 150_000,
        status = AppointmentStatus.UPCOMING,
        relatedInvoiceId = "INV-2026-061"
      ),
      ConsultationAppointment(
        id = "APT-2026-002",
        advocateName = "Adv. Anthony Kimaro",
        advocateTitle = "Managing Partner - Corporate Advisory",
        practiceArea = LegalCategory.CORPORATE,
        mode = ConsultationMode.VIRTUAL_SECURE_VIDEO,
        appointmentDate = "Aug 02, 2026",
        timeSlot = "02:30 PM - 03:30 PM",
        locationOrLink = "https://meet.etcetraadvocates.co.tz/chambers-secure/corp-019",
        agenda = "Preliminary review of proposed shareholder restructure.",
        feeTzs = 150_000,
        status = AppointmentStatus.COMPLETED,
        relatedInvoiceId = "INV-2026-061"
      )
    )
  )
  override val appointments: StateFlow<List<ConsultationAppointment>> = _appointments.asStateFlow()

  override suspend fun getAppointmentById(id: String): ConsultationAppointment? {
    return _appointments.value.find { it.id == id }
  }

  override suspend fun bookConsultation(
    advocateName: String,
    advocateTitle: String,
    category: LegalCategory,
    mode: ConsultationMode,
    date: String,
    time: String,
    agenda: String
  ): Result<ConsultationAppointment> {
    val count = _appointments.value.size + 1
    val aptId = "APT-2026-00$count"
    val fee = 150_000L
    val invId = onConsultationInvoiceCreated(aptId, advocateName, fee)

    val location = if (mode == ConsultationMode.IN_PERSON_OFFICE) {
      "Et Cetra Chambers, 7th Floor, Posta House, Dar es Salaam"
    } else {
      "https://meet.etcetraadvocates.co.tz/chambers-secure/$aptId"
    }

    val apt = ConsultationAppointment(
      id = aptId,
      advocateName = advocateName,
      advocateTitle = advocateTitle,
      practiceArea = category,
      mode = mode,
      appointmentDate = date,
      timeSlot = time,
      locationOrLink = location,
      agenda = agenda,
      feeTzs = fee,
      status = AppointmentStatus.PAYMENT_REQUIRED,
      relatedInvoiceId = invId
    )

    _appointments.update { listOf(apt) + it }
    onNotificationTrigger(
      NotificationType.APPOINTMENT,
      "Consultation Scheduled - Payment Required",
      "Consultation booking with $advocateName on $date ($time) created. Please complete payment to confirm.",
      "appointment_details/$aptId"
    )

    return Result.success(apt)
  }

  override suspend fun updateAppointmentStatus(id: String, status: AppointmentStatus) {
    _appointments.update { list ->
      list.map { if (it.id == id) it.copy(status = status) else it }
    }
  }
}

class MockDocumentRepository(
  private val onNotificationTrigger: (NotificationType, String, String, String?) -> Unit
) : DocumentRepository {

  private val _documents = MutableStateFlow<List<LegalDocument>>(
    listOf(
      LegalDocument(
        id = "DOC-001",
        name = "Commercial_Lease_Contract_VictoriaPlaza.pdf",
        category = DocumentCategory.EVIDENCE,
        sizeBytes = 2_450_000,
        uploadedDate = "Sep 18, 2026",
        isVerifiedByFirm = true,
        relatedRequestId = "REQ-2026-001"
      ),
      LegalDocument(
        id = "DOC-002",
        name = "Default_Notice_Demand_Letter.pdf",
        category = DocumentCategory.PLEADINGS,
        sizeBytes = 850_000,
        uploadedDate = "Sep 18, 2026",
        isVerifiedByFirm = true,
        relatedRequestId = "REQ-2026-001"
      ),
      LegalDocument(
        id = "DOC-003",
        name = "Commercial_Court_Plaint_Suit118.pdf",
        category = DocumentCategory.PLEADINGS,
        sizeBytes = 3_120_000,
        uploadedDate = "May 10, 2026",
        isVerifiedByFirm = true,
        relatedMatterId = "MAT-2026-007"
      ),
      LegalDocument(
        id = "DOC-004",
        name = "Draft3_Shareholders_Agreement_Final.pdf",
        category = DocumentCategory.BRIEFS,
        sizeBytes = 1_850_000,
        uploadedDate = "Sep 15, 2026",
        isVerifiedByFirm = true,
        relatedMatterId = "MAT-2026-019"
      ),
      LegalDocument(
        id = "DOC-005",
        name = "Certificate_of_Incorporation_BRELA.pdf",
        category = DocumentCategory.KYC,
        sizeBytes = 980_000,
        uploadedDate = "Aug 10, 2026",
        isVerifiedByFirm = true,
        relatedMatterId = "MAT-2026-019"
      )
    )
  )
  override val documents: StateFlow<List<LegalDocument>> = _documents.asStateFlow()

  override suspend fun uploadDocument(
    name: String,
    category: DocumentCategory,
    sizeBytes: Long,
    relatedMatterId: String?,
    relatedRequestId: String?
  ): Result<LegalDocument> {
    val count = _documents.value.size + 1
    val now = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
    val doc = LegalDocument(
      id = "DOC-00$count",
      name = name,
      category = category,
      sizeBytes = sizeBytes,
      uploadedDate = now,
      isVerifiedByFirm = false,
      relatedMatterId = relatedMatterId,
      relatedRequestId = relatedRequestId
    )
    _documents.update { listOf(doc) + it }
    onNotificationTrigger(
      NotificationType.DOCUMENT,
      "Document Uploaded: $name",
      "Document submitted for chambers review and verification.",
      "documents"
    )
    return Result.success(doc)
  }
}

class MockNotificationRepository : NotificationRepository {
  private val _notifications = MutableStateFlow<List<AppNotification>>(
    listOf(
      AppNotification(
        id = "NOTIF-001",
        type = NotificationType.REQUEST,
        title = "Payment Approved — Awaiting Office Processing",
        message = "Payment for REQ-2026-001 has been approved. The file is currently awaiting lead counsel assignment.",
        timestamp = "Sep 19, 14:50",
        isRead = false,
        targetScreenRoute = "request_details/REQ-2026-001"
      ),
      AppNotification(
        id = "NOTIF-002",
        type = NotificationType.PAYMENT,
        title = "Invoice Generated: INV-2026-089",
        message = "Initial assessment fee required for Trademark Infringement Cease & Desist request.",
        timestamp = "Sep 22, 10:00",
        isRead = false,
        targetScreenRoute = "invoice_details/INV-2026-089"
      ),
      AppNotification(
        id = "NOTIF-003",
        type = NotificationType.APPOINTMENT,
        title = "Upcoming Consultation in Chambers",
        message = "Consultation with Adv. Beatrice Mbowe scheduled for Oct 05 at 10:00 AM at Posta House.",
        timestamp = "Sep 20, 09:15",
        isRead = true,
        targetScreenRoute = "appointment_details/APT-2026-001"
      ),
      AppNotification(
        id = "NOTIF-004",
        type = NotificationType.MESSAGE,
        title = "New Message from Chambers",
        message = "Adv. Beatrice Mbowe replied regarding your commercial tenancy file.",
        timestamp = "Sep 19, 15:30",
        isRead = false,
        targetScreenRoute = "conversation_details/CONV-001"
      ),
      AppNotification(
        id = "NOTIF-005",
        type = NotificationType.MATTER,
        title = "Matter Milestone Updated",
        message = "Milestone 'Draft Shareholders Agreement Review' marked completed for ETA/CORP/2026/019.",
        timestamp = "Sep 15, 16:20",
        isRead = true,
        targetScreenRoute = "matter_details/MAT-2026-019"
      )
    )
  )
  override val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

  override suspend fun markAsRead(id: String) {
    _notifications.update { list ->
      list.map { if (it.id == id) it.copy(isRead = true) else it }
    }
  }

  override suspend fun markAllAsRead() {
    _notifications.update { list ->
      list.map { it.copy(isRead = true) }
    }
  }

  override fun addNotification(
    type: NotificationType,
    title: String,
    message: String,
    route: String?
  ) {
    val count = _notifications.value.size + 1
    val now = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date())
    val notif = AppNotification(
      id = "NOTIF-00$count",
      type = type,
      title = title,
      message = message,
      timestamp = now,
      isRead = false,
      targetScreenRoute = route
    )
    _notifications.update { listOf(notif) + it }
  }
}

class MockSettingsRepository : SettingsRepository {
  private val _settings = MutableStateFlow(
    AppPreferences(
      isDarkMode = false,
      language = "en",
      notificationsEnabled = true,
      biometricLoginEnabled = false,
      fontScale = 1.0f,
      reducedMotion = false
    )
  )
  override val settings: StateFlow<AppPreferences> = _settings.asStateFlow()

  override suspend fun updateDarkMode(enabled: Boolean) {
    _settings.update { it.copy(isDarkMode = enabled) }
  }

  override suspend fun updateLanguage(langCode: String) {
    _settings.update { it.copy(language = langCode) }
  }

  override suspend fun updateNotifications(enabled: Boolean) {
    _settings.update { it.copy(notificationsEnabled = enabled) }
  }

  override suspend fun updateBiometric(enabled: Boolean) {
    _settings.update { it.copy(biometricLoginEnabled = enabled) }
  }

  override suspend fun updateFontScale(scale: Float) {
    _settings.update { it.copy(fontScale = scale) }
  }

  override suspend fun updateReducedMotion(enabled: Boolean) {
    _settings.update { it.copy(reducedMotion = enabled) }
  }
}

/**
 * LawChambersCoordinator connects the mock repositories together
 * so changes in Payment update Requests, Invoices, and Appointments seamlessly.
 */
object LawChambersCoordinator {
  val authRepository = MockAuthRepository()
  val notificationRepository = MockNotificationRepository()
  val settingsRepository = MockSettingsRepository()

  val invoiceRepository = MockInvoiceRepository()

  lateinit var requestRepository: MockRequestRepository
  lateinit var paymentRepository: MockPaymentRepository
  lateinit var appointmentRepository: MockAppointmentRepository

  val matterRepository = MockMatterRepository()
  val messageRepository = MockMessageRepository { type, title, msg, route ->
    notificationRepository.addNotification(type, title, msg, route)
  }
  val documentRepository = MockDocumentRepository { type, title, msg, route ->
    notificationRepository.addNotification(type, title, msg, route)
  }

  init {
    paymentRepository = MockPaymentRepository(
      onRequestStatusUpdated = { reqId, status, note ->
        requestRepository.updateRequestStatus(reqId, status, note)
      },
      onInvoiceStatusUpdated = { invId, status ->
        when (status) {
          InvoiceStatus.PAYMENT_SUBMITTED -> invoiceRepository.markInvoicePaymentSubmitted(invId)
          InvoiceStatus.PAID -> invoiceRepository.markInvoicePaid(invId)
          InvoiceStatus.REJECTED -> invoiceRepository.markInvoiceRejected(invId)
          else -> {}
        }
      },
      onAppointmentStatusUpdated = { aptId, status ->
        appointmentRepository.updateAppointmentStatus(aptId, status)
      },
      onNotificationTrigger = { type, title, msg, route ->
        notificationRepository.addNotification(type, title, msg, route)
      }
    )

    requestRepository = MockRequestRepository(
      onPaymentRequiredCreated = { reqId, title, amount ->
        val invId = invoiceRepository.createInvoiceForRequest(reqId, title, amount)
        paymentRepository.createPayment("REQUEST", reqId, amount)
        invId
      },
      onNotificationTrigger = { type, title, msg, route ->
        notificationRepository.addNotification(type, title, msg, route)
      }
    )

    appointmentRepository = MockAppointmentRepository(
      onConsultationInvoiceCreated = { aptId, advName, fee ->
        val invId = invoiceRepository.createInvoiceForConsultation(aptId, advName, fee)
        paymentRepository.createPayment("CONSULTATION", aptId, fee)
        invId
      },
      onNotificationTrigger = { type, title, msg, route ->
        notificationRepository.addNotification(type, title, msg, route)
      }
    )
  }
}
