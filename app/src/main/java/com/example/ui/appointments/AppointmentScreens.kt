package com.example.ui.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppointmentStatus
import com.example.data.model.ConsultationAppointment
import com.example.data.model.ConsultationMode
import com.example.data.model.LegalCategory
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LawConfirmationDialog
import com.example.ui.components.LawTopBar
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AppointmentListScreen(
  viewModel: AppointmentsViewModel,
  isDarkMode: Boolean = false,
  onThemeToggle: ((Boolean) -> Unit)? = null,
  onNavigateToBook: () -> Unit,
  onNavigateToDetail: (String) -> Unit,
  onNavigateToPayment: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val appointments by viewModel.appointments.collectAsState()

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.appointmentsTitle,
        subtitle = "Chambers Legal Consultations",
        isDarkMode = isDarkMode,
        onThemeToggle = onThemeToggle
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = onNavigateToBook,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text(strings.bookConsultation) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("appointments_fab_book")
      )
    }
  ) { padding ->
    if (appointments.isEmpty()) {
      EmptyStateView(
        title = strings.noUpcomingConsultations,
        description = "You do not have any scheduled appointments. Book a consultation with our advocates.",
        icon = Icons.Default.CalendarMonth,
        actionLabel = strings.bookConsultation,
        onActionClick = onNavigateToBook,
        modifier = modifier.padding(padding)
      )
    } else {
      LazyColumn(
        modifier = modifier
          .fillMaxSize()
          .padding(padding),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(appointments) { appt ->
          AppointmentItemCard(
            appointment = appt,
            onClick = { onNavigateToDetail(appt.id) },
            onPayClick = { onNavigateToPayment(appt.id) }
          )
        }
      }
    }
  }
}

@Composable
fun AppointmentItemCard(
  appointment: ConsultationAppointment,
  onClick: () -> Unit,
  onPayClick: () -> Unit
) {
  val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("appointment_card_${appointment.id}"),
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    shadowElevation = 0.5.dp
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (appointment.mode == ConsultationMode.IN_PERSON_OFFICE) Icons.Default.LocationOn else Icons.Default.Videocam,
            contentDescription = null,
            tint = LawGoldSecondary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (appointment.mode == ConsultationMode.IN_PERSON_OFFICE) "In-Office" else "Virtual Chambers",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
          )
        }
        AppointmentStatusBadge(status = appointment.status)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = appointment.advocateName,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )

      Text(
        text = appointment.advocateTitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = LawNavyPrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${appointment.appointmentDate} • ${appointment.timeSlot}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
          )
        }

        Text(
          text = "TZS ${currencyFormat.format(appointment.feeTzs)}",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
      }

      if (appointment.status == AppointmentStatus.PAYMENT_REQUIRED) {
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = onPayClick,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .testTag("appt_pay_button_${appointment.id}")
        ) {
          Text("Pay Consultation Fee", style = MaterialTheme.typography.labelSmall)
        }
      }
    }
  }
}

@Composable
fun AppointmentStatusBadge(status: AppointmentStatus) {
  val (label, bgColor, textColor) = when (status) {
    AppointmentStatus.PAYMENT_REQUIRED -> Triple("Payment Due", LawStatusRejectedBg, LawStatusRejected)
    AppointmentStatus.PENDING_CONFIRMATION -> Triple("Under Verification", LawStatusActionBg, LawStatusAction)
    AppointmentStatus.UPCOMING -> Triple("Confirmed & Scheduled", LawStatusApprovedBg, LawStatusApproved)
    AppointmentStatus.COMPLETED -> Triple("Completed", LawStatusDraftBg, LawStatusDraft)
    AppointmentStatus.CANCELLED -> Triple("Cancelled", LawStatusRejectedBg, LawStatusRejected)
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = bgColor,
    contentColor = textColor
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
    )
  }
}

