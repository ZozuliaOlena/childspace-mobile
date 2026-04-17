package com.example.childspace.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.childspace.ui.navigation.BottomNavigationBar
import com.example.childspace.ui.navigation.BottomNavItem
import com.example.childspace.ui.schedule.ScheduleScreen
import com.example.childspace.ui.schedule.ScheduleViewModel

// import com.example.childspace.ui.chats.ChatsScreen -- екран чатів

@Composable
fun MainScreen(
    scheduleViewModel: ScheduleViewModel,
    onLogoutClick: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Schedule.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(BottomNavItem.Schedule.route) {
                ScheduleScreen(
                    viewModel = scheduleViewModel,
                    onLogoutClick = onLogoutClick
                )
            }

            // Додати екран чатів
            composable(BottomNavItem.Chats.route) {

            }
        }
    }
}