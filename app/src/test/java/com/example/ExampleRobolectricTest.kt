package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.AppointmentStatus
import com.example.data.model.ConsultationMode
import com.example.data.model.LegalCategory
import com.example.data.model.UrgencyLevel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ET CETRA", appName)
  }

  @Test
  fun `verify mock coordinator initialization`() = runBlocking {
    val requests = LawChambersCoordinator.requestRepository.requests.value
    assertTrue(requests.isNotEmpty())

    val matters = LawChambersCoordinator.matterRepository.matters.value
    assertTrue(matters.isNotEmpty())

    val invoices = LawChambersCoordinator.invoiceRepository.invoices.value
    assertTrue(invoices.isNotEmpty())

    val appointments = LawChambersCoordinator.appointmentRepository.appointments.value
    assertTrue(appointments.isNotEmpty())
  }

  @Test
  fun `verify appointment booking flow`() = runBlocking {
    val result = LawChambersCoordinator.appointmentRepository.bookConsultation(
      advocateName = "Adv. Sarah Mwangi",
      advocateTitle = "Managing Partner",
      category = LegalCategory.CORPORATE,
      mode = ConsultationMode.IN_PERSON_OFFICE,
      date = "Oct 12, 2026",
      time = "10:00 AM",
      agenda = "Corporate restructuring advice"
    )
    assertTrue(result.isSuccess)
    val appt = result.getOrThrow()
    assertEquals(AppointmentStatus.PAYMENT_REQUIRED, appt.status)
  }

  @Test
  fun `verify legal intake creation flow`() = runBlocking {
    val result = LawChambersCoordinator.requestRepository.createRequest(
      clientName = "Anthony M. Sawe",
      clientPhone = "+255 754 123 456",
      clientEmail = "saweanthony9@gmail.com",
      category = LegalCategory.IP,
      urgency = UrgencyLevel.URGENT,
      title = "Trademark Infringement Notice",
      description = "Third party using conflicting trademark mark in local retail market.",
      opposingParty = "Acme Retail Ltd",
      desiredOutcome = "Cease and desist demand letter",
      attachments = emptyList()
    )
    assertTrue(result.isSuccess)
    val req = result.getOrThrow()
    assertNotNull(req.id)
  }
}
