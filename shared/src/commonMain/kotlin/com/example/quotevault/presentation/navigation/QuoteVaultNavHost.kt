package com.example.quotevault.presentation.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.savedstate.read
import com.example.quotevault.presentation.BrowseQuotesViewModel
import com.example.quotevault.presentation.screens.BrowseQuotesScreen
import com.example.quotevault.presentation.screens.DetailQuoteScreen
import com.example.quotevault.presentation.screens.FavoritesQuotesScreen
import com.example.quotevault.presentation.screens.SettingsScreen

private data class TabItem(
    val destination: AppDestination,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun QuoteVaultNavHost(
    viewModel: BrowseQuotesViewModel,
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.query.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favoriteIds = remember(favorites) { favorites.mapTo(mutableSetOf()) { it.id } }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(viewModel) {
        viewModel.loadQuotes()
        viewModel.events.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val tabs = listOf(
        TabItem(AppDestination.Browse, "Browse", Icons.Default.Home),
        TabItem(AppDestination.Favorites, "Favorites", Icons.Default.Favorite),
        TabItem(AppDestination.Settings, "Settings", Icons.Default.Settings),
    )

    fun openDetail(quoteId: Int) {
        navController.navigate(AppDestination.Detail.createRoute(quoteId))
    }

    fun openTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp
        val showBottomBar = !useNavigationRail && currentRoute != AppDestination.Detail.route

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        tabs.forEach { tab ->
                            NavigationBarItem(
                                selected = currentRoute == tab.destination.route,
                                onClick = { openTab(tab.destination.route) },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (useNavigationRail) {
                    NavigationRail {
                        tabs.forEach { tab ->
                            NavigationRailItem(
                                selected = currentRoute == tab.destination.route,
                                onClick = { openTab(tab.destination.route) },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                }
                NavHost(
                    navController = navController,
                    startDestination = AppDestination.Browse.route,
                    modifier = Modifier.weight(1f).fillMaxSize(),
                ) {
                    composable(AppDestination.Browse.route) {
                        BrowseQuotesScreen(
                            state = uiState,
                            query = query,
                            favoriteIds = favoriteIds,
                            onSearch = viewModel::onSearch,
                            onRefresh = { viewModel.loadQuotes(forceRefresh = true) },
                            onQuoteClick = ::openDetail,
                            onToggleFavorite = viewModel::onToggleFavorite,
                        )
                    }
                    composable(AppDestination.Favorites.route) {
                        FavoritesQuotesScreen(
                            favorites = favorites,
                            favoriteIds = favoriteIds,
                            onQuoteClick = ::openDetail,
                            onToggleFavorite = viewModel::onToggleFavorite,
                        )
                    }
                    composable(AppDestination.Settings.route) {
                        SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onDarkThemeChange = onDarkThemeChange,
                        )
                    }
                    composable(
                        route = AppDestination.Detail.route,
                        arguments = listOf(
                            navArgument(AppDestination.Detail.ARG_QUOTE_ID) {
                                type = NavType.IntType
                            },
                        ),
                        deepLinks = listOf(
                            navDeepLink { uriPattern = AppDestination.Detail.DEEP_LINK },
                        ),
                    ) { entry ->
                        val quoteId = entry.arguments?.read {
                            getInt(AppDestination.Detail.ARG_QUOTE_ID)
                        }
                        val quote = quoteId?.let(viewModel::findQuote)
                        DetailQuoteScreen(
                            quote = quote,
                            isFavorite = quoteId != null && quoteId in favoriteIds,
                            onToggleFavorite = viewModel::onToggleFavorite,
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
