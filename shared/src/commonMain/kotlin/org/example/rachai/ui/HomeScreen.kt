package org.example.rachai.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.rachai.auth.SessionManager

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login realizado com sucesso!", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Token JWT recebido:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            SessionManager.token?.take(24)?.plus("...") ?: "",
            style = MaterialTheme.typography.bodySmall
        )
        Button(
            onClick = {
                SessionManager.clear()
                onLogout()
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Sair")
        }
    }
}
