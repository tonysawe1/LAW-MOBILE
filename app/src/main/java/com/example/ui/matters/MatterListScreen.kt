package com.example.ui.matters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.LegalMatter
import com.example.data.model.MatterStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LawTopBar
import com.example.ui.components.MatterStatusBadge
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.LawGoldSecondary

@Composable
fun MatterListScreen(
  viewModel: MattersViewModel,
  isDarkMode: Boolean = false,
  onThemeToggle: ((Boolean) -> Unit)? = null,
  onNavigateToMatterDetail: (String) -> Unit,
  onNavigateToNewRequest: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val matters by viewModel.filteredMatters.collectAsState()
  val selectedFilter by viewModel.selectedStatusFilter.collectAsState()

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.mattersTitle,
        subtitle = "Active Legal Matters & Court Representations",
        isDarkMode = isDarkMode,
        onThemeToggle = onThemeToggle
      )
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Filter row
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          FilterChip(
            selected = selectedFilter == null,
            onClick = { viewModel.setFilter(null) },
            label = { Text(strings.all) },
            modifier = Modifier.testTag("matter_filter_all")
          )
        }
        MatterStatus.values().forEach { status ->
          item {
            FilterChip(
              selected = selectedFilter == status,
              onClick = { viewModel.setFilter(status) },
              label = { Text(status.name.replace("_", " ")) },
              modifier = Modifier.testTag("matter_filter_${status.name}")
            )
          }
        }
      }

      if (matters.isEmpty()) {
        EmptyStateView(
          title = strings.emptyMattersTitle,
          description = strings.emptyMattersDesc,
          icon = Icons.Default.Gavel,
          actionLabel = strings.newRequest,
          onActionClick = onNavigateToNewRequest
        )
      } else {
        LazyColumn(
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(matters) { matter ->
            MatterItemCard(
              matter = matter,
              onClick = { onNavigateToMatterDetail(matter.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun MatterItemCard(
  matter: LegalMatter,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("matter_card_${matter.id}"),
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
          text = matter.matterReference,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = LawGoldSecondary
        )
        MatterStatusBadge(status = matter.status)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = matter.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Lead Counsel: ${matter.leadAdvocate}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      if (matter.courtForum.isNotBlank()) {
        Text(
          text = "Forum: ${matter.courtForum}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Status: ${matter.activeNotes}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }
    }
  }
}
