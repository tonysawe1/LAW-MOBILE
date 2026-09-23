package com.example.ui.matters

import androidx.compose.foundation.background
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
import com.example.data.model.LegalMatter
import com.example.ui.components.LawTopBar
import com.example.ui.components.MatterStatusBadge
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.LawGoldSecondary
import com.example.ui.theme.LawNavyPrimary

@Composable
fun MatterDetailScreen(
  matterId: String,
  viewModel: MattersViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToContactCounsel: (String) -> Unit,
  onNavigateToRequest: (String) -> Unit,
  onNavigateToInvoices: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val matters by viewModel.matters.collectAsState()
  val matter = matters.find { it.id == matterId || it.matterReference == matterId }

  Scaffold(
    topBar = {
      LawTopBar(
        title = matter?.matterReference ?: strings.mattersTitle,
        subtitle = "Active Legal Matter File",
        showBack = true,
        onBackClick = onNavigateBack
      )
    }
  ) { padding ->
    if (matter == null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        contentAlignment = Alignment.Center
      ) {
        Text("Matter record not found.")
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
      // 1. Matter Header Card
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
              text = matter.matterReference,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            MatterStatusBadge(status = matter.status)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = matter.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Practice Area: ${matter.category.title}",
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
              Text("Court / Forum", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(matter.courtForum, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Date Commenced", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(matter.openedDate, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
            }
          }
        }
      }

      // 2. Lead Counsel Assigned & Contact Action
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
          }
          Spacer(modifier = Modifier.width(14.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Assigned Lead Counsel",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Text(
              text = matter.leadAdvocate,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }

          Button(
            onClick = {
              viewModel.contactCounsel(matter) { convId ->
                onNavigateToContactCounsel(convId)
              }
            },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("matter_contact_counsel_btn")
          ) {
            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Message")
          }
        }
      }

      // 3. Status Notes
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, LawGoldSecondary.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = LawGoldSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Current Status & Next Steps",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = matter.activeNotes,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
          )
        }
      }

      // 4. Opposing Party & Originating Request
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Matter Particulars",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Opposing Party: ${matter.opposingParty}",
            style = MaterialTheme.typography.bodyMedium
          )

          if (matter.relatedRequestId.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
              onClick = { onNavigateToRequest(matter.relatedRequestId) },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("matter_view_intake_btn")
            ) {
              Text("View Originating Request (${matter.relatedRequestId})")
            }
          }
        }
      }

      // 5. Activity Milestones
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Chambers Legal Milestones (${matter.milestones.size})",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(12.dp))

          matter.milestones.forEachIndexed { index, milestone ->
            Row(modifier = Modifier.fillMaxWidth()) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                  modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (milestone.isCompleted) LawNavyPrimary else LawGoldSecondary)
                )
                if (index < matter.milestones.size - 1) {
                  Box(
                    modifier = Modifier
                      .width(2.dp)
                      .height(44.dp)
                      .background(MaterialTheme.colorScheme.outlineVariant)
                  )
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = milestone.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = milestone.notes,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
