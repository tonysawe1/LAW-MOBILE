package com.example.ui.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.RequestStatus
import com.example.ui.components.LawTopBar
import com.example.ui.components.RequestStatusBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.LawGoldSecondary
import com.example.ui.theme.LawStatusApproved
import com.example.ui.theme.LawStatusApprovedBg
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RequestDetailScreen(
  requestId: String,
  viewModel: RequestsViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToPayment: (String) -> Unit,
  onNavigateToContactOffice: (String) -> Unit,
  onNavigateToMatter: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val requests by viewModel.requests.collectAsState()
  val request = requests.find { it.id == requestId || it.referenceNumber == requestId }

  val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

  Scaffold(
    topBar = {
      LawTopBar(
        title = request?.referenceNumber ?: strings.requestsTitle,
        subtitle = "Legal Intake Particulars",
        showBack = true,
        onBackClick = onNavigateBack
      )
    }
  ) { padding ->
    if (request == null) {
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text("Request not found", style = MaterialTheme.typography.bodyLarge)
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
      // 1. Status & Header Card
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
              text = request.referenceNumber,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            RequestStatusBadge(status = request.status)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = request.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Category: ${request.category.title}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text("Urgency", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(modifier = Modifier.height(2.dp))
              UrgencyBadge(urgency = request.urgency)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Filed On", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(modifier = Modifier.height(2.dp))
              Text(request.submissionDate, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
            }
          }
        }
      }

      // 2. CRITICAL BUSINESS RULE NOTICE
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, LawGoldSecondary.copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.Top
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = LawGoldSecondary,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = strings.requestNoticeDisclaimer,
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = FontWeight.Medium,
              lineHeight = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // 3. SPECIAL STATE: PAYMENT APPROVED & AWAITING OFFICE PROCESSING
      if (request.status == RequestStatus.PAYMENT_APPROVED || request.status == RequestStatus.AWAITING_PROCESSING) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          color = LawStatusApprovedBg,
          border = androidx.compose.foundation.BorderStroke(1.dp, LawStatusApproved.copy(alpha = 0.3f))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = LawStatusApproved)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = strings.paymentApprovedAwaitingProcessing,
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = LawStatusApproved
                )
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Your intake payment has been verified by the Accounts Dept. Et Cetra Advocates Chambers is currently conducting conflicts clearance and assigning lead counsel to formally institute the Matter.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = {
                viewModel.contactOfficeForRequest(request.id, request.title) { convId ->
                  onNavigateToContactOffice(convId)
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = LawStatusApproved),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("request_contact_office_button")
            ) {
              Icon(Icons.Default.Chat, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text(strings.contactOffice)
            }
          }
        }
      }

      // 4. Case Facts & Description
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = strings.caseDescription,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = request.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
          )

          if (!request.opposingParty.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = strings.opposingParty,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = request.opposingParty,
              style = MaterialTheme.typography.bodyMedium
            )
          }

          if (!request.desiredOutcome.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = strings.desiredOutcome,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = request.desiredOutcome,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      }

      // 5. Attached Documents List
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "${strings.attachDocuments} (${request.attachedDocuments.size})",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(10.dp))

          if (request.attachedDocuments.isEmpty()) {
            Text(
              text = "No files attached to this intake.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          } else {
            request.attachedDocuments.forEach { doc ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Description,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = doc.name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                  )
                  Text(
                    text = "${doc.sizeBytes / 1024} KB • ${doc.uploadDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }

      // 6. Action: Payment Required Button
      if (request.status == RequestStatus.PAYMENT_REQUIRED) {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Assessment Fee Payable",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Amount: TZS ${currencyFormat.format(request.estimatedFeeTzs)}",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = { onNavigateToPayment(request.id) },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("request_detail_pay_now_button"),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Payment, contentDescription = null)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Proceed to Payment", style = MaterialTheme.typography.titleSmall)
            }
          }
        }
      }

      // 7. Action: If Accepted as Matter -> Link to Matter Details
      if (request.status == RequestStatus.ACCEPTED && !request.acceptedMatterId.isNullOrBlank()) {
        Button(
          onClick = { onNavigateToMatter(request.acceptedMatterId) },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("request_view_matter_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Open Official Matter Workspace")
          Spacer(modifier = Modifier.width(8.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
