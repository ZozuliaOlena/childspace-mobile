package com.example.childspace.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.childspace.data.model.ScheduleDto
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

val DarkPurple = Color(0xFF4F169E)
val AccentPurple = Color(0xFF7620D0)
val LightPurpleBg = Color(0xFFEDE4F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadSchedule()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Розклад", color = DarkPurple, fontWeight = FontWeight.Bold)
                },
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
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentPurple
                )
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (viewModel.schedule.isEmpty()) {
                Text(
                    text = "Занять поки немає 😴",
                    color = DarkPurple,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val groupedSchedule = remember(viewModel.schedule) {
                    viewModel.schedule
                        .groupBy { it.startTime.substringBefore("T") }
                        .toSortedMap()
                }

                val dates = groupedSchedule.keys.toList()

                val todayString = remember {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
                }

                val initialTabIndex = remember(dates) {
                    val todayIndex = dates.indexOf(todayString)
                    if (todayIndex != -1) {
                        todayIndex
                    } else {
                        val futureIndex = dates.indexOfFirst { it > todayString }
                        if (futureIndex != -1) futureIndex else dates.size - 1.coerceAtLeast(0)
                    }
                }

                var selectedTabIndex by remember(dates) { mutableStateOf(initialTabIndex) }

                Column(modifier = Modifier.fillMaxSize()) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = LightPurpleBg,
                        contentColor = AccentPurple,
                        edgePadding = 8.dp,
                        divider = {},
                        indicator = { tabPositions ->
                            if (selectedTabIndex < tabPositions.size) {
                                TabRowDefaults.Indicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = AccentPurple,
                                    height = 3.dp
                                )
                            }
                        }
                    ) {
                        dates.forEachIndexed { index, dateString ->
                            val isToday = dateString == todayString

                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                modifier = Modifier.drawBehind {
                                    if (isToday) {
                                        drawLine(
                                            color = AccentPurple.copy(alpha = 0.3f),
                                            start = Offset(0f, size.height),
                                            end = Offset(size.width, size.height),
                                            strokeWidth = 6f
                                        )
                                    }
                                },
                                text = {
                                    Text(
                                        text = getFormattedDateText(dateString),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = if (selectedTabIndex == index) AccentPurple else DarkPurple.copy(alpha = 0.6f)
                                    )
                                }
                            )
                        }
                    }

                    val selectedDate = dates.getOrNull(selectedTabIndex)
                    val lessonsForSelectedDate = groupedSchedule[selectedDate] ?: emptyList()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                    ) {
                        items(lessonsForSelectedDate) { lesson ->
                            ScheduleCard(lesson)
                        }
                    }
                }
            }
        }
    }
}

fun getFormattedDateText(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val todayCalendar = Calendar.getInstance()
    val todayString = inputFormat.format(todayCalendar.time)

    val tomorrowCalendar = Calendar.getInstance()
    tomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrowString = inputFormat.format(tomorrowCalendar.time)

    return when (dateString) {
        todayString -> "Сьогодні"
        tomorrowString -> "Завтра"
        else -> {
            try {
                val parsedDate = inputFormat.parse(dateString)
                val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                if (parsedDate != null) outputFormat.format(parsedDate) else dateString
            } catch (e: Exception) {
                dateString
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