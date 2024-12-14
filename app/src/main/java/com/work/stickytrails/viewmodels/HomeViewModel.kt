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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        fetchNotes()
    }

    fun fetchNotes() {
        _isLoading.value = true
        _errorMessage.value = null // Clear any previous error message
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                val response = noteApi.getNotes("Bearer $token")
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        _notes.value = apiResponse.data.sortedBy { it.priority }
                    } else {
                        _errorMessage.value = "Failed to load notes. Try again later."
                    }
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                // Handle network errors or any other exceptions
                _errorMessage.value = "Network error. Please check your connection."
            } finally {
                _isLoading.value = false
            }
        }
    }
}

