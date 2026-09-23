package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawTopBar(
  title: String,
  subtitle: String? = null,
  showBack: Boolean = false,
  onBackClick: (() -> Unit)? = null,
  isDarkMode: Boolean = false,
  onThemeToggle: ((Boolean) -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
  unreadNotificationCount: Int = 0,
  onNotificationClick: (() -> Unit)? = null
) {
  val strings = LocalAppStrings.current

  Column {
    TopAppBar(
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (!showBack) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
          }
          Column {
            Text(
              text = title,
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.sp
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
              Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp,
                  letterSpacing = 0.3.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      },
      navigationIcon = {
        if (showBack && onBackClick != null) {
          IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag("top_bar_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = strings.back
            )
          }
        }
      },
      actions = {
        // 1. Quick Light/Dark Mode Toggle
        if (onThemeToggle != null) {
          IconButton(
            onClick = { onThemeToggle(!isDarkMode) },
            modifier = Modifier.testTag("top_bar_theme_toggle")
          ) {
            Crossfade(targetState = isDarkMode, animationSpec = tween(300), label = "theme_crossfade") { dark ->
              if (dark) {
                Icon(
                  imageVector = Icons.Default.LightMode,
                  contentDescription = "Switch to Light Mode",
                  tint = MaterialTheme.colorScheme.primary
                )
              } else {
                Icon(
                  imageVector = Icons.Default.DarkMode,
                  contentDescription = "Switch to Dark Mode",
                  tint = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }

        // 2. Notifications Bell
        if (onNotificationClick != null) {
          IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.testTag("top_bar_notifications_button")
          ) {
            Box {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = strings.navNotifications
              )
              if (unreadNotificationCount > 0) {
                Box(
                  modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
                    .background(MaterialTheme.colorScheme.error, shape = CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                    color = MaterialTheme.colorScheme.onError,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
        actions()
      },
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
      )
    )
    HorizontalDivider(
      color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
      thickness = 0.5.dp
    )
  }
}

@Composable
fun LawCard(
  modifier: Modifier = Modifier,
  shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(14.dp),
  backgroundColor: Color = MaterialTheme.colorScheme.surface,
  borderColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val baseModifier = modifier
    .clip(shape)
    .background(backgroundColor)
    .border(0.75.dp, borderColor, shape)

  val clickableModifier = if (onClick != null) {
    baseModifier.clickable(onClick = onClick)
  } else {
    baseModifier
  }

  Column(
    modifier = clickableModifier.padding(16.dp),
    content = content
  )
}

@Composable
fun MetricStatCard(
  title: String,
  value: String,
  subtitle: String? = null,
  icon: ImageVector,
  accentColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "metric_stat_card"
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    shadowElevation = 0.5.dp,
    modifier = modifier.testTag(testTag)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
          )
        )
        Box(
          modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(accentColor.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      )
      if (subtitle != null) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }
  }
}

@Composable
fun RequestStatusBadge(status: RequestStatus, modifier: Modifier = Modifier) {
  val strings = LocalAppStrings.current
  val (label, bgColor, textColor) = when (status) {
    RequestStatus.DRAFT -> Triple(strings.statusDraft, LawStatusDraftBg, LawStatusDraft)
    RequestStatus.SUBMITTED -> Triple(strings.statusSubmitted, LawStatusPendingBg, LawStatusPending)
    RequestStatus.PAYMENT_REQUIRED -> Triple(strings.statusPaymentRequired, LawStatusRejectedBg, LawStatusRejected)
    RequestStatus.PAYMENT_PENDING -> Triple(strings.statusPaymentPending, LawStatusPendingBg, LawStatusPending)
    RequestStatus.PAYMENT_SUBMITTED -> Triple(strings.statusPaymentSubmitted, LawStatusActionBg, LawStatusAction)
    RequestStatus.PAYMENT_APPROVED -> Triple(strings.statusPaymentApproved, LawStatusApprovedBg, LawStatusApproved)
    RequestStatus.AWAITING_PROCESSING -> Triple(strings.statusAwaitingProcessing, LawStatusActionBg, LawStatusAction)
    RequestStatus.ACCEPTED -> Triple(strings.statusAccepted, LawStatusApprovedBg, LawStatusApproved)
    RequestStatus.REJECTED -> Triple(strings.statusRejected, LawStatusRejectedBg, LawStatusRejected)
  }

  Surface(
    modifier = modifier.clip(RoundedCornerShape(6.dp)),
    color = bgColor,
    contentColor = textColor,
    border = androidx.compose.foundation.BorderStroke(0.5.dp, textColor.copy(alpha = 0.3f))
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(textColor)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
      )
    }
  }
}

@Composable
fun PaymentStatusBadge(status: PaymentVerificationStatus, modifier: Modifier = Modifier) {
  val (label, bgColor, textColor) = when (status) {
    PaymentVerificationStatus.PENDING_SUBMISSION -> Triple("Payment Due", LawStatusPendingBg, LawStatusPending)
    PaymentVerificationStatus.PROOF_SUBMITTED -> Triple("Proof Submitted", LawStatusActionBg, LawStatusAction)
    PaymentVerificationStatus.UNDER_VERIFICATION -> Triple("Under Verification", LawStatusActionBg, LawStatusAction)
    PaymentVerificationStatus.APPROVED -> Triple("Payment Approved", LawStatusApprovedBg, LawStatusApproved)
    PaymentVerificationStatus.REJECTED -> Triple("Proof Rejected", LawStatusRejectedBg, LawStatusRejected)
  }

  Surface(
    modifier = modifier.clip(RoundedCornerShape(6.dp)),
    color = bgColor,
    contentColor = textColor,
    border = androidx.compose.foundation.BorderStroke(0.5.dp, textColor.copy(alpha = 0.3f))
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(textColor)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
      )
    }
  }
}

