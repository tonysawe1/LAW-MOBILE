package com.example.ui.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.LawGoldSecondary
import com.example.ui.theme.LawStatusApproved

@Composable
fun RequestSubmissionResultScreen(
  requestId: String,
  onNavigateToPayment: (String) -> Unit,
  onNavigateToRequestDetail: (String) -> Unit,
  onNavigateToHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(80.dp)
        .clip(CircleShape)
        .background(LawStatusApproved.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = null,
        tint = LawStatusApproved,
        modifier = Modifier.size(44.dp)
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = strings.submissionSuccess,
      style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Intake Reference: $requestId",
      style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(20.dp))

    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      color = MaterialTheme.colorScheme.surfaceVariant,
      border = androidx.compose.foundation.BorderStroke(1.dp, LawGoldSecondary)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Important Chambers Notice",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LawGoldSecondary)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = strings.requestNoticeDisclaimer,
          style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(
      onClick = { onNavigateToPayment(requestId) },
      modifier = Modifier
        .fillMaxWidth()
        .height(52.dp)
        .testTag("result_proceed_to_payment_btn"),
      shape = RoundedCornerShape(12.dp)
    ) {
      Icon(Icons.Default.Payment, contentDescription = null)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Proceed to Legal Fee Payment", style = MaterialTheme.typography.titleMedium)
    }

    Spacer(modifier = Modifier.height(12.dp))

    OutlinedButton(
      onClick = { onNavigateToRequestDetail(requestId) },
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("result_view_request_btn"),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text(strings.viewRequestAction)
    }

    Spacer(modifier = Modifier.height(12.dp))

    TextButton(
      onClick = onNavigateToHome,
      modifier = Modifier.testTag("result_return_home_btn")
    ) {
      Text("Return to Dashboard")
    }
  }
}
