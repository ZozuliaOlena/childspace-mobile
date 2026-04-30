package com.example.childspace.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val DarkPurple = Color(0xFF4F169E)
val AccentPurple = Color(0xFF7620D0)
val LightPurpleBg = Color(0xFFEDE4F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onLogoutClick: () -> Unit) {

    val state by viewModel.state.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мій профіль", color = DarkPurple, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightPurpleBg)
            )
        },
        containerColor = LightPurpleBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentPurple
                )
            }
            else if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                ) {

                    state.user?.let { user ->

                        item {
                            UserCard(
                                firstName = user.firstName,
                                lastName = user.lastName,
                                email = user.email,
                                role = user.role
                            )
                        }

                        if (user.children.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Прив'язані діти",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPurple,
                                    modifier = Modifier.padding(top = 8.dp, start = 8.dp)
                                )
                            }
                        }

                        items(user.children) { child ->
                            ChildCard(
                                childName = child.name,
                                age = "${child.age} років",
                                groupNames = child.groupNames
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { showLogoutDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Вийти з акаунта",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false }, // Закрити, якщо клікнути повз вікно
                    title = {
                        Text(text = "Підтвердження", fontWeight = FontWeight.Bold, color = DarkPurple)
                    },
                    text = {
                        Text(text = "Ви впевнені, що хочете вийти з акаунта?", color = DarkPurple)
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showLogoutDialog = false
                                onLogoutClick()
                            }
                        ) {
                            Text("Так, вийти", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showLogoutDialog = false }
                        ) {
                            Text("Скасувати", color = AccentPurple)
                        }
                    },
                    containerColor = Color.White,
                    titleContentColor = DarkPurple,
                    textContentColor = DarkPurple
                )
            }
        }
    }
}

@Composable
fun UserCard(firstName: String, lastName: String, email: String, role: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AccentPurple.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Аватар",
                    tint = AccentPurple,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "$firstName $lastName",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkPurple,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkPurple.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = AccentPurple,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = role,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChildCard(childName: String, age: String, groupNames: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(AccentPurple.copy(alpha = 0.6f))
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = childName,
                    style = MaterialTheme.typography.titleMedium,
                    color = DarkPurple,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🧒 Вік: $age",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkPurple.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (groupNames.isEmpty()) {
                    Text(
                        text = "👥 Без групи",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentPurple
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        groupNames.forEach { groupName ->
                            Surface(
                                color = AccentPurple.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = groupName,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AccentPurple
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}