@Composable
fun InvoiceStatusBadge(status: InvoiceStatus, modifier: Modifier = Modifier) {
  val (label, bgColor, textColor) = when (status) {
    InvoiceStatus.UNPAID -> Triple("Unpaid", LawStatusRejectedBg, LawStatusRejected)
    InvoiceStatus.PAYMENT_SUBMITTED -> Triple("Under Verification", LawStatusActionBg, LawStatusAction)
    InvoiceStatus.PAID -> Triple("Paid & Settled", LawStatusApprovedBg, LawStatusApproved)
    InvoiceStatus.REJECTED -> Triple("Payment Rejected", LawStatusRejectedBg, LawStatusRejected)
  }

  Surface(
    modifier = modifier.clip(RoundedCornerShape(6.dp)),
    color = bgColor,
    contentColor = textColor,
    border = androidx.compose.foundation.BorderStroke(0.5.dp, textColor.copy(alpha = 0.3f))
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(textColor)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
      )
    }
  }
}

@Composable
fun MatterStatusBadge(status: MatterStatus, modifier: Modifier = Modifier) {
  val (label, bgColor, textColor) = when (status) {
    MatterStatus.ACTIVE -> Triple("Active In Chambers", LawStatusActionBg, LawStatusAction)
    MatterStatus.IN_COURT -> Triple("In Court Hearings", LawStatusPendingBg, LawStatusPending)
    MatterStatus.DISCOVERY -> Triple("Discovery Phase", LawStatusActionBg, LawStatusAction)
    MatterStatus.SETTLEMENT_NEGOTIATION -> Triple("Settlement Talks", LawStatusApprovedBg, LawStatusApproved)
    MatterStatus.CONCLUDED -> Triple("Concluded", LawStatusDraftBg, LawStatusDraft)
  }

  Surface(
    modifier = modifier.clip(RoundedCornerShape(6.dp)),
    color = bgColor,
    contentColor = textColor,
    border = androidx.compose.foundation.BorderStroke(0.5.dp, textColor.copy(alpha = 0.3f))
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(textColor)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
      )
    }
  }
}

@Composable
fun UrgencyBadge(urgency: UrgencyLevel, modifier: Modifier = Modifier) {
  val (label, color) = when (urgency) {
    UrgencyLevel.NORMAL -> "Standard" to LawSlateTertiary
    UrgencyLevel.URGENT -> "Urgent" to MaterialTheme.colorScheme.secondary
    UrgencyLevel.CRITICAL -> "Critical Priority" to MaterialTheme.colorScheme.error
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
  ) {
    Box(
      modifier = Modifier
        .size(8.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
      color = color
    )
  }
}

@Composable
fun EmptyStateView(
  title: String,
  description: String,
  icon: ImageVector,
  actionLabel: String? = null,
  onActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(76.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(38.dp)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = description,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 16.dp)
    )

    if (actionLabel != null && onActionClick != null) {
      Spacer(modifier = Modifier.height(24.dp))
      Button(
        onClick = onActionClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
      ) {
        Text(actionLabel)
      }
    }
  }
}

@Composable
fun LoadingView(message: String = "Loading...", modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(32.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      CircularProgressIndicator(
        color = MaterialTheme.colorScheme.secondary,
        strokeWidth = 3.dp
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun ErrorView(
  errorMessage: String,
  onRetry: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = strings.error,
      style = MaterialTheme.typography.titleMedium.copy(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold
      ),
      color = MaterialTheme.colorScheme.error
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = errorMessage,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(20.dp))
    OutlinedButton(
      onClick = onRetry,
      shape = RoundedCornerShape(12.dp)
    ) {
      Text(strings.retry)
    }
  }
}

@Composable
fun LawConfirmationDialog(
  title: String,
  message: String,
  confirmButtonText: String,
  dismissButtonText: String,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
  isDestructive: Boolean = false
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(16.dp),
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontFamily = FontFamily.Serif,
          fontWeight = FontWeight.Bold
        )
      )
    },
    text = {
      Text(text = message, style = MaterialTheme.typography.bodyMedium)
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        shape = RoundedCornerShape(10.dp),
        colors = if (isDestructive) {
          ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        } else {
          ButtonDefaults.buttonColors()
        }
      ) {
        Text(confirmButtonText)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(dismissButtonText)
      }
    }
  )
}
