package com.work.stickytrails.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.work.stickytrails.models.NoteModel
import com.work.stickytrails.network.RetrofitInstance
import com.work.stickytrails.network.RetrofitInstance.noteApi
import com.work.stickytrails.utils.TokenManager
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(private val tokenManager: TokenManager) : ViewModel() {
    private val _notes = MutableStateFlow<List<NoteModel>>(emptyList())
    val notes: StateFlow<List<NoteModel>> = _notes

    fun fetchNotes() {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            val response = noteApi.getNotes("Bearer $token")
            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse != null && apiResponse.success) {
                    // Assign the notes list to the StateFlow
                    _notes.value = apiResponse.data.sortedBy { it.priority }
                } else {
                    // Handle API response failure
                    Log.e("FetchNotes", "Error: ${response.errorBody()?.string()}")
                }
            } else {
                // Handle HTTP error
                Log.e("FetchNotes", "HTTP Error: ${response.code()}")
            }
        }
    }
}

