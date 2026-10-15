package io.gitforge.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import io.gitforge.core.designsystem.component.bounceClick
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

    val currentScreenTitle = when (currentRoute) {
        Screen.Home.route -> "GitForge"
        Screen.Repositories.route -> "Repositories"
        Screen.CodeStudio.route -> "Code Studio"
        Screen.Notifications.route -> "Inbox & Activity"
        Screen.Profile.route -> "Developer Profile"
        else -> "GitForge"
    }

    Scaffold(
        topBar = {
            if (isPrimaryScreen && currentRoute != Screen.Home.route && currentRoute != Screen.Profile.route) {
                TopAppBar(
                    title = {
                        Text(
                            text = currentScreenTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.ui.graphics.Color(0xFF14161B)
                    ),
                    actions = {
                        IconButton(
                            onClick = { navController.navigate(Screen.Terminal.route) }
                        ) {
                            Icon(Icons.Default.Terminal, contentDescription = "Terminal", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(
                            onClick = { navController.navigate(Screen.Settings.route) }
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (isPrimaryScreen) {
                val dockItems = primaryDestinations.map { screen ->
                    screen.title to screen.icon
                }
                val selectedIndex = primaryDestinations.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 6.dp)
                ) {
                    io.gitforge.core.designsystem.component.EssentialsFloatingDock(
                        items = dockItems,
                        selectedIndex = selectedIndex,
                        onItemSelected = { index ->
                            val screen = primaryDestinations[index]
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        onActionClick = {
                            navController.navigate(Screen.Terminal.route)
                        },
                        actionIcon = Icons.Default.Terminal
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(220))
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(280)
                ) + fadeOut(animationSpec = tween(200))
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(220))
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(280)
                ) + fadeOut(animationSpec = tween(200))
            }
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
                val homeState by homeViewModel.uiState.collectAsState()
                ProductivityScreen(
                    initialSection = section,
                    availableRepos = homeState.recentRepositories,
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
