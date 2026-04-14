package com.example.childspace.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

// Твоя фирменная палитра
val DarkPurple = Color(0xFF4F169E)
val AccentPurple = Color(0xFF7620D0)
val LightPurpleBg = Color(0xFFEDE4F5)

@Composable
fun LoginScreen(viewModel: AuthViewModel, onNavigateToMain: () -> Unit) {
    // Surface задает цвет фона на весь экран
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightPurpleBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ChildSpace",
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                color = DarkPurple
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "З поверненням!",
                style = MaterialTheme.typography.bodyLarge,
                color = AccentPurple
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Настройка цветов для полей ввода (белый фон, фиолетовые рамки)
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = AccentPurple,
                unfocusedBorderColor = DarkPurple.copy(alpha = 0.3f),
                focusedLabelColor = AccentPurple,
                unfocusedLabelColor = DarkPurple.copy(alpha = 0.7f),
                focusedLeadingIconColor = AccentPurple,
                unfocusedLeadingIconColor = DarkPurple.copy(alpha = 0.5f),
                cursorColor = AccentPurple
            )

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Електронна пошта") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp), // Сильное скругление как на макете
                colors = textFieldColors,
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = textFieldColors,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
            )

            viewModel.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.onLoginClick(onNavigateToMain) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentPurple,
                    contentColor = Color.White
                ),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Увійти",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}