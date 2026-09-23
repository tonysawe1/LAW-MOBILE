package com.example.ui.navigation

sealed class Screen(val route: String) {
  // Auth
  object Welcome : Screen("welcome")
  object Login : Screen("login")
  object Register : Screen("register")
  object ForgotPassword : Screen("forgot_password")

  // Primary Tabs
  object Home : Screen("home")
  object Requests : Screen("requests")
  object Matters : Screen("matters")
  object Messages : Screen("messages")
  object Appointments : Screen("appointments")

  // Sub-flows
  object NewRequest : Screen("new_request")
  object RequestDetail : Screen("request_detail/{requestId}") {
    fun createRoute(requestId: String) = "request_detail/$requestId"
  }
  object RequestResult : Screen("request_result/{requestId}") {
    fun createRoute(requestId: String) = "request_result/$requestId"
  }

  object Payment : Screen("payment/{paymentId}") {
    fun createRoute(paymentId: String) = "payment/$paymentId"
  }

  object MatterDetail : Screen("matter_detail/{matterId}") {
    fun createRoute(matterId: String) = "matter_detail/$matterId"
  }

  object ConversationDetail : Screen("conversation_detail/{conversationId}") {
    fun createRoute(conversationId: String) = "conversation_detail/$conversationId"
  }

  object BookConsultation : Screen("book_consultation")
  object AppointmentDetail : Screen("appointment_detail/{appointmentId}") {
    fun createRoute(appointmentId: String) = "appointment_detail/$appointmentId"
  }

  object Documents : Screen("documents")
  object Invoices : Screen("invoices")
  object InvoiceDetail : Screen("invoice_detail/{invoiceId}") {
    fun createRoute(invoiceId: String) = "invoice_detail/$invoiceId"
  }
  object Notifications : Screen("notifications")
  object Profile : Screen("profile")
  object Settings : Screen("settings")
}
