package com.example.quotevault

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.quotevault.data.remote.QuoteApi
import com.example.quotevault.data.remote.createHttpClient
import com.example.quotevault.data.local.rememberQuoteDatabase
import com.example.quotevault.data.repository.QuoteRepositoryImpl
import com.example.quotevault.domain.usecase.GetQuotesUseCase
import com.example.quotevault.domain.usecase.SearchQuotesUseCase
import com.example.quotevault.domain.usecase.ToggleFavoriteUseCase
import com.example.quotevault.presentation.BrowseQuotesViewModel
import com.example.quotevault.presentation.screens.BrowseQuotesScreen
import com.example.quotevault.presentation.screens.DetailQuoteScreen
import com.example.quotevault.presentation.screens.FavoritesQuotesScreen
import kotlinx.serialization.Serializable

private object Destination {
    const val BROWSE = "browse"
    const val FAVORITES = "favorites"
}

@Serializable
private data class QuoteDetail(val quoteId: Int)

private val LatteCream = Color(0xFFFAF6F0)
private val EspressoBrown = Color(0xFF4E342E)
private val MochaBrown = Color(0xFF8D6E63)
private val Terracotta = Color(0xFFB66A50)

private val QuoteVaultColorScheme: ColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    secondary = MochaBrown,
    onSecondary = Color.White,
    background = LatteCream,
    onBackground = EspressoBrown,
    surface = LatteCream,
    onSurface = EspressoBrown,
    surfaceVariant = Color(0xFFEDE3D8),
    onSurfaceVariant = MochaBrown,
)

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val client = remember { createHttpClient() }
    val database = rememberQuoteDatabase()
    DisposableEffect(client) {
        onDispose { client.close() }
    }

    val repository = remember(client, database, scope) {
        QuoteRepositoryImpl(QuoteApi(client), database.favoriteQuoteDao(), scope)
    }
    val viewModel = remember(scope, repository) {
        BrowseQuotesViewModel(
            getQuotes = GetQuotesUseCase(repository),
            searchQuotes = SearchQuotesUseCase(),
            toggleFavorite = ToggleFavoriteUseCase(repository),
            repository = repository,
            scope = scope,
        )
    }

    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favoriteIds = remember(favorites) { favorites.mapTo(mutableSetOf()) { it.id } }
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Destination.BROWSE
    var query by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.loadQuotes()
        viewModel.events.collect { message = it }
    }

    MaterialTheme(colorScheme = QuoteVaultColorScheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = Destination.BROWSE,
                    modifier = Modifier.weight(1f),
                ) {
                    composable(Destination.BROWSE) {
                        BrowseQuotesScreen(
                            state = uiState,
                            query = query,
                            favoriteIds = favoriteIds,
                            message = message,
                            onSearch = {
                                query = it
                                viewModel.onSearch(it)
                            },
                            onRefresh = { viewModel.loadQuotes(forceRefresh = true) },
                            onQuoteClick = { id -> navController.navigate(QuoteDetail(id)) },
                            onToggleFavorite = viewModel::onToggleFavorite,
                        )
                    }

                    composable(Destination.FAVORITES) {
                        FavoritesQuotesScreen(
                            favorites = favorites,
                            favoriteIds = favoriteIds,
                            message = message,
                            onQuoteClick = { id ->
                                navController.navigate(QuoteDetail(id))
                            },
                            onToggleFavorite = viewModel::onToggleFavorite,
                        )
                    }

                    composable<QuoteDetail> { entry ->
                        val quoteId = entry.toRoute<QuoteDetail>().quoteId
                        DetailQuoteScreen(
                            quote = viewModel.findQuote(quoteId),
                            isFavorite = quoteId in favoriteIds,
                            onToggleFavorite = viewModel::onToggleFavorite,
                            onBack = { navController.popBackStack() },
                        )
                    }
                }

                if (currentRoute in setOf(
                        Destination.BROWSE,
                        Destination.FAVORITES,
                    )
                ) {
                    AppNavigationBar(
                        currentRoute = currentRoute,
                        navController = navController,
                    )
                }
            }
        }
    }
}

@Composable
private fun AppNavigationBar(
    currentRoute: String,
    navController: NavHostController,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        NavigationButton("Browse", currentRoute == Destination.BROWSE) {
            navigateToTab(navController, Destination.BROWSE)
        }
        NavigationButton("Favorites", currentRoute == Destination.FAVORITES) {
            navigateToTab(navController, Destination.FAVORITES)
        }
    }
}

private fun navigateToTab(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun NavigationButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    if (selected) {
        Button(onClick = onClick) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick) { Text(label) }
    }
}
