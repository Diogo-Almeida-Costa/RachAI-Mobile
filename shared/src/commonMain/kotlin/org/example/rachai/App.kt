package org.example.rachai

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import org.example.rachai.navigation.HomeRoute
import org.example.rachai.navigation.LoginRoute
import org.example.rachai.navigation.RegisterRoute
import org.example.rachai.ui.HomeScreen
import org.example.rachai.ui.LoginScreen
import org.example.rachai.ui.RegisterScreen

/**
 * Base do URI de deep link do app.
 * No Android, o esquema "rachai" precisa estar declarado no AndroidManifest.xml
 * dentro do intent-filter da MainActivity para o sistema saber abrir o app.
 * Exemplo de teste via adb:
 * adb shell am start -a android.intent.action.VIEW -d "rachai://app/home?userEmail=teste@rachai.com"
 */
private const val DEEP_LINK_BASE = "rachai://app"

@Composable
@Preview
fun App(navController: NavHostController = rememberNavController()) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = LoginRoute
            ) {
                composable<LoginRoute> {
                    LoginScreen(
                        onLoginSuccess = { email ->
                            navController.navigate(HomeRoute(userEmail = email)) {
                                // Remove a tela de login da pilha: "voltar" no Home não deve
                                // reabrir o login logo depois de autenticar.
                                popUpTo(LoginRoute) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate(RegisterRoute())
                        }
                    )
                }

                composable<RegisterRoute> { backStackEntry ->
                    val route: RegisterRoute = backStackEntry.toRoute()
                    RegisterScreen(
                        prefillEmail = route.prefillEmail,
                        onRegisterSuccess = {
                            navController.navigate(LoginRoute) {
                                popUpTo<RegisterRoute> { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<HomeRoute>(
                    deepLinks = listOf(
                        navDeepLink<HomeRoute>(basePath = "$DEEP_LINK_BASE/home")
                    )
                ) { backStackEntry ->
                    val route: HomeRoute = backStackEntry.toRoute()
                    HomeScreen(
                        userEmail = route.userEmail,
                        onLogout = {
                            navController.navigate(LoginRoute) {
                                // Limpa toda a pilha: depois de sair, "voltar" não deve
                                // levar de volta para uma tela autenticada.
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
