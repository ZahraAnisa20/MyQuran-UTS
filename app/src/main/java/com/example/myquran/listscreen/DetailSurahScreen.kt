package com.example.myquran.listscreen

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // ⬅️ Tambah ini
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.myquran.data.Ayat
import com.example.myquran.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailSurahScreen(surahId: Int, viewModel: DetailViewModel) {
    val ayatList = viewModel.ayahList.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(surahId) {
        viewModel.getAyatBySurah(surahId)
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detail Surah") })
        },
        containerColor = Color(0xFFD6F0FF) // 🎨 Soft biru di background utama
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 🔍 Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari ayat...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            } else {
                val filteredList = ayatList.filter {
                    it.arabicText.contains(searchQuery.text, ignoreCase = true) ||
                            it.translationText.contains(searchQuery.text, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    items(filteredList) { ayat ->
                        AyatCard(ayat = ayat, onPlayAudio = { url ->
                            try {
                                mediaPlayer?.release()
                                mediaPlayer = MediaPlayer().apply {
                                    setDataSource(url)
                                    prepare()
                                    start()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun AyatCard(ayat: Ayat, onPlayAudio: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFE4EC) // 🎨 Soft pink untuk card ayat
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${ayat.number}. ${ayat.arabicText}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = ayat.translationText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                onPlayAudio(ayat.audioUrl)
            }) {
                Text("▶️ Dengarkan")
            }
        }
    }
}
