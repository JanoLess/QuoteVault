package com.example.quotevault

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.quotevault.di.createAppGraph
import com.example.quotevault.presentation.navigation.QuoteVaultNavHost
import com.example.quotevault.presentation.theme.QuoteVaultTheme

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val graph = remember(scope) { createAppGraph(scope) }
    DisposableEffect(graph) {
        onDispose { graph.close() }
    }

    var isDarkTheme by remember { mutableStateOf(graph.settings.isDarkMode()) }

    QuoteVaultTheme(darkTheme = isDarkTheme) {
        QuoteVaultNavHost(
            viewModel = graph.viewModel,
            isDarkTheme = isDarkTheme,
            onDarkThemeChange = { enabled ->
                isDarkTheme = enabled
                graph.settings.setDarkMode(enabled)
            },
        )
    }
}
