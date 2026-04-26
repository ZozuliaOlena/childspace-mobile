package com.example.childspace.ui.main

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import android.Manifest
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log

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
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isPermissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!isPermissionGranted) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_DEBUG", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM_DEBUG", "Мій FCM Токен: $token")

             profileViewModel.updateFcmToken(token)
        }
    }

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