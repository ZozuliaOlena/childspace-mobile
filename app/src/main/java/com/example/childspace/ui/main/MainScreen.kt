package com.example.childspace.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.childspace.ui.chat.ChatDetailsScreen
import com.example.childspace.ui.chat.ChatDetailsViewModel
import com.example.childspace.ui.chat.ChatsScreen
import com.example.childspace.ui.chat.ChatsViewModel
import com.example.childspace.ui.navigation.BottomNavigationBar
import com.example.childspace.ui.navigation.BottomNavItem
import com.example.childspace.ui.schedule.ScheduleScreen
import com.example.childspace.ui.schedule.ScheduleViewModel
import com.example.childspace.ui.profile.ProfileScreen
import com.example.childspace.ui.profile.ProfileViewModel

@Composable
fun MainScreen(
    scheduleViewModel: ScheduleViewModel,
    chatsViewModel: ChatsViewModel,
    profileViewModel: ProfileViewModel,
    chatDetailsViewModel: ChatDetailsViewModel,
    currentUserId: String,
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
                    viewModel = scheduleViewModel
                )
            }

            composable(BottomNavItem.Chats.route) {
                ChatsScreen(
                    viewModel = chatsViewModel,
                    onChatClick = { chat ->
                        navController.navigate("chat_detail/${chat.id}?name=${chat.name}&count=${chat.participantsCount}")
                    }
                )
            }

            composable(
                route = "chat_detail/{chatId}?name={chatName}&count={count}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("chatName") { type = NavType.StringType; defaultValue = "Чат" },
                    navArgument("count") { type = NavType.IntType; defaultValue = 0 }
                )
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                val chatName = backStackEntry.arguments?.getString("chatName") ?: "Чат"
                val count = backStackEntry.arguments?.getInt("count") ?: 0

                ChatDetailsScreen(
                    viewModel = chatDetailsViewModel,
                    chatId = chatId,
                    chatName = chatName,
                    participantsCount = count,
                    currentUserId = currentUserId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }
}