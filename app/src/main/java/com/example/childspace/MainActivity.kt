package com.example.childspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.childspace.data.local.TokenManager
import com.example.childspace.data.repository.AuthRepository
import com.example.childspace.data.repository.ChatRepository
import com.example.childspace.data.repository.ScheduleRepository
import com.example.childspace.network.AuthApiService
import com.example.childspace.network.RetrofitClient
import com.example.childspace.network.ScheduleApiService
import com.example.childspace.ui.auth.AuthViewModel
import com.example.childspace.ui.auth.LoginScreen
import com.example.childspace.ui.main.MainScreen
import com.example.childspace.ui.schedule.ScheduleViewModel
import com.example.childspace.ui.theme.ChildspaceTheme

import com.example.childspace.network.ProfileApiService
import com.example.childspace.data.repository.ProfileRepository
import com.example.childspace.network.ChatApiService
import com.example.childspace.network.SignalRManager
import com.example.childspace.ui.chat.ChatDetailsViewModel
import com.example.childspace.ui.chat.ChatsViewModel
import com.example.childspace.ui.profile.ProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(applicationContext)

        val retrofit = RetrofitClient.create(tokenManager)

        val authApi = retrofit.create(AuthApiService::class.java)
        val scheduleApi = retrofit.create(ScheduleApiService::class.java)

        val profileApi = retrofit.create(ProfileApiService::class.java)

        val chatApi = retrofit.create(ChatApiService::class.java)

        val authRepository = AuthRepository(authApi, tokenManager)
        val authViewModel = AuthViewModel(authRepository)

        val scheduleRepository = ScheduleRepository(scheduleApi)
        val scheduleViewModel = ScheduleViewModel(scheduleRepository)

        val profileRepository = ProfileRepository(profileApi)
        val profileViewModel = ProfileViewModel(profileRepository)

        val signalRManager = SignalRManager(tokenManager)
        val chatRepository = ChatRepository(chatApi, signalRManager)
        val chatsViewModel = ChatsViewModel(chatRepository)
        val chatDetailsViewModel = ChatDetailsViewModel(chatRepository)

        setContent {
            ChildspaceTheme {
                var isLoggedIn by remember { mutableStateOf(tokenManager.getToken() != null) }

                if (isLoggedIn) {
                    MainScreen(
                        scheduleViewModel = scheduleViewModel,
                        profileViewModel = profileViewModel,
                        chatsViewModel = chatsViewModel,
                        chatDetailsViewModel = chatDetailsViewModel,
                        currentUserId = tokenManager.getUserId(),
                        onLogoutClick = {
                            authRepository.logout()
                            isLoggedIn = false
                        }
                    )
                } else {
                    LoginScreen(
                        viewModel = authViewModel,
                        onNavigateToMain = {
                            isLoggedIn = true
                        }
                    )
                }
            }
        }
    }
}