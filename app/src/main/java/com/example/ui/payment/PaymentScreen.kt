package com.example.ui.payment

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentMethodType
import com.example.data.model.PaymentVerificationStatus
import com.example.ui.components.LawTopBar
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
  paymentId: String,
  viewModel: PaymentViewModel,
  onNavigateBack: () -> Unit,
  onPaymentApprovedContinue: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val uiState by viewModel.uiState.collectAsState()

  LaunchedEffect(paymentId) {
    viewModel.loadPayment(paymentId)
  }

  val payment = uiState.paymentDetails
  val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.paymentTitle,
        subtitle = "ET CETRA ADVOCATES Accounts Desk",
        showBack = true,
        onBackClick = onNavigateBack
      )
    }
  ) { padding ->
    if (payment == null) {
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
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
      // 1. Amount & Reference Card
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 1.dp
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${strings.invoiceReference}: ${payment.referenceNumber}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            PaymentStatusBadge(status = payment.status)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = strings.amountDue,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "TZS ${currencyFormat.format(payment.amountTzs)}",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Target: ${payment.targetType} • ID: ${payment.targetId}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
          )
        }
      }

      // 2. Status Banner / Feedback
      when (payment.status) {
        PaymentVerificationStatus.APPROVED -> {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LawStatusApprovedBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, LawStatusApproved)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LawStatusApproved)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = strings.paymentApprovedTitle,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LawStatusApproved)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = strings.paymentApprovedDesc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
              if (!payment.verifiedAt.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Verified at: ${payment.verifiedAt} • Ref: ${payment.transactionReference ?: "N/A"}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = onPaymentApprovedContinue,
                colors = ButtonDefaults.buttonColors(containerColor = LawStatusApproved),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("payment_approved_continue_btn")
              ) {
                Text("Continue to File Details")
              }
            }
          }
        }

        PaymentVerificationStatus.PROOF_SUBMITTED, PaymentVerificationStatus.UNDER_VERIFICATION -> {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LawStatusActionBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, LawStatusAction)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = LawStatusAction)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = strings.underVerification,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LawStatusAction)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Proof: ${payment.proofUploadedName ?: "Receipt"} (Ref: ${payment.transactionReference ?: "Submitted"}). Accounts officers typically verify within 1-2 chambers business hours.",
                style = MaterialTheme.typography.bodySmall
              )
            }
          }
        }

        PaymentVerificationStatus.REJECTED -> {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = LawStatusRejectedBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, LawStatusRejected)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Cancel, contentDescription = null, tint = LawStatusRejected)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = strings.paymentRejectedTitle,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = LawStatusRejected)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = payment.rejectionReason ?: strings.paymentRejectedDesc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = { viewModel.openProofDialog() },
                colors = ButtonDefaults.buttonColors(containerColor = LawStatusRejected),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("payment_repay_button")
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(strings.repayNow)
              }
            }
          }
        }

        PaymentVerificationStatus.PENDING_SUBMISSION -> {
          // Standard instructions continue below
        }
      }

      // 3. Payment Method Selection
      Text(
        text = strings.paymentMethod,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MethodChip(
          title = "Lipa Number",
          icon = Icons.Default.PhoneAndroid,
          isSelected = uiState.selectedMethod == PaymentMethodType.LIPA_NUMBER,
          onClick = { viewModel.selectMethod(PaymentMethodType.LIPA_NUMBER) },
          testTag = "pay_method_lipa",
          modifier = Modifier.weight(1f)
        )
        MethodChip(
          title = "QR Code",
          icon = Icons.Default.QrCode2,
          isSelected = uiState.selectedMethod == PaymentMethodType.QR_CODE,
          onClick = { viewModel.selectMethod(PaymentMethodType.QR_CODE) },
          testTag = "pay_method_qr",
          modifier = Modifier.weight(1f)
        )
        MethodChip(
          title = "Bank Wire",
          icon = Icons.Default.AccountBalance,
          isSelected = uiState.selectedMethod == PaymentMethodType.BANK_ACCOUNT,
          onClick = { viewModel.selectMethod(PaymentMethodType.BANK_ACCOUNT) },
          testTag = "pay_method_bank",
          modifier = Modifier.weight(1f)
        )
      }

      // 4. Payment Channel Details & Instructions
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          when (uiState.selectedMethod) {
            PaymentMethodType.LIPA_NUMBER -> {
              Text(
                text = strings.lipaNumber,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = strings.lipaInstructions,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(16.dp))

              DetailRow("Till / Merchant Number", "5928341")
              DetailRow("Merchant Name", "ET CETRA ADVOCATES CO. LTD")
              DetailRow("Account / Reference", payment.referenceNumber)
              DetailRow("Amount", "TZS ${currencyFormat.format(payment.amountTzs)}")
            }

            PaymentMethodType.QR_CODE -> {
              Text(
                text = strings.qrCode,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = strings.qrInstructions,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(16.dp))

              // Authoritative QR Code graphic representation
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
              ) {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = Color.White,
                  border = androidx.compose.foundation.BorderStroke(2.dp, LawNavyPrimary),
                  shadowElevation = 2.dp,
                  modifier = Modifier.size(190.dp)
                ) {
                  Column(
                    modifier = Modifier.fillMaxSize().padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.QrCode,
                      contentDescription = "Authoritative Et Cetra QR Code",
                      tint = Color(0xFF0F172A),
                      modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "TILL 5928341 • ET CETRA",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        fontSize = 9.sp
                      )
                    )
                  }
                }
              }

              Text(
                text = "Authoritative Destination: Till 5928341 • Ref: ${payment.referenceNumber}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
              )
            }

            PaymentMethodType.BANK_ACCOUNT -> {
              Text(
                text = strings.bankAccount,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = strings.bankInstructions,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(16.dp))

              DetailRow("Bank Name", "Stanbic Bank Tanzania Limited")
              DetailRow("Account Name", "ET CETRA ADVOCATES COMPANY LIMITED")
              DetailRow("Account Number", "9120000481239")
              DetailRow("Branch", "Centre Branch, Dar es Salaam")
              DetailRow("Swift Code", "SBICXTZTZ")
              DetailRow("Reference to Quote", payment.referenceNumber)
            }
          }
        }
      }

      // 5. Action: Upload Payment Proof Button
      Button(
        onClick = { viewModel.openProofDialog() },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("payment_upload_proof_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.CloudUpload, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (payment.status == PaymentVerificationStatus.REJECTED) strings.repayNow else strings.uploadProof,
          style = MaterialTheme.typography.titleMedium
        )
      }

      // 6. PROTOCOL SIMULATION TOOLBAR (For immediate end-to-end prototype verification)
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Prototype Verification Controls",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = { viewModel.simulateOfficeApproval() },
              modifier = Modifier.weight(1f).testTag("simulate_payment_approval_btn"),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = LawStatusApproved),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Approve", style = MaterialTheme.typography.labelSmall)
            }

            OutlinedButton(
              onClick = { viewModel.simulateOfficeRejection() },
              modifier = Modifier.weight(1f).testTag("simulate_payment_rejection_btn"),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = LawStatusRejected),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Reject", style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Proof Upload Dialog
  if (uiState.showProofDialog && payment != null) {
    ProofUploadModal(
      reference = payment.referenceNumber,
      amount = "TZS ${currencyFormat.format(payment.amountTzs)}",
      isSubmitting = uiState.isSubmittingProof,
      onDismiss = { viewModel.closeProofDialog() },
      onSubmit = { proofName, txnId ->
        viewModel.submitProof(proofName, txnId)
      }
    )
  }
}

