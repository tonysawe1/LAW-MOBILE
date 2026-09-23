package com.example.ui.matters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.LegalMatter
import com.example.data.model.MatterStatus
import com.example.data.repository.MatterRepository
import com.example.data.repository.MessageRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MattersViewModel(
  private val matterRepository: MatterRepository = LawChambersCoordinator.matterRepository,
  private val messageRepository: MessageRepository = LawChambersCoordinator.messageRepository
) : ViewModel() {

  val matters: StateFlow<List<LegalMatter>> = matterRepository.matters

  private val _selectedStatusFilter = MutableStateFlow<MatterStatus?>(null)
  val selectedStatusFilter: StateFlow<MatterStatus?> = _selectedStatusFilter.asStateFlow()

  val filteredMatters: StateFlow<List<LegalMatter>> = combine(
    matters,
    _selectedStatusFilter
  ) { list, filter ->
    if (filter == null) list else list.filter { it.status == filter }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setFilter(status: MatterStatus?) {
    _selectedStatusFilter.value = status
  }

  fun getMatterById(id: String): Flow<LegalMatter?> = flow {
    emit(matterRepository.getMatterById(id))
  }

  fun contactCounsel(matter: LegalMatter, onReady: (String) -> Unit) {
    viewModelScope.launch {
      val convId = messageRepository.getOrCreateConversationForMatter(
        matterId = matter.id,
        title = "${matter.matterReference}: ${matter.title}"
      )
      onReady(convId)
    }
  }
}
