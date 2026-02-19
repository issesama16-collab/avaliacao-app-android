package com.avaliacao.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.avaliacao.app.ui.screens.DashboardScreen
import com.avaliacao.app.ui.screens.HomeScreen
import com.avaliacao.app.ui.screens.SurveyBuilderScreen
import com.avaliacao.app.ui.screens.SurveyRespondScreen
import com.avaliacao.app.ui.screens.SurveyResultsScreen

sealed class Route(val route: String) {
    object Home : Route("home")
    object Dashboard : Route("dashboard")
    object SurveyBuilder : Route("survey_builder/{surveyId}") {
        fun createRoute(surveyId: Int? = null) = if (surveyId != null) "survey_builder/$surveyId" else "survey_builder/0"
    }
    object SurveyRespond : Route("survey_respond/{slug}") {
        fun createRoute(slug: String) = "survey_respond/$slug"
    }
    object SurveyResults : Route("survey_results/{surveyId}") {
        fun createRoute(surveyId: Int) = "survey_results/$surveyId"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home.route
    ) {
        composable(Route.Home.route) {
            HomeScreen(navController)
        }

        composable(Route.Dashboard.route) {
            DashboardScreen(navController)
        }

        composable(
            route = Route.SurveyBuilder.route,
            arguments = listOf(navArgument("surveyId") { type = NavType.IntType })
        ) { backStackEntry ->
            val surveyId = backStackEntry.arguments?.getInt("surveyId") ?: 0
            SurveyBuilderScreen(navController, surveyId)
        }

        composable(
            route = Route.SurveyRespond.route,
            arguments = listOf(navArgument("slug") { type = NavType.StringType })
        ) { backStackEntry ->
            val slug = backStackEntry.arguments?.getString("slug") ?: ""
            SurveyRespondScreen(navController, slug)
        }

        composable(
            route = Route.SurveyResults.route,
            arguments = listOf(navArgument("surveyId") { type = NavType.IntType })
        ) { backStackEntry ->
            val surveyId = backStackEntry.arguments?.getInt("surveyId") ?: 0
            SurveyResultsScreen(navController, surveyId)
        }
    }
}
