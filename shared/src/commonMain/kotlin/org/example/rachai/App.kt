package org.example.rachai

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.example.rachai.ui.HomeScreen
import org.example.rachai.ui.LoginScreen
import org.example.rachai.ui.RegisterScreen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

private enum class Screen { Login, Register, Home }

@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            var currentScreen by remember { mutableStateOf(Screen.Login) }

            when (currentScreen) {
                Screen.Login -> LoginScreen(
                    onLoginSuccess = { currentScreen = Screen.Home },
                    onNavigateToRegister = { currentScreen = Screen.Register }
                )
                Screen.Register -> RegisterScreen(
                    onRegisterSuccess = { currentScreen = Screen.Login },
                    onNavigateToLogin = { currentScreen = Screen.Login }
                )
                Screen.Home -> HomeScreen(
                    onLogout = { currentScreen = Screen.Login }
                )
            }
        }
    }
}
