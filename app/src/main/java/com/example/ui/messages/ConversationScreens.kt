package com.example.ui.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LawTopBar
import com.example.ui.localization.LocalAppStrings
import com.example.ui.theme.LawGoldSecondary
import com.example.ui.theme.LawNavyPrimary

@Composable
fun ConversationListScreen(
  viewModel: MessagesViewModel,
  isDarkMode: Boolean = false,
  onThemeToggle: ((Boolean) -> Unit)? = null,
  onNavigateToConversation: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val conversations by viewModel.conversations.collectAsState()

  Scaffold(
    topBar = {
      LawTopBar(
        title = strings.messagesTitle,
        subtitle = "Counsel & Chambers Communications",
        isDarkMode = isDarkMode,
        onThemeToggle = onThemeToggle
      )
    }
  ) { padding ->
    if (conversations.isEmpty()) {
      EmptyStateView(
        title = strings.emptyMessagesTitle,
        description = strings.emptyMessagesDesc,
        icon = Icons.Default.ChatBubbleOutline,
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
        items(conversations) { conv ->
          ConversationItemCard(
            conversation = conv,
            onClick = {
              viewModel.selectConversation(conv.id)
              onNavigateToConversation(conv.id)
            }
          )
        }
      }
    }
  }
}

@Composable
fun ConversationItemCard(
  conversation: Conversation,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("conversation_card_${conversation.id}"),
    shape = RoundedCornerShape(14.dp),
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
          .size(46.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = conversation.advocateName,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = conversation.lastMessageTimestamp,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = conversation.title,
          style = MaterialTheme.typography.labelSmall.copy(color = LawGoldSecondary, fontWeight = FontWeight.SemiBold),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = conversation.lastMessageText,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      if (conversation.unreadCount > 0) {
        Spacer(modifier = Modifier.width(8.dp))
        Box(
          modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = conversation.unreadCount.toString(),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun ConversationDetailScreen(
  conversationId: String,
  viewModel: MessagesViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val strings = LocalAppStrings.current
  val conversations by viewModel.conversations.collectAsState()
  val messages by viewModel.activeMessages.collectAsState()
  val activeConv = conversations.find { it.id == conversationId }

  LaunchedEffect(conversationId) {
    viewModel.selectConversation(conversationId)
  }

  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Scaffold(
    topBar = {
      LawTopBar(
        title = activeConv?.advocateName ?: strings.messagesTitle,
        subtitle = activeConv?.title ?: "Chambers Discussion",
        showBack = true,
        onBackClick = onNavigateBack
      )
    },
    bottomBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
      ) {
        Row(
          modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .navigationBarsPadding()
            .imePadding(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text(strings.typeMessageHint) },
            maxLines = 4,
            modifier = Modifier
              .weight(1f)
              .testTag("message_input_field"),
            shape = RoundedCornerShape(20.dp)
          )

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = {
              if (inputText.isNotBlank()) {
                viewModel.sendMessage(inputText)
                inputText = ""
              }
            },
            enabled = inputText.isNotBlank(),
            modifier = Modifier.testTag("message_send_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = if (inputText.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  ) { padding ->
    LazyColumn(
      state = listState,
      modifier = modifier
        .fillMaxSize()
        .padding(padding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Topic Header chip
      item {
        Box(
          modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
          contentAlignment = Alignment.Center
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "Topic: ${activeConv?.title ?: "General Legal Consultation"}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      items(messages) { msg ->
        MessageBubble(message = msg)
      }
    }
  }
}

@Composable
fun MessageBubble(message: ChatMessage) {
  val isClient = message.isFromClient

  Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = if (isClient) Alignment.CenterEnd else Alignment.CenterStart
  ) {
    Surface(
      shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (isClient) 16.dp else 4.dp,
        bottomEnd = if (isClient) 4.dp else 16.dp
      ),
      color = if (isClient) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
      modifier = Modifier.widthIn(max = 290.dp),
      shadowElevation = 0.5.dp
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        if (!isClient) {
          Text(
            text = message.senderName,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = LawGoldSecondary
            )
          )
          Spacer(modifier = Modifier.height(2.dp))
        }

        Text(
          text = message.messageText,
          style = MaterialTheme.typography.bodyMedium,
          color = if (isClient) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = message.timestamp,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = if (isClient) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.align(Alignment.End)
        )
      }
    }
  }
}
