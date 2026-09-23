package com.example.ui.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentCategory
import com.example.data.model.LegalDocument
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LawTopBar
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.LawGoldSecondary
import com.example.ui.theme.LawNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentListScreen(
  viewModel: DocumentsViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val documents by viewModel.filteredDocuments.collectAsState(initial = emptyList())
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCat by viewModel.selectedCategoryFilter.collectAsState()

  var showUploadDialog by remember { mutableStateOf(false) }
  var previewDocument by remember { mutableStateOf<LegalDocument?>(null) }

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.documentsTitle,
        subtitle = "Secure Legal File Repository",
        showBack = true,
        onBackClick = onNavigateBack
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { showUploadDialog = true },
        icon = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
        text = { Text("Upload File") },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("documents_fab_upload")
      )
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(padding)
    ) {
      // Search Box
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = { Text("Search deeds, pleadings, contracts...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
          if (searchQuery.isNotBlank()) {
            IconButton(onClick = { viewModel.setSearchQuery("") }) {
              Icon(Icons.Default.Clear, contentDescription = null)
            }
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .testTag("document_search_input"),
        shape = RoundedCornerShape(12.dp)
      )

      // Category Chips
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          FilterChip(
            selected = selectedCat == null,
            onClick = { viewModel.setCategoryFilter(null) },
            label = { Text("ALL") },
            modifier = Modifier.testTag("doc_filter_all")
          )
        }
        items(DocumentCategory.values()) { cat ->
          FilterChip(
            selected = selectedCat == cat,
            onClick = { viewModel.setCategoryFilter(cat) },
            label = { Text(cat.label) },
            modifier = Modifier.testTag("doc_filter_${cat.name.lowercase()}")
          )
        }
      }

      if (documents.isEmpty()) {
        EmptyStateView(
          title = "No Documents Found",
          description = "No legal records match the selected search criteria or filter.",
          icon = Icons.Default.FolderOpen,
          actionLabel = "Upload Document",
          onActionClick = { showUploadDialog = true }
        )
      } else {
        LazyColumn(
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(documents) { doc ->
            DocumentItemRow(
              document = doc,
              onClick = { previewDocument = doc }
            )
          }
        }
      }
    }
  }

  // Upload dialog
  if (showUploadDialog) {
    SimulatedDocumentUploadDialog(
      onDismiss = { showUploadDialog = false },
      onUpload = { name, cat, size ->
        viewModel.uploadSimulatedDocument(name, cat, size)
        showUploadDialog = false
      }
    )
  }

  // Preview dialog
  val currentPreview = previewDocument
  if (currentPreview != null) {
    DocumentPreviewDialog(
      document = currentPreview,
      onDismiss = { previewDocument = null }
    )
  }
}

@Composable
fun DocumentItemRow(
  document: LegalDocument,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("doc_item_${document.id}"),
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    shadowElevation = 0.5.dp
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Description,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = document.name,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${document.sizeBytes / 1024} KB • Category: ${document.category.label}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Uploaded: ${document.uploadedDate} ${if (document.isVerifiedByFirm) "• Verified by Firm" else ""}",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = if (document.isVerifiedByFirm) LawGoldSecondary else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      IconButton(onClick = onClick) {
        Icon(Icons.Default.Visibility, contentDescription = "View", tint = LawGoldSecondary)
      }
    }
  }
}

@Composable
fun SimulatedDocumentUploadDialog(
  onDismiss: () -> Unit,
  onUpload: (String, DocumentCategory, Long) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf(DocumentCategory.EVIDENCE) }
  val sampleFiles = listOf(
    Pair("Lease_Addendum_Final.pdf", 2_400_000L),
    Pair("Court_Summons_Copy.pdf", 1_100_000L),
    Pair("Site_Inspection_Photo.jpg", 3_200_000L),
    Pair("TRA_Tax_Clearance.pdf", 890_000L)
  )
  var selectedFileIndex by remember { mutableStateOf(0) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Upload Legal Document", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Document Description / Title *") },
          placeholder = { Text("e.g. Registered Title Deed") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("upload_doc_title_input"),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Select Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(4.dp))
        DocumentCategory.values().take(3).forEach { cat ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable { category = cat }
          ) {
            RadioButton(selected = category == cat, onClick = { category = cat })
            Spacer(modifier = Modifier.width(6.dp))
            Text(cat.label, style = MaterialTheme.typography.bodySmall)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Select File to Attach:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(6.dp))

        sampleFiles.forEachIndexed { index, file ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
              .clickable { selectedFileIndex = index },
            shape = RoundedCornerShape(8.dp),
            color = if (selectedFileIndex == index) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
          ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
              RadioButton(selected = selectedFileIndex == index, onClick = { selectedFileIndex = index })
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(file.first, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                Text("${file.second / 1024} KB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val selected = sampleFiles[selectedFileIndex]
          onUpload(title.ifBlank { selected.first }, category, selected.second)
        },
        modifier = Modifier.testTag("upload_doc_confirm_btn")
      ) {
        Text("Upload to Vault")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun DocumentPreviewDialog(
  document: LegalDocument,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = LawGoldSecondary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(document.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("File: ${document.name}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
            Text("Category: ${document.category.label}", style = MaterialTheme.typography.bodySmall)
            Text("Size: ${document.sizeBytes / 1024} KB", style = MaterialTheme.typography.bodySmall)
            Text("Uploaded On: ${document.uploadedDate}", style = MaterialTheme.typography.bodySmall)
            if (!document.relatedMatterId.isNullOrBlank()) {
              Text("Matter Link: ${document.relatedMatterId}", style = MaterialTheme.typography.bodySmall, color = LawNavyPrimary)
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Chambers Cryptographic Seal: Verified. Stored securely under ET CETRA ADVOCATES legal document retention protocols.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    },
    confirmButton = {
      Button(onClick = onDismiss) {
        Text("Close")
      }
    }
  )
}
