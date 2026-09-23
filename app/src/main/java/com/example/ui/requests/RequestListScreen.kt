package com.example.ui.requests

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.LegalRequest
import com.example.data.model.RequestStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LawTopBar
import com.example.ui.components.RequestStatusBadge
import com.example.ui.components.UrgencyBadge
import com.example.ui.localization.LocalAppStrings

@Composable
fun RequestListScreen(
  viewModel: RequestsViewModel,
  isDarkMode: Boolean = false,
  onThemeToggle: ((Boolean) -> Unit)? = null,
  onNavigateToNewRequest: () -> Unit,
  onNavigateToRequestDetail: (String) -> Unit,
  onNavigateToPayment: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val requests by viewModel.filteredRequests.collectAsState()
  val selectedFilter by viewModel.selectedFilter.collectAsState()

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.requestsTitle,
        subtitle = "Intake Filings & Legal Assessments",
        isDarkMode = isDarkMode,
        onThemeToggle = onThemeToggle
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = onNavigateToNewRequest,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text(strings.newRequest) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("requests_fab_new")
      )
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Filter Chips
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          FilterChip(
            selected = selectedFilter == "ALL",
            onClick = { viewModel.setFilter("ALL") },
            label = { Text(strings.all) },
            modifier = Modifier.testTag("request_filter_all")
          )
        }
        item {
          FilterChip(
            selected = selectedFilter == "PAYMENT_DUE",
            onClick = { viewModel.setFilter("PAYMENT_DUE") },
            label = { Text("Payment Required") },
            modifier = Modifier.testTag("request_filter_payment")
          )
        }
        item {
          FilterChip(
            selected = selectedFilter == "PROCESSING",
            onClick = { viewModel.setFilter("PROCESSING") },
            label = { Text("In Processing") },
            modifier = Modifier.testTag("request_filter_processing")
          )
        }
        item {
          FilterChip(
            selected = selectedFilter == "ACCEPTED",
            onClick = { viewModel.setFilter("ACCEPTED") },
            label = { Text("Accepted Matters") },
            modifier = Modifier.testTag("request_filter_accepted")
          )
        }
      }

      if (requests.isEmpty()) {
        EmptyStateView(
          title = strings.emptyRequestsTitle,
          description = strings.emptyRequestsDesc,
          icon = Icons.Default.Assignment,
          actionLabel = strings.newRequest,
          onActionClick = onNavigateToNewRequest
        )
      } else {
        LazyColumn(
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(requests) { req ->
            RequestItemCard(
              request = req,
              onClick = { onNavigateToRequestDetail(req.id) },
              onPayClick = { onNavigateToPayment(req.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun RequestItemCard(
  request: LegalRequest,
  onClick: () -> Unit,
  onPayClick: () -> Unit
) {
  val strings = LocalAppStrings.current

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("request_card_${request.id}"),
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
        Text(
          text = request.referenceNumber,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
        RequestStatusBadge(status = request.status)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = request.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        maxLines = 2,
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

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        UrgencyBadge(urgency = request.urgency)

        Text(
          text = request.submissionDate,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      if (request.status == RequestStatus.PAYMENT_REQUIRED) {
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = onPayClick,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .testTag("request_pay_button_${request.id}")
        ) {
          Text("Pay Required Assessment Fee", style = MaterialTheme.typography.labelMedium)
        }
      }
    }
  }
}
