package com.work.stickytrails.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.work.stickytrails.models.LoginRequest
import com.work.stickytrails.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(identifier: String, password: String) {
        _loginState.value = LoginState.Loading

        // Simulate login API call (replace with actual API call)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.loginUser(LoginRequest(identifier, password))
                _loginState.value = LoginState.Success(response.token)
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
