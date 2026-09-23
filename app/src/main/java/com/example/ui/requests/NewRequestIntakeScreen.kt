package com.example.ui.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.data.model.LegalCategory
import com.example.data.model.UrgencyLevel
import com.example.ui.components.LawTopBar
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.LawGoldSecondary
import com.example.ui.theme.LawNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestIntakeScreen(
  viewModel: RequestsViewModel,
  onNavigateBack: () -> Unit,
  onSubmissionSuccess: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val currentStep by viewModel.currentStep.collectAsState()
  val draft by viewModel.currentDraft.collectAsState()
  val isSubmitting by viewModel.isSubmitting.collectAsState()

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.newRequest,
        subtitle = "Step $currentStep of 5: ${getStepTitle(currentStep, strings)}",
        showBack = true,
        onBackClick = {
          if (currentStep > 1) {
            viewModel.prevStep()
          } else {
            onNavigateBack()
          }
        }
      )
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Multi-Step Progress Tracker Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        for (step in 1..5) {
          val isCompleted = step < currentStep
          val isCurrent = step == currentStep

          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(
                when {
                  isCurrent -> MaterialTheme.colorScheme.primary
                  isCompleted -> LawGoldSecondary
                  else -> MaterialTheme.colorScheme.surfaceVariant
                }
              )
              .clickable {
                if (step < currentStep) viewModel.setStep(step)
              },
            contentAlignment = Alignment.Center
          ) {
            if (isCompleted) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(18.dp)
              )
            } else {
              Text(
                text = step.toString(),
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
            }
          }

          if (step < 5) {
            Box(
              modifier = Modifier
                .weight(1f)
                .height(3.dp)
                .background(
                  if (step < currentStep) LawGoldSecondary else MaterialTheme.colorScheme.outlineVariant
                )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Render Step Content
      when (currentStep) {
        1 -> Step1ClientInfo(draft, viewModel)
        2 -> Step2CategoryAndUrgency(draft, viewModel)
        3 -> Step3Details(draft, viewModel)
        4 -> Step4Attachments(draft, viewModel)
        5 -> Step5ReviewAndSubmit(draft)
      }

      Spacer(modifier = Modifier.height(28.dp))

      // Navigation Buttons
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        if (currentStep > 1) {
          OutlinedButton(
            onClick = { viewModel.prevStep() },
            modifier = Modifier
              .weight(1f)
              .height(50.dp)
              .testTag("request_step_back_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(strings.back)
          }
        }

        Button(
          onClick = {
            if (currentStep < 5) {
              viewModel.nextStep()
            } else {
              viewModel.submitRequest { reqId ->
                onSubmissionSuccess(reqId)
              }
            }
          },
          enabled = isStepValid(currentStep, draft) && !isSubmitting,
          modifier = Modifier
            .weight(if (currentStep > 1) 1.5f else 1f)
            .height(50.dp)
            .testTag(if (currentStep == 5) "request_intake_submit_button" else "request_step_next_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          if (isSubmitting) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
          } else {
            Text(if (currentStep == 5) strings.submit else strings.next)
          }
        }
      }
    }
  }
}

private fun getStepTitle(step: Int, strings: com.example.ui.localization.AppStrings): String = when (step) {
  1 -> strings.stepClientInfo
  2 -> strings.stepCategory
  3 -> strings.stepDescription
  4 -> strings.stepDocuments
  5 -> strings.stepReview
  else -> ""
}

private fun isStepValid(step: Int, draft: NewRequestDraft): Boolean = when (step) {
  1 -> draft.clientName.isNotBlank() && draft.clientPhone.isNotBlank() && draft.clientEmail.isNotBlank()
  2 -> true
  3 -> draft.title.isNotBlank() && draft.description.isNotBlank()
  4 -> true // Attachments are optional
  5 -> true
  else -> true
}

@Composable
fun Step1ClientInfo(draft: NewRequestDraft, viewModel: RequestsViewModel) {
  val strings = LocalAppStrings.current
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = strings.stepClientInfo,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "Verify your contact coordinates for all official chambers correspondence.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(18.dp))

    OutlinedTextField(
      value = draft.clientName,
      onValueChange = { viewModel.updateClientInfo(it, draft.clientPhone, draft.clientEmail) },
      label = { Text(strings.fullName) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("intake_client_name_input"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = draft.clientPhone,
      onValueChange = { viewModel.updateClientInfo(draft.clientName, it, draft.clientEmail) },
      label = { Text(strings.phoneNumber) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("intake_client_phone_input"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
      value = draft.clientEmail,
      onValueChange = { viewModel.updateClientInfo(draft.clientName, draft.clientPhone, it) },
      label = { Text(strings.email) },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("intake_client_email_input"),
      shape = RoundedCornerShape(12.dp)
    )
  }
}

@Composable
fun Step2CategoryAndUrgency(draft: NewRequestDraft, viewModel: RequestsViewModel) {
  val strings = LocalAppStrings.current
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = strings.legalCategory,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(12.dp))

    LegalCategory.values().forEach { category ->
      val isSelected = draft.category == category
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable { viewModel.updateCategoryAndUrgency(category, draft.urgency) }
          .testTag("intake_category_${category.name}"),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
          width = if (isSelected) 2.dp else 1.dp,
          color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          RadioButton(
            selected = isSelected,
            onClick = { viewModel.updateCategoryAndUrgency(category, draft.urgency) }
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = category.title,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = strings.urgencyLevel,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(12.dp))

    UrgencyLevel.values().forEach { urgency ->
      val isSelected = draft.urgency == urgency
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable { viewModel.updateCategoryAndUrgency(draft.category, urgency) }
          .testTag("intake_urgency_${urgency.name}"),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
          width = if (isSelected) 2.dp else 1.dp,
          color = if (isSelected) LawGoldSecondary else MaterialTheme.colorScheme.outlineVariant
        )
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          RadioButton(
            selected = isSelected,
            onClick = { viewModel.updateCategoryAndUrgency(draft.category, urgency) }
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = urgency.title,
            style = MaterialTheme.typography.bodyMedium.copy(
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          )
        }
      }
    }
  }
}

