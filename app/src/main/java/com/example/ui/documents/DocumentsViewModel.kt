package com.example.ui.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.DocumentCategory
import com.example.data.model.LegalDocument
import com.example.data.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DocumentsViewModel(
  private val documentRepository: DocumentRepository = LawChambersCoordinator.documentRepository
) : ViewModel() {

  val documents: StateFlow<List<LegalDocument>> = documentRepository.documents

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategoryFilter = MutableStateFlow<DocumentCategory?>(null)
  val selectedCategoryFilter: StateFlow<DocumentCategory?> = _selectedCategoryFilter.asStateFlow()

  val filteredDocuments = combine(
    documents,
    _searchQuery,
    _selectedCategoryFilter
  ) { list, query, category ->
    list.filter { doc ->
      val matchesQuery = query.isBlank() || doc.name.contains(query, ignoreCase = true)
      val matchesCat = category == null || doc.category == category
      matchesQuery && matchesCat
    }
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setCategoryFilter(category: DocumentCategory?) {
    _selectedCategoryFilter.value = category
  }

  fun uploadSimulatedDocument(name: String, category: DocumentCategory, sizeBytes: Long) {
    viewModelScope.launch {
      documentRepository.uploadDocument(
        name = name,
        category = category,
        sizeBytes = sizeBytes,
        relatedMatterId = null,
        relatedRequestId = null
      )
    }
  }
}
