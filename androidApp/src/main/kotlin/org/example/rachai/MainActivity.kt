package org.example.rachai

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    // Guardamos a referência para poder repassar deep links recebidos
    // enquanto a Activity já está em memória (onNewIntent).
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val controller = rememberNavController()
            navController = controller
            App(navController = controller)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // App já estava aberto: encaminha o novo deep link para o NavController atual.
        navController?.handleDeepLink(intent)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
