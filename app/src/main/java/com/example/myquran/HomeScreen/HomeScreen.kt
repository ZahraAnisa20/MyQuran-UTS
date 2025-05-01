package com.example.myquran.HomeScreen

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myquran.R
import com.example.myquran.googleclient.GoogleClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    googleClient: GoogleClient,
    account: GoogleSignInAccount?,
    onAccountChanged: (GoogleSignInAccount?) -> Unit
) {
    val context = LocalContext.current
    var showProfileDialog by remember { mutableStateOf(false) }
    var showLoginPrompt by remember { mutableStateOf(account == null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val resultData = result.data
        if (result.resultCode == Activity.RESULT_OK && resultData != null) {
            googleClient.handleSignInResult(
                data = resultData,
                onSuccess = { acc ->
                    onAccountChanged(acc) // ✅ Update global account
                    showLoginPrompt = false
                    Toast.makeText(context, "Login berhasil: ${acc.displayName}", Toast.LENGTH_SHORT).show()
                },
                onError = { e ->
                    Toast.makeText(context, "Login gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📖 Al-Qur'an Digital", color = Color.Black) },
                actions = {
                    account?.let {
                        AsyncImage(
                            model = it.photoUrl,
                            contentDescription = "Foto Profil",
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { showProfileDialog = true }
                        )
                    } ?: Button(onClick = {
                        val signInIntent = googleClient.getSignInIntent()
                        launcher.launch(signInIntent)
                    }) {
                        Text("Login Google")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFE4EC),
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFFFE4EC)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuranButton(
                    onClick = { navController.navigate("surah_list") },
                    enabled = account != null
                )
            }
        }

        // ✅ Dialog Profil
        if (showProfileDialog && account != null) {
            AlertDialog(
                onDismissRequest = { showProfileDialog = false },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = account.photoUrl,
                            contentDescription = "Foto Profil",
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = account.displayName ?: "Tidak ada nama")
                        Text(text = account.email ?: "Tidak ada email", fontSize = 12.sp)
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        googleClient.signOut {
                            onAccountChanged(null) // ✅ Update global account
                            showProfileDialog = false
                            showLoginPrompt = true
                            Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showProfileDialog = false }) {
                        Text("Tutup")
                    }
                }
            )
        }

        // ✅ Dialog Login Prompt
        if (showLoginPrompt && account == null) {
            AlertDialog(
                onDismissRequest = { showLoginPrompt = false },
                title = { Text("Login Diperlukan") },
                text = { Text("Silakan login dengan akun Google terlebih dahulu untuk menjelajahi ayat suci Al-Qur'an.") },
                confirmButton = {
                    TextButton(onClick = { showLoginPrompt = false }) {
                        Text("Oke")
                    }
                }
            )
        }
    }
}

@Composable
fun QuranButton(onClick: () -> Unit, enabled: Boolean) {
    val backgroundColor = if (enabled) Color(0xFF90CAF9) else Color.LightGray
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(enabled = enabled) { if (enabled) onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Jelajahi Al-Qur'an",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
