package com.example.notes_kmp.presentation.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notes_kmp.presentation.route.AppRoute
import com.example.notes_kmp.presentation.screen.detail.DetailScreen
import com.example.notes_kmp.presentation.screen.home.HomeScreen
import com.example.notes_kmp.presentation.theme.ThreadTheme

@Composable
fun NotesApp() {
    ThreadTheme {
        MainNavigation()
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.HomeScreen.name
    ) {
        composable(
            route = AppRoute.HomeScreen.name
        ) {
            HomeScreen(navController = navController)
        }

        composable(
            route = "${AppRoute.DetailScreen.name}?url={url}",
            arguments = listOf(
                navArgument("url") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val url = backStackEntry.savedStateHandle.get<String>("url") ?: ""
            DetailScreen(
                url = url,
                onBack = { navController.navigateUp() }
            )
        }
    }
}