@Composable
fun MethodChip(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clickable(onClick = onClick)
      .testTag(testTag),
    shape = RoundedCornerShape(10.dp),
    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    )
  ) {
    Column(
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
fun DetailRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
      textAlign = TextAlign.End
    )
  }
}

@Composable
fun ProofUploadModal(
  reference: String,
  amount: String,
  isSubmitting: Boolean,
  onDismiss: () -> Unit,
  onSubmit: (String, String) -> Unit
) {
  var txnReference by remember { mutableStateOf("TXN-${(100000..999999).random()}TZ") }
  var selectedFile by remember { mutableStateOf("Mpesa_Payment_Confirmation_${reference.takeLast(4)}.pdf") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Submit Proof of Payment", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Ref: $reference • Amount: $amount",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = txnReference,
          onValueChange = { txnReference = it },
          label = { Text("Transaction Reference / Receipt #") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("proof_txn_input"),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Receipt, contentDescription = null, tint = LawGoldSecondary)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = selectedFile,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
              )
              Text(
                text = "Simulated bank/mobile money receipt",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onSubmit(selectedFile, txnReference) },
        enabled = txnReference.isNotBlank() && !isSubmitting,
        modifier = Modifier.testTag("proof_submit_confirm_btn")
      ) {
        if (isSubmitting) {
          CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        } else {
          Text("Submit Proof")
        }
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
