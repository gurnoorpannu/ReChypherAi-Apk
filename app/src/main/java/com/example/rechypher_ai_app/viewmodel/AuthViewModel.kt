package com.example.rechypher_ai_app.viewmodel

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechypher_ai_app.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    private val _authState = MutableStateFlow(AuthState(user = authRepository.currentUser))
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    // Handle Google Sign-In result
    fun handleSignInResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                
                viewModelScope.launch {
                    authRepository.signInWithGoogle(account).fold(
                        onSuccess = { user ->
                            _authState.value = AuthState(user = user)
                        },
                        onFailure = { exception ->
                            _authState.value = AuthState(error = exception.message ?: "Sign in failed")
                        }
                    )
                }
            } catch (e: ApiException) {
                _authState.value = AuthState(error = "Google Sign-In failed: ${e.message}")
            }
        } else {
            _authState.value = AuthState(error = "Sign in cancelled")
        }
    }
    
    // Sign out
    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState()
    }
    
    // Clear error
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}
