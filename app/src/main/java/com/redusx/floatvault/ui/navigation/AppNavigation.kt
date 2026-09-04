package com.redusx.floatvault.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.redusx.floatvault.ui.screen.AddEditEntryScreen
import com.redusx.floatvault.ui.screen.AuthScreen
import com.redusx.floatvault.ui.screen.EntryDetailScreen
import com.redusx.floatvault.ui.screen.HomeScreen
import com.redusx.floatvault.ui.screen.SettingsScreen

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
    onBiometricRequested: (onSuccess: () -> Unit) -> Unit,
    onBiometricResetRequested: (onSuccess: () -> Unit, onError: () -> Unit) -> Unit,
    onDeviceCredentialRequested: (title: String, subtitle: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.AUTH
    ) {
        composable(
            route = Routes.AUTH,
            exitTransition = {
                fadeOut(animationSpec = tween(280)) +
                slideOutVertically(
                    targetOffsetY = { -it / 6 },
                    animationSpec = tween(280, easing = FastOutSlowInEasing)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(260)) +
                scaleIn(initialScale = 1.04f, animationSpec = tween(260, easing = FastOutSlowInEasing))
            }
        ) {
            AuthScreen(
                onAuthenticated = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.AUTH) { inclusive = true }
                    }
                },
                onBiometricRequested = onBiometricRequested,
                onBiometricResetRequested = onBiometricResetRequested,
                onDeviceCredentialRequested = onDeviceCredentialRequested
            )
        }

        composable(
            route = Routes.HOME,
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                scaleIn(initialScale = 0.95f, animationSpec = tween(300, easing = FastOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(220))
            }
        ) {
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
