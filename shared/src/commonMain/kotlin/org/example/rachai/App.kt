package org.example.rachai

import androidx.compose.foundation.layout.fillMaxSize
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
import org.example.rachai.navigation.AddExpenseRoute
import org.example.rachai.navigation.AddMemberRoute
import org.example.rachai.navigation.CreateGroupRoute
import org.example.rachai.navigation.GroupDetailRoute
import org.example.rachai.navigation.GroupsRoute
import org.example.rachai.navigation.LoginRoute
import org.example.rachai.navigation.ProfileRoute
import org.example.rachai.navigation.RegisterRoute
import org.example.rachai.theme.RachaiTheme
import org.example.rachai.ui.LoginScreen
import org.example.rachai.ui.RegisterScreen
import org.example.rachai.ui.expense.AddExpenseScreen
import org.example.rachai.ui.groupdetail.AddMemberScreen
import org.example.rachai.ui.groupdetail.GroupDetailScreen
import org.example.rachai.ui.groups.CreateGroupScreen
import org.example.rachai.ui.groups.GroupsScreen
import org.example.rachai.ui.profile.ProfileScreen

/**
 * Deep link base. O esquema "rachai" precisa estar no AndroidManifest.
 * Teste: adb shell am start -a android.intent.action.VIEW -d "rachai://app/home"
 */
private const val DEEP_LINK_BASE = "rachai://app"

@Composable
@Preview
fun App(navController: NavHostController = rememberNavController()) {
    RachaiTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = LoginRoute) {

                composable<LoginRoute> {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(GroupsRoute) {
                                popUpTo(LoginRoute) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = { navController.navigate(RegisterRoute()) }
                    )
                }

                composable<RegisterRoute> { entry ->
                    val route: RegisterRoute = entry.toRoute()
                    RegisterScreen(
                        prefillEmail = route.prefillEmail,
                        onRegisterSuccess = {
                            navController.navigate(LoginRoute) {
                                popUpTo<RegisterRoute> { inclusive = true }
                            }
                        },
                        onNavigateToLogin = { navController.popBackStack() }
                    )
                }

                composable<GroupsRoute>(
                    deepLinks = listOf(navDeepLink<GroupsRoute>(basePath = "$DEEP_LINK_BASE/home"))
                ) {
                    GroupsScreen(
                        onOpenGroup = { group ->
                            navController.navigate(GroupDetailRoute(group.id, group.name))
                        },
                        onCreateGroup = { navController.navigate(CreateGroupRoute) },
                        onOpenProfile = { navController.navigate(ProfileRoute) }
                    )
                }

                composable<CreateGroupRoute> {
                    CreateGroupScreen(
                        onBack = { navController.popBackStack() },
                        onCreated = { group ->
                            navController.navigate(GroupDetailRoute(group.id, group.name)) {
                                popUpTo<CreateGroupRoute> { inclusive = true }
                            }
                        }
                    )
                }

                composable<GroupDetailRoute> { entry ->
                    val route: GroupDetailRoute = entry.toRoute()
                    GroupDetailScreen(
                        groupId = route.groupId,
                        groupName = route.groupName,
                        onBack = { navController.popBackStack() },
                        onAddExpense = { navController.navigate(AddExpenseRoute(it)) },
                        onOpenExpense = { },
                        onAddMember = { navController.navigate(AddMemberRoute(it)) }
                    )
                }

                composable<AddExpenseRoute> { entry ->
                    val route: AddExpenseRoute = entry.toRoute()
                    AddExpenseScreen(
                        groupId = route.groupId,
                        onBack = { navController.popBackStack() },
                        onCreated = { navController.popBackStack() }
                    )
                }

                composable<AddMemberRoute> { entry ->
                    val route: AddMemberRoute = entry.toRoute()
                    AddMemberScreen(
                        groupId = route.groupId,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<ProfileRoute> {
                    ProfileScreen(
                        onBack = { navController.popBackStack() },
                        onLoggedOut = {
                            navController.navigate(LoginRoute) { popUpTo(0) { inclusive = true } }
                        }
                    )
                }
            }
        }
    }
}