@Composable
fun Step3Details(draft: NewRequestDraft, viewModel: RequestsViewModel) {
  val strings = LocalAppStrings.current
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = "Matter Subject & Facts",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
      value = draft.title,
      onValueChange = { viewModel.updateDetails(it, draft.description, draft.opposingParty, draft.desiredOutcome) },
      label = { Text("Short Descriptive Title *") },
      placeholder = { Text("e.g. Breach of Commercial Tenancy Agreement") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("intake_title_input"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
      value = draft.description,
      onValueChange = { viewModel.updateDetails(draft.title, it, draft.opposingParty, draft.desiredOutcome) },
      label = { Text(strings.caseDescription + " *") },
      placeholder = { Text(strings.caseDescriptionHint) },
      minLines = 4,
      maxLines = 6,
      modifier = Modifier.fillMaxWidth().testTag("intake_description_input"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
      value = draft.opposingParty,
      onValueChange = { viewModel.updateDetails(draft.title, draft.description, it, draft.desiredOutcome) },
      label = { Text(strings.opposingParty) },
      placeholder = { Text("Individual or entity name (for conflict check)") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("intake_opposing_party_input"),
      shape = RoundedCornerShape(12.dp)
    )

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
      value = draft.desiredOutcome,
      onValueChange = { viewModel.updateDetails(draft.title, draft.description, draft.opposingParty, it) },
      label = { Text(strings.desiredOutcome) },
      placeholder = { Text("e.g. Specific performance, damages, injunctive relief") },
      minLines = 2,
      modifier = Modifier.fillMaxWidth().testTag("intake_desired_outcome_input"),
      shape = RoundedCornerShape(12.dp)
    )
  }
}

@Composable
fun Step4Attachments(draft: NewRequestDraft, viewModel: RequestsViewModel) {
  val strings = LocalAppStrings.current
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = strings.attachDocuments,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "Attach relevant contracts, statutory notices, communications, or evidence (PDF, PNG, JPG).",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Simulated file selector buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedButton(
        onClick = {
          viewModel.addSimulatedAttachment(
            name = "Contract_Agreement_Copy_${System.currentTimeMillis().toString().takeLast(3)}.pdf",
            size = 1_450_000,
            mime = "application/pdf"
          )
        },
        modifier = Modifier.weight(1f).testTag("intake_attach_contract_btn"),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.UploadFile, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Add PDF", style = MaterialTheme.typography.labelSmall)
      }

      OutlinedButton(
        onClick = {
          viewModel.addSimulatedAttachment(
            name = "Evidence_Photo_${System.currentTimeMillis().toString().takeLast(3)}.jpg",
            size = 980_000,
            mime = "image/jpeg"
          )
        },
        modifier = Modifier.weight(1f).testTag("intake_attach_photo_btn"),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.Image, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Add Photo", style = MaterialTheme.typography.labelSmall)
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (draft.attachedDocuments.isEmpty()) {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      ) {
        Text(
          text = "No files attached yet. Attachments are optional at intake stage.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(16.dp)
        )
      }
    } else {
      draft.attachedDocuments.forEach { doc ->
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surface,
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(text = doc.name, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
              Text(text = "${doc.sizeBytes / 1024} KB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { viewModel.removeAttachment(doc.id) }) {
              Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
            }
          }
        }
      }
    }
  }
}

@Composable
fun Step5ReviewAndSubmit(draft: NewRequestDraft) {
  val strings = LocalAppStrings.current
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = strings.stepReview,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = "Confirm intake particulars before transmitting to Et Cetra Chambers.",
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(16.dp))

    // MANDATORY BUSINESS DISCLAIMER
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surfaceVariant,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, LawGoldSecondary)
    ) {
      Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.Top
      ) {
        Icon(Icons.Default.WarningAmber, contentDescription = null, tint = LawGoldSecondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "Notice of Legal Assessment Fee",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LawNavyPrimary)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = strings.requestNoticeDisclaimer,
            style = MaterialTheme.typography.bodySmall.copy(
              lineHeight = 20.sp,
              fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      color = MaterialTheme.colorScheme.surface,
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
      Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        ReviewRow("Client", draft.clientName)
        ReviewRow("Contact", "${draft.clientPhone} • ${draft.clientEmail}")
        ReviewRow("Category", draft.category.title)
        ReviewRow("Urgency", draft.urgency.title)
        ReviewRow("Title", draft.title)
        ReviewRow("Description", draft.description)
        if (draft.opposingParty.isNotBlank()) ReviewRow("Opposing Party", draft.opposingParty)
        if (draft.desiredOutcome.isNotBlank()) ReviewRow("Desired Outcome", draft.desiredOutcome)
        ReviewRow("Attachments", "${draft.attachedDocuments.size} file(s)")
      }
    }
  }
}

@Composable
fun ReviewRow(label: String, value: String) {
  Column {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall
    )
  }
}
