package com.example.myquran

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.myquran.googleclient.GoogleClient
import com.example.myquran.navigasi.QuranNavGraph
import com.example.myquran.ui.theme.MyQuranTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    private lateinit var googleClient: GoogleClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inisialisasi Firebase & Google SignIn
        FirebaseApp.initializeApp(this)
        googleClient = GoogleClient(this)

        setContent {
            MyQuranTheme {
                val navController = rememberNavController()

                // ✅ Gunakan state agar otomatis menyebar saat berubah
                var currentAccount by remember {
                    mutableStateOf(GoogleSignIn.getLastSignedInAccount(this))
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuranNavGraph(
                        navController = navController,
                        googleClient = googleClient,
                        currentAccount = currentAccount,
                        onAccountChanged = { newAccount ->
                            currentAccount = newAccount // ✅ update global state
                        }
                    )
                }
            }
        }
    }
}
