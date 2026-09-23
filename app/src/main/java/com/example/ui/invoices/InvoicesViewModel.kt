package com.example.ui.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.InvoiceStatus
import com.example.data.model.LegalInvoice
import com.example.data.repository.InvoiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class InvoicesViewModel(
  private val invoiceRepository: InvoiceRepository = LawChambersCoordinator.invoiceRepository
) : ViewModel() {

  val invoices: StateFlow<List<LegalInvoice>> = invoiceRepository.invoices

  private val _selectedStatusFilter = MutableStateFlow<InvoiceStatus?>(null)
  val selectedStatusFilter: StateFlow<InvoiceStatus?> = _selectedStatusFilter.asStateFlow()

  val filteredInvoices = combine(
    invoices,
    _selectedStatusFilter
  ) { list, filter ->
    if (filter == null) list else list.filter { it.status == filter }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setFilter(status: InvoiceStatus?) {
    _selectedStatusFilter.value = status
  }
}
