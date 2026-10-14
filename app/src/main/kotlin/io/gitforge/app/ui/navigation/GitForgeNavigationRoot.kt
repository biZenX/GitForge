package io.gitforge.app.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.gitforge.app.ui.home.HomeScreen
import io.gitforge.app.ui.home.HomeViewModel
import io.gitforge.app.ui.notifications.NotificationsScreen
import io.gitforge.app.ui.productivity.ProductivityScreen
import io.gitforge.app.ui.productivity.ProductivityViewModel
import io.gitforge.app.ui.profile.ProfileScreen
import io.gitforge.app.ui.repos.RepoDetailScreen
import io.gitforge.app.ui.repos.RepoDetailViewModel
import io.gitforge.app.ui.repos.RepositoriesScreen
import io.gitforge.app.ui.repos.RepositoriesViewModel
import io.gitforge.app.ui.settings.SettingsScreen
import io.gitforge.app.ui.settings.SettingsViewModel
import io.gitforge.app.ui.workspace.CodeStudioTabScreen
import io.gitforge.app.ui.workspace.WorkspaceViewModel
import io.gitforge.core.designsystem.theme.ForgeEmerald
import io.gitforge.feature.terminal.TerminalScreen
import java.io.File

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Repositories : Screen("repositories", "Repos", Icons.Outlined.Folder)
    data object CodeStudio : Screen("codestudio", "Studio", Icons.Default.EditNote)
    data object Notifications : Screen("notifications", "Inbox", Icons.Outlined.Notifications)
    data object Profile : Screen("profile", "Profile", Icons.Outlined.Person)

    // Secondary routes
    data object RepoDetail : Screen("repos/{owner}/{repo}", "Repository", Icons.Outlined.Folder) {
        fun createRoute(owner: String, repo: String) = "repos/$owner/$repo"
    }
    data object Terminal : Screen("terminal", "Terminal", Icons.Default.Terminal)
    data object Productivity : Screen("productivity/{section}", "Productivity", Icons.Default.Dashboard) {
        fun createRoute(section: String) = "productivity/$section"
    }
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val primaryDestinations = listOf(
    Screen.Home,
    Screen.Repositories,
    Screen.CodeStudio,
    Screen.Notifications,
    Screen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitForgeNavigationRoot(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isPrimaryScreen = primaryDestinations.any { it.route == currentRoute }

    // Shared ViewModels
    val homeViewModel: HomeViewModel = hiltViewModel()
    val reposViewModel: RepositoriesViewModel = hiltViewModel()
    val workspaceViewModel: WorkspaceViewModel = hiltViewModel()
    val productivityViewModel: ProductivityViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    val context = LocalContext.current
    val workspaceRoot = remember(context) { File(context.filesDir, "workspaces").apply { if (!exists()) mkdirs() } }

    Scaffold(
        topBar = {
            if (isPrimaryScreen) {
                TopAppBar(
                    title = {
                        Text(
                            text = "GitForge",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.Terminal.route) }) {
                            Icon(Icons.Default.Terminal, contentDescription = "Terminal", tint = MaterialTheme.colorScheme.tertiary)
                        }
                        IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (isPrimaryScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 6.dp
                ) {
                    primaryDestinations.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title, modifier = Modifier.size(24.dp)) },
                            label = { Text(screen.title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToRepo = { owner, repo ->
                        navController.navigate(Screen.RepoDetail.createRoute(owner, repo))
                    },
                    onNavigateToTerminal = { navController.navigate(Screen.Terminal.route) },
                    onNavigateToStudio = { navController.navigate(Screen.CodeStudio.route) },
                    onNavigateToProductivity = { section ->
                        navController.navigate(Screen.Productivity.createRoute(section))
                    },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Repositories.route) {
                RepositoriesScreen(
                    viewModel = reposViewModel,
                    onNavigateToRepo = { owner, repo ->
                        navController.navigate(Screen.RepoDetail.createRoute(owner, repo))
                    }
                )
            }

            composable(Screen.CodeStudio.route) {
                CodeStudioTabScreen(
                    viewModel = workspaceViewModel,
                    onNavigateToTerminal = { navController.navigate(Screen.Terminal.route) }
                )
            }

            composable(Screen.Notifications.route) {
                NotificationsScreen(viewModel = productivityViewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    homeViewModel = homeViewModel,
                    settingsViewModel = settingsViewModel,
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(
                route = Screen.RepoDetail.route,
                arguments = listOf(
                    navArgument("owner") { type = NavType.StringType },
                    navArgument("repo") { type = NavType.StringType }
                )
            ) { backStack ->
                val owner = backStack.arguments?.getString("owner") ?: ""
                val repo = backStack.arguments?.getString("repo") ?: ""
                val repoDetailViewModel: RepoDetailViewModel = hiltViewModel()

                RepoDetailScreen(
                    owner = owner,
                    repo = repo,
                    viewModel = repoDetailViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Terminal.route) {
                TerminalScreen(workspaceRoot = workspaceRoot)
            }

            composable(
                route = Screen.Productivity.route,
                arguments = listOf(
                    navArgument("section") { type = NavType.StringType; defaultValue = "issues" }
                )
            ) { backStack ->
                val section = backStack.arguments?.getString("section") ?: "issues"
                ProductivityScreen(
                    initialSection = section,
                    viewModel = productivityViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
