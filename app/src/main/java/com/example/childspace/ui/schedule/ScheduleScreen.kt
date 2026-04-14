package com.example.childspace.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.childspace.data.model.ScheduleDto

val DarkPurple = Color(0xFF4F169E)
val AccentPurple = Color(0xFF7620D0)
val LightPurpleBg = Color(0xFFEDE4F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel, onLogoutClick: () -> Unit) {
    LaunchedEffect(Unit) {
        viewModel.loadSchedule()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Розклад", color = DarkPurple, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightPurpleBg),
                actions = {
                    TextButton(onClick = onLogoutClick) {
                        Text("Вийти", color = AccentPurple, fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        containerColor = LightPurpleBg
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentPurple
                )
            }
            else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
            else if (viewModel.schedule.isEmpty()) {
                Text(
                    text = "Занять поки немає 😴",
                    color = DarkPurple,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                ) {
                    items(viewModel.schedule) { lesson ->
                        ScheduleCard(lesson)
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleCard(lesson: ScheduleDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
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
                    .width(12.dp)
                    .fillMaxHeight()
                    .background(AccentPurple)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                val title = lesson.subjectName ?: lesson.groupName ?: "Заняття"

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkPurple,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                val formattedStart = lesson.startTime.substringAfter("T").take(5)
                val formattedEnd = lesson.endTime.substringAfter("T").take(5)

                Text(
                    text = "🕒 $formattedStart - $formattedEnd",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkPurple.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "🚪 Кабінет: ${lesson.roomName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkPurple.copy(alpha = 0.8f)
                )

                if (lesson.teacherName != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "👨‍🏫 Викладач: ${lesson.teacherName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentPurple
                    )
                }

                if (lesson.groupName != null && lesson.subjectName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "👥 Група: ${lesson.groupName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentPurple
                    )
                }
            }
        }
    }
}