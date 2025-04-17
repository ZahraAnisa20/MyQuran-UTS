package com.example.myquran.navigasi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myquran.HomeScreen.HomeScreen
import com.example.myquran.listscreen.DetailSurahScreen
import com.example.myquran.listscreen.SurahListScreen
import com.example.myquran.viewmodel.DetailViewModel
import com.example.myquran.viewmodel.SurahViewModel

@Composable
fun QuranNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "home") {

        // 🏠 Home screen
        composable("home") {
            HomeScreen(navController = navController)
        }

        // 📖 Surah list
        composable("surah_list") {
            val viewModel: SurahViewModel = viewModel()
            SurahListScreen(navController = navController, viewModel = viewModel)
        }

        // 📄 Detail ayat by surah
        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
            val viewModel: DetailViewModel = viewModel()
            DetailSurahScreen(surahId = id, viewModel = viewModel)
        }
    }
}