package com.example.childspace.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.childspace.data.model.ChatMessageResponseDto
import com.example.childspace.ui.theme.AccentPurple
import com.example.childspace.ui.theme.DarkPurple
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailsScreen (
    viewModel: ChatDetailsViewModel,
    chatId: String,
    chatName: String,
    participantsCount: Int,
    currentUserId: String,
    onBackClick: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val editingMessage by viewModel.editingMessage.collectAsState()
    var messageText by remember { mutableStateOf("") }

    var showParticipantsDialog by remember { mutableStateOf(false) }
    val participantsList by viewModel.participants.collectAsState()

    LaunchedEffect(chatId) {
        viewModel.openChat(chatId)
    }

    LaunchedEffect(editingMessage) {
        if (editingMessage != null) {
            messageText = editingMessage!!.content
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(chatName, color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = { showParticipantsDialog = true }) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$participantsCount учасників", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkPurple)
            )
        },
        containerColor = Color.White
    ){ paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ){
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                reverseLayout = true
            ) {
                itemsIndexed(messages) { index, message ->
                    val isMine = message.senderId.equals(currentUserId, ignoreCase = true)

                    val showDate = if (index == messages.lastIndex) {
                        true
                    } else {
                        val currentDate = formatDateForHeader(message.createdAt)
                        val previousDate = formatDateForHeader(messages[index + 1].createdAt)
                        currentDate != previousDate
                    }

                    MessageBubble(
                        message = message,
                        isMine = isMine,
                        onDelete = { viewModel.deleteMessage(message.id) },
                        onEdit = { viewModel.setEditingMessage(message) }
                    )

                    if (showDate) {
                        DateHeader(dateText = formatDateForHeader(message.createdAt))
                    }
                }
            }
            MessageInputArea(
                text = messageText,
                onTextChange = { messageText = it },
                onSend = {
                    viewModel.sendMessage(messageText)
                    messageText = ""
                },
                isEditing = editingMessage != null,
                onCancelEdit = {
                    viewModel.setEditingMessage(null)
                    messageText = ""
                }
            )
        }
    }
    if (showParticipantsDialog) {
        LaunchedEffect(Unit) {
            viewModel.loadParticipants()
        }
        AlertDialog(
            onDismissRequest = { showParticipantsDialog = false },
            title = {
                Text("Учасники чату", color = DarkPurple, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            },
            text = {
                if (participantsList.isEmpty()) {
                    Text("Завантаження...", color = Color.Gray)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(participantsList) { user ->
                            val isMe = user.id.equals(currentUserId, ignoreCase = true)
                            val fullName = "${user.firstName} ${user.lastName}"

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, AccentPurple.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEDE4F5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.firstName.take(1).uppercase(),
                                        color = DarkPurple,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = if (isMe) "$fullName (Ви)" else fullName,
                                    color = DarkPurple,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showParticipantsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEDE4F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Закрити", color = DarkPurple, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: ChatMessageResponseDto,
    isMine: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 80.dp, max = 280.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { if (isMine) showMenu = true }
                )
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, AccentPurple),
                shadowElevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (!isMine) {
                        Text(
                            text = message.senderName,
                            fontWeight = FontWeight.Bold,
                            color = DarkPurple,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = message.content,
                        color = Color.Black,
                        fontSize = 15.sp
                    )
                    Text(
                        text = formatTime(message.createdAt),
                        color = Color.Gray,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(if (isMine) Alignment.CenterHorizontally else Alignment.End)
                            .padding(top = 4.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Редагувати") },
                    onClick = {
                        showMenu = false
                        onEdit()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Видалити", color = Color.Red) },
                    onClick = {
                        showMenu = false
                        onDelete()
                    }
                )
            }
        }
    }
}

@Composable
fun DateHeader(dateText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dateText,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 14.sp
        )
    }
}

@Composable
fun MessageInputArea(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isEditing: Boolean,
    onCancelEdit: () -> Unit
) {
    Column(modifier = Modifier.background(Color.White)) {
        if (isEditing) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF0E6FA))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Редагування повідомлення...", color = AccentPurple, fontSize = 12.sp)
                TextButton(onClick = onCancelEdit) {
                    Text("Скасувати", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Напишіть повідомлення...", color = Color.Gray) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    unfocusedBorderColor = AccentPurple.copy(alpha = 0.5f),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onSend,
                enabled = text.isNotBlank(),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF4EBFF),
                    contentColor = DarkPurple,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text(if (isEditing) "Зберегти" else "Надіслати")
            }
        }
    }
}

fun formatTime(isoString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = parser.parse(isoString)
        if (date != null){
            formatter.format(date)
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}

fun formatDateForHeader(isoString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMMM yyyy 'р.'", Locale("uk", "UA"))
        val date = parser.parse(isoString)
        if (date != null){
            formatter.format(date)
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}