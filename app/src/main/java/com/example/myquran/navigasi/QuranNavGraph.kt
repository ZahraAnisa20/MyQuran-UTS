package com.example.myquran.navigasi

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myquran.HomeScreen.HomeScreen
import com.example.myquran.googleclient.GoogleClient
import com.example.myquran.listscreen.DetailSurahScreen
import com.example.myquran.listscreen.SurahListScreen
import com.example.myquran.splashscreen.SplashScreen
import com.example.myquran.viewmodel.DetailViewModel
import com.example.myquran.viewmodel.SurahViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@Composable
fun QuranNavGraph(
    navController: NavHostController,
    googleClient: GoogleClient,
    currentAccount: GoogleSignInAccount?,
    onAccountChanged: (GoogleSignInAccount?) -> Unit // ✅ Tambahkan ini
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                googleClient = googleClient,
                account = currentAccount,
                onAccountChanged = onAccountChanged // ✅ Kirim ke HomeScreen
            )
        }
        composable("surah_list") {
            val viewModel: SurahViewModel = viewModel()
            SurahListScreen(
                navController = navController,
                viewModel = viewModel,
                account = currentAccount
            )
        }
        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 1
            val viewModel: DetailViewModel = viewModel()
            DetailSurahScreen(
                surahId = id,
                viewModel = viewModel,
                account = currentAccount
            )
        }
    }
}
