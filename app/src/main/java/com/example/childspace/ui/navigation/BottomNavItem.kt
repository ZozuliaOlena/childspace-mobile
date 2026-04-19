package com.example.childspace.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person // ДОБАВЛЕНО: иконка профиля
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Schedule : BottomNavItem("schedule", "Розклад", Icons.Filled.DateRange)
    object Chats : BottomNavItem("chats", "Чати", Icons.Filled.MailOutline)
    object Profile : BottomNavItem("profile", "Профіль", Icons.Filled.Person)
}

val DarkPurple = Color(0xFF4F169E)
val AccentPurple = Color(0xFF7620D0)
val LightPurpleBg = Color(0xFFEDE4F5)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Schedule,
        BottomNavItem.Chats,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = AccentPurple
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentPurple,
                    selectedTextColor = AccentPurple,
                    indicatorColor = LightPurpleBg,
                    unselectedIconColor = DarkPurple.copy(alpha = 0.5f),
                    unselectedTextColor = DarkPurple.copy(alpha = 0.5f)
                )
            )
        }
    }
}