@Composable
fun AppointmentDetailScreen(
  appointmentId: String,
  viewModel: AppointmentsViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToPayment: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val appointments by viewModel.appointments.collectAsState()
  val appointment = appointments.find { it.id == appointmentId }
  val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

  var showCancelDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      LawTopBar(
        title = "Consultation Details",
        subtitle = appointment?.advocateName ?: strings.appointmentsTitle,
        showBack = true,
        onBackClick = onNavigateBack
      )
    }
  ) { padding ->
    if (appointment == null) {
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text("Appointment record not found.")
      }
      return@Scaffold
    }

    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shadowElevation = 1.dp
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Ref: ${appointment.id}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            AppointmentStatusBadge(status = appointment.status)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = appointment.advocateName,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )

          Text(
            text = appointment.advocateTitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(16.dp))

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

          Spacer(modifier = Modifier.height(16.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
              Text("Date & Time", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(appointment.appointmentDate, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
              Text(appointment.timeSlot, style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Consultation Mode", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(
                text = if (appointment.mode == ConsultationMode.IN_PERSON_OFFICE) "In-Person Office" else "Virtual Video Call",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text("Location / Video Link", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(
            text = appointment.locationOrLink,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
          )
        }
      }

      // Purpose / Agenda
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Consultation Agenda & Purpose",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = appointment.agenda,
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }

      // Fee card
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Professional Consultation Fee", style = MaterialTheme.typography.labelSmall)
            Text("TZS ${currencyFormat.format(appointment.feeTzs)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          }
          if (appointment.status == AppointmentStatus.PAYMENT_REQUIRED) {
            Button(
              onClick = { onNavigateToPayment(appointment.id) },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("appt_detail_pay_button")
            ) {
              Text("Pay Now")
            }
          }
        }
      }

      // Actions
      if (appointment.status == AppointmentStatus.UPCOMING || appointment.status == AppointmentStatus.PAYMENT_REQUIRED) {
        OutlinedButton(
          onClick = { showCancelDialog = true },
          colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
          modifier = Modifier.fillMaxWidth().height(48.dp).testTag("cancel_appointment_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Cancel Appointment")
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  if (showCancelDialog) {
    LawConfirmationDialog(
      title = "Cancel Consultation",
      message = "Are you sure you want to cancel this consultation with ${appointment?.advocateName}? Chambers policy advises giving at least 24 hours notice.",
      confirmButtonText = "Confirm Cancellation",
      dismissButtonText = "Keep Appointment",
      isDestructive = true,
      onConfirm = {
        showCancelDialog = false
        if (appointment != null) viewModel.cancelAppointment(appointment.id)
      },
      onDismiss = { showCancelDialog = false }
    )
  }
}

@Composable
fun BookConsultationScreen(
  viewModel: AppointmentsViewModel,
  onNavigateBack: () -> Unit,
  onBookSuccess: (ConsultationAppointment) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val draft by viewModel.bookingDraft.collectAsState()
  val isSubmitting by viewModel.isSubmitting.collectAsState()

  val advocates = listOf(
    Pair("Adv. Sarah Mwangi", "Managing Partner • Corporate & Commercial"),
    Pair("Adv. Josephat Kimaro", "Senior Litigation Counsel"),
    Pair("Adv. Neema Lyimo", "Conveyancing & Real Estate Specialist"),
    Pair("Adv. Baraka Temu", "Tax & Regulatory Counsel")
  )

  val timeSlots = listOf(
    "09:00 AM - 10:00 AM",
    "10:30 AM - 11:30 AM",
    "02:00 PM - 03:00 PM",
    "04:00 PM - 05:00 PM"
  )

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.bookConsultation,
        subtitle = "Schedule Counsel Conference",
        showBack = true,
        onBackClick = onNavigateBack
      )
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = "Select Practice Area",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )

      LegalCategory.values().take(4).forEach { cat ->
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.updateDraft(area = cat) },
          shape = RoundedCornerShape(10.dp),
          color = if (draft.practiceArea == cat) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (draft.practiceArea == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
          )
        ) {
          Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = draft.practiceArea == cat, onClick = { viewModel.updateDraft(area = cat) })
            Spacer(modifier = Modifier.width(8.dp))
            Text(cat.title, style = MaterialTheme.typography.bodyMedium)
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Select Advocate",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )

      advocates.forEach { (advName, advTitle) ->
        val isSelected = draft.advocateName == advName
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.updateDraft(advocate = advName, title = advTitle) },
          shape = RoundedCornerShape(10.dp),
          color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
          )
        ) {
          Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isSelected, onClick = { viewModel.updateDraft(advocate = advName, title = advTitle) })
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(advName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
              Text(advTitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Consultation Mode",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
          modifier = Modifier
            .weight(1f)
            .clickable { viewModel.updateDraft(mode = ConsultationMode.IN_PERSON_OFFICE) },
          shape = RoundedCornerShape(10.dp),
          color = if (draft.mode == ConsultationMode.IN_PERSON_OFFICE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              Icons.Default.LocationOn,
              contentDescription = null,
              tint = if (draft.mode == ConsultationMode.IN_PERSON_OFFICE) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              "In-Person Office",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (draft.mode == ConsultationMode.IN_PERSON_OFFICE) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
              )
            )
          }
        }

        Surface(
          modifier = Modifier
            .weight(1f)
            .clickable { viewModel.updateDraft(mode = ConsultationMode.VIRTUAL_SECURE_VIDEO) },
          shape = RoundedCornerShape(10.dp),
          color = if (draft.mode == ConsultationMode.VIRTUAL_SECURE_VIDEO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              Icons.Default.Videocam,
              contentDescription = null,
              tint = if (draft.mode == ConsultationMode.VIRTUAL_SECURE_VIDEO) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              "Virtual Chambers",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (draft.mode == ConsultationMode.VIRTUAL_SECURE_VIDEO) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Preferred Time Slot",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )

      timeSlots.forEach { slot ->
        val isSelected = draft.timeSlot == slot
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.updateDraft(slot = slot) },
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = isSelected, onClick = { viewModel.updateDraft(slot = slot) })
            Spacer(modifier = Modifier.width(8.dp))
            Text(slot, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      OutlinedTextField(
        value = draft.purpose,
        onValueChange = { viewModel.updateDraft(purpose = it) },
        label = { Text("Consultation Agenda / Brief Summary") },
        placeholder = { Text("Outline the key legal issues to discuss during the session...") },
        minLines = 3,
        modifier = Modifier.fillMaxWidth().testTag("consultation_purpose_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          viewModel.bookConsultation { appt ->
            onBookSuccess(appt)
          }
        },
        enabled = !isSubmitting,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("book_consultation_submit_btn"),
        shape = RoundedCornerShape(12.dp)
      ) {
        if (isSubmitting) {
          CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
        } else {
          Text("Confirm Booking (TZS 150,000)", style = MaterialTheme.typography.titleMedium)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
