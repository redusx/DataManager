package com.example.datamanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.datamanager.ui.screen.AddEditEntryScreen
import com.example.datamanager.ui.screen.AuthScreen
import com.example.datamanager.ui.screen.EntryDetailScreen
import com.example.datamanager.ui.screen.HomeScreen
import com.example.datamanager.ui.screen.SettingsScreen

object Routes {
    const val AUTH = "auth"
    const val HOME = "home"
    const val ENTRY_DETAIL = "entry/{entryId}"
    const val ADD_ENTRY = "addEntry?category={category}&template={template}"
    const val EDIT_ENTRY = "editEntry/{entryId}"
    const val SETTINGS = "settings"

    fun entryDetail(entryId: Long) = "entry/$entryId"
    fun addEntry(category: String? = null, template: String? = null) =
        "addEntry?category=${category ?: ""}&template=${template ?: ""}"
    fun editEntry(entryId: Long) = "editEntry/$entryId"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    onBiometricRequested: (onSuccess: () -> Unit) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.AUTH
    ) {
        composable(Routes.AUTH) {
            AuthScreen(
                onAuthenticated = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                onBiometricRequested = onBiometricRequested
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onEntryClick = { entryId ->
                    navController.navigate(Routes.entryDetail(entryId))
                },
                onAddClick = { category, templateId ->
                    navController.navigate(Routes.addEntry(category, templateId))
                },
                onSettingsClick = {
                    navController.navigate(Routes.SETTINGS)
                },
                onLockClick = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.ENTRY_DETAIL,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            EntryDetailScreen(
                entryId = entryId,
                onBackClick = { navController.popBackStack() },
                onEditClick = { id ->
                    navController.navigate(Routes.editEntry(id))
                }
            )
        }

        composable(
            route = Routes.ADD_ENTRY,
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("template") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category")?.takeIf { it.isNotEmpty() }
            val templateId = backStackEntry.arguments?.getString("template")?.takeIf { it.isNotEmpty() }
            AddEditEntryScreen(
                category = category,
                templateId = templateId,
                entryId = null,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_ENTRY,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            AddEditEntryScreen(
                category = null,
                templateId = null,
                entryId = entryId,
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onDataDeleted = {
                    navController.navigate(Routes.AUTH) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
