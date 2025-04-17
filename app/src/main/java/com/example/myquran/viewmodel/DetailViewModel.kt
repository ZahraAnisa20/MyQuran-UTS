package com.example.myquran.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myquran.data.Ayat
import com.example.myquran.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    private val _ayahList = MutableStateFlow<List<Ayat>>(emptyList())
    val ayahList: StateFlow<List<Ayat>> = _ayahList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getAyatBySurah(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getAyatWithAudio(id)

                val arabicAyat = response.data.find { it.edition.identifier == "quran-uthmani" }?.ayahs ?: emptyList()
                val translationAyat = response.data.find { it.edition.identifier == "id.indonesian" }?.ayahs ?: emptyList()
                val audioAyat = response.data.find { it.edition.identifier == "ar.abdulbasitmurattal" }?.ayahs ?: emptyList()

                _ayahList.value = arabicAyat.mapIndexed { index, arab ->
                    Ayat(
                        number = arab.number,
                        arabicText = arab.text,
                        translationText = translationAyat.getOrNull(index)?.text ?: "",
                        audioUrl = audioAyat.getOrNull(index)?.audio ?: ""
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}