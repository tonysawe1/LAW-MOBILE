package com.example.ui.invoices

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.InvoiceStatus
import com.example.data.model.LegalInvoice
import com.example.ui.components.EmptyStateView
import com.example.ui.components.InvoiceStatusBadge
import com.example.ui.components.LawTopBar
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.LawGoldSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun InvoiceListScreen(
  viewModel: InvoicesViewModel,
  onNavigateToInvoiceDetail: (String) -> Unit,
  onNavigateToPayment: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val invoices by viewModel.filteredInvoices.collectAsState()
  val selectedFilter by viewModel.selectedStatusFilter.collectAsState()

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.invoicesTitle,
        subtitle = "Chambers Fee Notes & Financial Billing"
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
            selected = selectedFilter == null,
            onClick = { viewModel.setFilter(null) },
            label = { Text(strings.all) },
            modifier = Modifier.testTag("invoice_filter_all")
          )
        }
        InvoiceStatus.values().forEach { status ->
          item {
            FilterChip(
              selected = selectedFilter == status,
              onClick = { viewModel.setFilter(status) },
              label = { Text(status.name.replace("_", " ")) },
              modifier = Modifier.testTag("invoice_filter_${status.name}")
            )
          }
        }
      }

      if (invoices.isEmpty()) {
        EmptyStateView(
          title = strings.emptyInvoicesTitle,
          description = strings.emptyInvoicesDesc,
          icon = Icons.Default.ReceiptLong
        )
      } else {
        LazyColumn(
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(invoices) { inv ->
            InvoiceItemCard(
              invoice = inv,
              onClick = { onNavigateToInvoiceDetail(inv.id) },
              onPayClick = { onNavigateToPayment(inv.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun InvoiceItemCard(
  invoice: LegalInvoice,
  onClick: () -> Unit,
  onPayClick: () -> Unit
) {
  val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("invoice_card_${invoice.id}"),
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
          text = invoice.invoiceNumber,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
        InvoiceStatusBadge(status = invoice.status)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = invoice.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Due: ${invoice.dueDate}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Text(
          text = "TZS ${currencyFormat.format(invoice.totalTzs)}",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
      }

      if (invoice.status == InvoiceStatus.UNPAID || invoice.status == InvoiceStatus.REJECTED) {
        Spacer(modifier = Modifier.height(12.dp))
        Button(
          onClick = onPayClick,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .testTag("invoice_pay_btn_${invoice.id}")
        ) {
          Icon(Icons.Default.Payment, contentDescription = null)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Settle Fee Note", style = MaterialTheme.typography.labelMedium)
        }
      }
    }
  }
}

@Composable
fun InvoiceDetailScreen(
  invoiceId: String,
  viewModel: InvoicesViewModel,
  onNavigateBack: () -> Unit,
  onNavigateToPayment: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val invoices by viewModel.invoices.collectAsState()
  val invoice = invoices.find { it.id == invoiceId || it.invoiceNumber == invoiceId }
  val currencyFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

  Scaffold(
    topBar = {
      LawTopBar(
        title = invoice?.invoiceNumber ?: strings.invoicesTitle,
        subtitle = "Tax Invoice & Fee Particulars",
        showBack = true,
        onBackClick = onNavigateBack
      )
    }
  ) { padding ->
    if (invoice == null) {
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text("Invoice record not found.")
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
      // Header
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
              text = invoice.invoiceNumber,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            InvoiceStatusBadge(status = invoice.status)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = invoice.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = invoice.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

          Spacer(modifier = Modifier.height(14.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
              Text("Issue Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(invoice.issueDate, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("Payment Due Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(invoice.dueDate, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }

      // Itemized List
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Itemized Professional Fees & Disbursements",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(12.dp))

          invoice.items.forEach { item ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = item.description,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                  text = "Qty: ${item.quantity} @ TZS ${currencyFormat.format(item.unitPriceTzs)}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              Text(
                text = "TZS ${currencyFormat.format(item.totalTzs)}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
              )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
            Text("TZS ${currencyFormat.format(invoice.subtotalTzs)}", style = MaterialTheme.typography.bodyMedium)
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("VAT (18% Statutory)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("TZS ${currencyFormat.format(invoice.vatTzs)}", style = MaterialTheme.typography.bodySmall)
          }

          Spacer(modifier = Modifier.height(12.dp))

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

          Spacer(modifier = Modifier.height(12.dp))

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Amount Due", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(
              "TZS ${currencyFormat.format(invoice.totalTzs)}",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            )
          }
        }
      }

      // Action Button
      if (invoice.status == InvoiceStatus.UNPAID || invoice.status == InvoiceStatus.REJECTED) {
        Button(
          onClick = { onNavigateToPayment(invoice.id) },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("invoice_detail_pay_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Payment, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Proceed to Settle Invoice", style = MaterialTheme.typography.titleMedium)
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
