package com.example.quotevault.presentation.navigation

sealed class AppDestination(val route: String) {
    data object Browse : AppDestination("browse")
    data object Favorites : AppDestination("favorites")
    data object Settings : AppDestination("settings")
    data object Detail : AppDestination("quote/{quoteId}") {
        const val ARG_QUOTE_ID = "quoteId"
        const val DEEP_LINK = "quotevault://quote/{quoteId}"

        fun createRoute(quoteId: Int): String = "quote/$quoteId"
    }
}
