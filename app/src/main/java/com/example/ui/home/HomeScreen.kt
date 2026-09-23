package com.example.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.localization.LocalAppStrings
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: HomeViewModel,
  isDarkMode: Boolean = false,
  onThemeToggle: ((Boolean) -> Unit)? = null,
  onNavigateToMakeRequest: () -> Unit,
  onNavigateToBookConsultation: () -> Unit,
  onNavigateToInvoices: () -> Unit,
  onNavigateToContactOffice: (String) -> Unit,
  onNavigateToRequestDetails: (String) -> Unit,
  onNavigateToMatterDetails: (String) -> Unit,
  onNavigateToPayment: (String) -> Unit,
  onNavigateToNotifications: () -> Unit,
  onNavigateToAppointments: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val state by viewModel.uiState.collectAsState()
  val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.firmName,
        subtitle = strings.firmTagline,
        isDarkMode = isDarkMode,
        onThemeToggle = onThemeToggle,
        unreadNotificationCount = state.unreadNotificationsCount,
        onNotificationClick = onNavigateToNotifications
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Classic Chambers Client Welcome Banner
      item {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)),
          shadowElevation = 1.dp
        ) {
          Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
              )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                modifier = Modifier.padding(bottom = 4.dp)
              ) {
                Text(
                  text = "CHAMBERS CLIENT PORTAL",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.secondary
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Text(
                text = "${strings.greeting}, ${state.currentUser?.fullName ?: "Valued Client"}",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Serif,
                  fontWeight = FontWeight.Bold,
                  fontSize = 17.sp
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
              if (!state.currentUser?.organization.isNullOrBlank()) {
                Text(
                  text = state.currentUser?.organization ?: "",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                )
              }
            }
          }
        }
      }

      // 2. Balanced 2x2 Metric Stat Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          MetricStatCard(
            title = strings.activeRequests,
            value = state.activeRequestsCount.toString(),
            subtitle = "Active legal intakes",
            icon = Icons.Default.Assignment,
            accentColor = MaterialTheme.colorScheme.primary,
            onClick = onNavigateToMakeRequest,
            testTag = "home_metric_requests",
            modifier = Modifier.weight(1f)
          )
          MetricStatCard(
            title = strings.activeMatters,
            value = state.activeMattersCount.toString(),
            subtitle = "Ongoing litigation & advisory",
            icon = Icons.Default.Gavel,
            accentColor = MaterialTheme.colorScheme.secondary,
            onClick = {},
            testTag = "home_metric_matters",
            modifier = Modifier.weight(1f)
          )
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          MetricStatCard(
            title = strings.pendingInvoices,
            value = state.pendingInvoicesCount.toString(),
            subtitle = if (state.pendingInvoicesTotalTzs > 0) "TZS ${currencyFormat.format(state.pendingInvoicesTotalTzs)}" else "All settled",
            icon = Icons.Default.ReceiptLong,
            accentColor = if (state.pendingInvoicesCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            onClick = onNavigateToInvoices,
            testTag = "home_metric_invoices",
            modifier = Modifier.weight(1f)
          )
          MetricStatCard(
            title = strings.unreadMessagesCount,
            value = state.unreadMessagesCount.toString(),
            subtitle = "Chambers messages",
            icon = Icons.Default.ChatBubble,
            accentColor = MaterialTheme.colorScheme.tertiary,
            onClick = {
              viewModel.getOrCreateGeneralOfficeChat { convId ->
                onNavigateToContactOffice(convId)
              }
            },
            testTag = "home_metric_messages",
            modifier = Modifier.weight(1f)
          )
        }
      }

      // 3. Outstanding Fee Note Banner (if any)
      if (state.pendingInvoicesCount > 0) {
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "${state.pendingInvoicesCount} Pending Fee Note(s)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onErrorContainer
                  )
                  Text(
                    text = "Outstanding balance: TZS ${currencyFormat.format(state.pendingInvoicesTotalTzs)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                  )
                }
                Button(
                  onClick = onNavigateToInvoices,
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                  Text("Pay Now", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
              }
            }
          }
        }
      }

      // 4. Quick Actions Grid
      item {
        Text(
          text = strings.quickActions,
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
        )
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          HomeActionCard(
            label = strings.actionMakeRequest,
            subtitle = "Submit intake",
            icon = Icons.Default.PostAdd,
            onClick = onNavigateToMakeRequest,
            testTag = "home_quick_action_request",
            modifier = Modifier.weight(1f)
          )
          HomeActionCard(
            label = strings.actionBookConsultation,
            subtitle = "Meet advocate",
            icon = Icons.Default.Event,
            onClick = onNavigateToBookConsultation,
            testTag = "home_quick_action_consultation",
            modifier = Modifier.weight(1f)
          )
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          HomeActionCard(
            label = strings.actionViewInvoices,
            subtitle = "Fee notes & receipts",
            icon = Icons.Default.Receipt,
            onClick = onNavigateToInvoices,
            testTag = "home_quick_action_invoices",
            modifier = Modifier.weight(1f)
          )
          HomeActionCard(
            label = strings.actionContactOffice,
            subtitle = "Direct message",
            icon = Icons.Default.SupportAgent,
            onClick = {
              viewModel.getOrCreateGeneralOfficeChat { convId ->
                onNavigateToContactOffice(convId)
              }
            },
            testTag = "home_quick_action_contact",
            modifier = Modifier.weight(1f)
          )
        }
      }

      // 5. Upcoming Consultation Spotlight
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = strings.upcomingConsultation,
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          )
          TextButton(onClick = onNavigateToAppointments) {
            Text(
              text = strings.all,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
              )
            )
          }
        }
      }

      item {
        val appt = state.upcomingAppointment
        if (appt != null) {
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onNavigateToAppointments() },
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
            shadowElevation = 0.5.dp
          ) {
            Row(modifier = Modifier.fillMaxWidth()) {
              // Classic Gold accent stripe
              Box(
                modifier = Modifier
                  .width(4.dp)
                  .fillMaxHeight()
                  .background(MaterialTheme.colorScheme.secondary)
              )
              Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = appt.advocateName,
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontFamily = FontFamily.Serif,
                      fontWeight = FontWeight.Bold
                    )
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                  ) {
                    Text(
                      text = if (appt.mode == ConsultationMode.IN_PERSON_OFFICE) "Office Visit" else "Virtual Call",
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                      ),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = appt.advocateTitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = MaterialTheme.colorScheme.secondary
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "${appt.appointmentDate} • ${appt.timeSlot}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }
        } else {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.EventAvailable,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Text(
                text = strings.noUpcomingConsultations,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // 6. Recent Legal Intakes Section
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = strings.requestsTitle,
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = FontFamily.Serif,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          )
        }
      }

      items(state.recentRequests) { request ->
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToRequestDetails(request.id) },
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
          shadowElevation = 0.5.dp
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = request.referenceNumber,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.primary
              )
              RequestStatusBadge(status = request.status)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = request.title,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = request.description,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )

            if (request.status == RequestStatus.PAYMENT_REQUIRED) {
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = { onNavigateToPayment(request.id) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
              ) {
                Text(
                  text = "Pay Assessment Fee",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
              }
            }
          }
        }
      }

      // 7. Active Legal Matters Section
      item {
        Text(
          text = strings.mattersTitle,
          style = MaterialTheme.typography.titleMedium.copy(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
        )
      }

      items(state.activeMatters) { matter ->
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToMatterDetails(matter.id) },
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surface,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
          shadowElevation = 0.5.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = matter.matterReference,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                  ),
                  color = MaterialTheme.colorScheme.secondary
                )
                MatterStatusBadge(status = matter.status)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = matter.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Counsel: ${matter.leadAdvocate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun HomeActionCard(
  label: String,
  subtitle: String,
  icon: ImageVector,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    shadowElevation = 0.5.dp,
    modifier = modifier.testTag(testTag)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = label,
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
