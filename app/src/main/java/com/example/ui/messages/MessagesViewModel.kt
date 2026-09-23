package com.example.ui.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.mock.LawChambersCoordinator
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.repository.MessageRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MessagesViewModel(
  private val messageRepository: MessageRepository = LawChambersCoordinator.messageRepository
) : ViewModel() {

  val conversations: StateFlow<List<Conversation>> = messageRepository.conversations

  private val _activeConversationId = MutableStateFlow<String?>(null)
  val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()

  val activeMessages: StateFlow<List<ChatMessage>> = _activeConversationId.flatMapLatest { id ->
    if (id == null) flowOf(emptyList()) else messageRepository.getMessagesForConversation(id)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun selectConversation(id: String) {
    _activeConversationId.value = id
  }

  fun sendMessage(content: String) {
    val convId = _activeConversationId.value ?: return
    if (content.isBlank()) return
    viewModelScope.launch {
      messageRepository.sendMessage(convId, content.trim())
    }
  }
}
