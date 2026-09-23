package com.example.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.AppNotification
import com.example.data.model.NotificationType
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LawTopBar
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.LawGoldSecondary
import com.example.ui.theme.LawNavyPrimary
import com.example.ui.theme.LawStatusApproved
import com.example.ui.theme.LawStatusRejected

@Composable
fun NotificationListScreen(
  viewModel: NotificationsViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToTarget: (NotificationType, String?) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val notifications by viewModel.notifications.collectAsState()
  val hasUnread = notifications.any { !it.isRead }

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.notificationsTitle,
        subtitle = "Chambers Legal Notices & Alerts",
        showBack = true,
        onBackClick = onNavigateBack,
        actions = {
          if (hasUnread) {
            TextButton(
              onClick = { viewModel.markAllAsRead() },
              modifier = Modifier.testTag("notifications_mark_all_read")
            ) {
              Text(strings.markAllAsRead)
            }
          }
        }
      )
    }
  ) { padding ->
    if (notifications.isEmpty()) {
      EmptyStateView(
        title = strings.emptyNotificationsTitle,
        description = strings.emptyNotificationsDesc,
        icon = Icons.Default.NotificationsNone,
        modifier = modifier.padding(padding)
      )
    } else {
      LazyColumn(
        modifier = modifier
          .fillMaxSize()
          .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(notifications) { notif ->
          NotificationItemCard(
            notification = notif,
            onClick = {
              viewModel.markAsRead(notif.id)
              onNavigateToTarget(notif.type, notif.targetScreenRoute)
            }
          )
        }
      }
    }
  }
}

@Composable
fun NotificationItemCard(
  notification: AppNotification,
  onClick: () -> Unit
) {
  val (icon, tint) = when (notification.type) {
    NotificationType.REQUEST -> Icons.Default.Assignment to LawNavyPrimary
    NotificationType.PAYMENT -> Icons.Default.Payment to LawGoldSecondary
    NotificationType.MATTER -> Icons.Default.Gavel to LawNavyPrimary
    NotificationType.APPOINTMENT -> Icons.Default.CalendarToday to LawGoldSecondary
    NotificationType.MESSAGE -> Icons.Default.ChatBubble to LawNavyPrimary
    NotificationType.DOCUMENT -> Icons.Default.Description to LawNavyPrimary
  }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("notif_card_${notification.id}"),
    shape = RoundedCornerShape(12.dp),
    color = if (!notification.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(
      width = if (!notification.isRead) 1.5.dp else 1.dp,
      color = if (!notification.isRead) LawGoldSecondary else MaterialTheme.colorScheme.outlineVariant
    ),
    shadowElevation = 0.5.dp
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(tint.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = notification.title,
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.SemiBold
            )
          )
          if (!notification.isRead) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(LawGoldSecondary)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = notification.message,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = notification.timestamp,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
      }
    }
  }
}
