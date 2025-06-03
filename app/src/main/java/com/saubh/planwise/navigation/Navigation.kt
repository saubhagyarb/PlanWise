package com.saubh.planwise.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saubh.planwise.screens.ProjectViewModel
import com.saubh.planwise.screens.AddEditScreenUI
import com.saubh.planwise.screens.HomeScreenUI
import com.saubh.planwise.screens.ProjectDetailScreenUI
import com.saubh.planwise.screens.SearchScreenUI

object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val PROJECT_DETAIL = "project_detail"
    const val ADD_EDIT_PROJECT = "add_edit_project"

    // Route with parameters
    fun projectDetailRoute(projectId: Long): String = "$PROJECT_DETAIL/$projectId"
}

@Composable
fun AppNavigation(
    viewModel: ProjectViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            val homeScreenUI = HomeScreenUI(viewModel, navController)
            homeScreenUI.HomeScreen()
        }

        composable(Routes.SEARCH) {
            val searchScreenUI = SearchScreenUI(viewModel, navController)
            searchScreenUI.SearchScreen()
        }

        composable(
            route = "${Routes.PROJECT_DETAIL}/{projectId}",
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0
            val detailScreenUI = ProjectDetailScreenUI(viewModel, navController)
            detailScreenUI.ProjectDetailScreen(projectId)
        }

        composable(
            route = "${Routes.ADD_EDIT_PROJECT}?projectId={projectId}",
            arguments = listOf(navArgument("projectId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: -1L
            val addEditScreenUI = AddEditScreenUI(viewModel, navController)
            addEditScreenUI.AddEditScreen(projectId)
        }
    }
}