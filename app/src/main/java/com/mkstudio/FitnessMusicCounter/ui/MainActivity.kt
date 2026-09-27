package com.mkstudio.FitnessMusicCounter.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mkstudio.FitnessMusicCounter.model.WorkoutConfig
import com.mkstudio.FitnessMusicCounter.model.WorkoutMode
import com.mkstudio.FitnessMusicCounter.ui.theme.BgDark
import com.mkstudio.FitnessMusicCounter.ui.theme.FitnessTheme
import com.mkstudio.FitnessMusicCounter.ui.viewmodel.CountViewModel
import com.mkstudio.FitnessMusicCounter.ui.viewmodel.MainViewModel
import com.mkstudio.FitnessMusicCounter.ui.views.count.WorkoutScreen
import com.mkstudio.FitnessMusicCounter.ui.views.setup.SetupScreen
import com.mkstudio.FitnessMusicCounter.ui.views.stat.StatScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val countViewModel: CountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind radio playback service
        mainViewModel.bindRadio()

        setContent {
            FitnessTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgDark)
                ) {
                    FitnessAppNavigation(
                        mainViewModel = mainViewModel,
                        countViewModel = countViewModel
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.unbindRadio()
    }
}

@Composable
fun FitnessAppNavigation(
    mainViewModel: MainViewModel,
    countViewModel: CountViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "setup"
    ) {
        composable("setup") {
            SetupScreen(
                viewModel = mainViewModel,
                onStartWorkout = { config ->
                    navController.navigate("workout/${config.mode.id}/${config.targetSets}/${config.restDurationSeconds}/${config.workDurationSeconds}")
                },
                onNavigateToStats = {
                    navController.navigate("stat")
                }
            )
        }

        composable(
            route = "workout/{modeId}/{targetSets}/{restSec}/{workSec}",
            arguments = listOf(
                navArgument("modeId") { type = NavType.IntType },
                navArgument("targetSets") { type = NavType.IntType },
                navArgument("restSec") { type = NavType.IntType },
                navArgument("workSec") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val modeId = backStackEntry.arguments?.getInt("modeId") ?: 2
            val targetSets = backStackEntry.arguments?.getInt("targetSets") ?: 10
            val restSec = backStackEntry.arguments?.getInt("restSec") ?: 60
            val workSec = backStackEntry.arguments?.getInt("workSec") ?: 20

            val mode = WorkoutMode.values().find { it.id == modeId } ?: WorkoutMode.REST_TIMER
            val config = WorkoutConfig(
                mode = mode,
                targetSets = targetSets,
                restDurationSeconds = restSec,
                workDurationSeconds = workSec
            )

            WorkoutScreen(
                countViewModel = countViewModel,
                mainViewModel = mainViewModel,
                workoutConfig = config,
                onFinishWorkout = {
                    navController.popBackStack()
                }
            )
        }

        composable("stat") {
            StatScreen(
                viewModel = mainViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}