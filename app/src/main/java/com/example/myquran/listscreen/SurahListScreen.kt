package com.example.myquran.listscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myquran.viewmodel.SurahViewModel
import com.example.myquran.data.Surah
import com.example.myquran.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahListScreen(navController: NavController, viewModel: SurahViewModel) {
    val surahList = viewModel.surahList.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    val backgroundColor = Color(0xFFD6F0FF) // c\biru muda dari gambar

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("\uD83D\uDD4C Surah \uD83D\uDD4C") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari surah...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            } else {
                val filteredList = surahList.filter {
                    it.name.contains(searchQuery.text, ignoreCase = true) ||
                            it.englishName.contains(searchQuery.text, ignoreCase = true) ||
                            it.englishNameTranslation.contains(searchQuery.text, ignoreCase = true)
                }

                if (filteredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("❌ Tidak ada surah ditemukan")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        items(filteredList) { surah ->
                            SurahCard(surah = surah, onClick = {
                                navController.navigate("detail/${surah.number}")
                            })
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun SurahCard(
    surah: Surah,
    onClick: () -> Unit
) {
    val cardColor = Color(0xFFFFE4EC) // warna pink

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = surah.englishName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${surah.englishNameTranslation} • Ayat: ${surah.numberOfAyahs}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Image(
                painter = painterResource(id = R.drawable.logo1),
                contentDescription = "Icon Surah",
                modifier = Modifier
                    .size(80.dp)
                    .padding(start = 16.dp)
            )
        }
    }
}
