package com.example.childspace.ui.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.childspace.data.model.ScheduleDto
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight

val DarkPurple = Color(0xFF4F169E)
val AccentPurple = Color(0xFF7620D0)
val LightPurpleBg = Color(0xFFEDE4F5)
val SelectedDayBg = Color(0xFF7620D0)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadSchedule()
    }

    val initialPage = 5000
    val pagerState = rememberPagerState(initialPage = initialPage) { 10000 }
    val coroutineScope = rememberCoroutineScope()

    val todayString = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    val weekDays = remember(pagerState.currentPage) { getWeekDaysForPage(pagerState.currentPage, initialPage) }
    val currentMonthYear = remember(pagerState.currentPage) { getMonthYearForPage(pagerState.currentPage, initialPage) }

    val groupedSchedule = remember(viewModel.schedule) {
        viewModel.schedule.groupBy { it.startTime.substringBefore("T") }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Розклад", color = DarkPurple, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadSchedule() }) {
                        Icon(Icons.Default.Refresh, "Оновити", tint = DarkPurple)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightPurpleBg)
            )
        },
        containerColor = LightPurpleBg
    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = viewModel.isLoading,
            onRefresh = { viewModel.loadSchedule() },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 0.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 7)
                                }
                            }) {
                                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Минулий тиждень", tint = DarkPurple)
                            }

                            Text(
                                text = currentMonthYear,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkPurple
                            )

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 7)
                                }
                            }) {
                                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Наступний тиждень", tint = DarkPurple)
                            }
                        }

                        if (pagerState.currentPage != initialPage) {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(initialPage) }
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text("Сьогодні", color = AccentPurple, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(80.dp))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weekDays.forEach { day ->
                            val isSelected = pagerState.currentPage == day.pageIndex
                            val isToday = day.dateString == todayString

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) SelectedDayBg else Color.Transparent)
                                    .clickable {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(day.pageIndex)
                                        }
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = day.dayOfWeekName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White else DarkPurple.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = day.dayOfMonth,
                                    fontSize = 16.sp,
                                    fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Bold,
                                    color = if (isSelected) Color.White else DarkPurple
                                )

                                val hasLessons = groupedSchedule.containsKey(day.dateString)
                                if (hasLessons && !isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .size(6.dp)
                                            .background(AccentPurple, CircleShape)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, page - initialPage) }
                        val dateStringForPage = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                        val lessonsForDay = groupedSchedule[dateStringForPage] ?: emptyList()

                        if (lessonsForDay.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Занять немає, відпочиваємо! ☕",
                                    color = DarkPurple.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
                            ) {
                                items(lessonsForDay) { lesson ->
                                    ScheduleCard(lesson)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class WeekDayInfo(
    val dayOfWeekName: String,
    val dayOfMonth: String,
    val dateString: String,
    val pageIndex: Int
)

fun getWeekDaysForPage(currentPage: Int, initialPage: Int): List<WeekDayInfo> {
    val calendar = Calendar.getInstance(Locale("uk", "UA"))
    calendar.add(Calendar.DAY_OF_YEAR, currentPage - initialPage)

    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val mondayOffset = if (dayOfWeek == Calendar.SUNDAY) 6 else (dayOfWeek - Calendar.MONDAY)
    calendar.add(Calendar.DAY_OF_YEAR, -mondayOffset)

    var currentLoopPageIndex = currentPage - mondayOffset
    val days = mutableListOf<WeekDayInfo>()
    val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
    val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val nameFormat = SimpleDateFormat("EE", Locale("uk"))

    for (i in 0 until 7) {
        val name = nameFormat.format(calendar.time).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
        days.add(
            WeekDayInfo(
                dayOfWeekName = name,
                dayOfMonth = dayFormat.format(calendar.time),
                dateString = apiFormat.format(calendar.time),
                pageIndex = currentLoopPageIndex
            )
        )
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        currentLoopPageIndex++
    }
    return days
}

fun getMonthYearForPage(currentPage: Int, initialPage: Int): String {
    val calendar = Calendar.getInstance(Locale("uk", "UA"))
    calendar.add(Calendar.DAY_OF_YEAR, currentPage - initialPage)
    val format = SimpleDateFormat("LLLL yyyy", Locale("uk", "UA"))
    return format.format(calendar.time).replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
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