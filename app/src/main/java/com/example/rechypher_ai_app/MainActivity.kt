package com.example.rechypher_ai_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.rechypher_ai_app.data.AuthRepository
import com.example.rechypher_ai_app.ui.screens.HomeScreen
import com.example.rechypher_ai_app.ui.screens.LoginScreen
import com.example.rechypher_ai_app.ui.screens.MainScreen
import com.example.rechypher_ai_app.ui.theme.ReChypherAiAppTheme
import com.example.rechypher_ai_app.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    private lateinit var authRepository: AuthRepository
    private lateinit var authViewModel: AuthViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize authentication components
        authRepository = AuthRepository(this)
        authViewModel = AuthViewModel(authRepository)
        
        setContent {
            ReChypherAiAppTheme {
                val authState by authViewModel.authState.collectAsState()
                
                // Show LoginScreen if user is not authenticated, otherwise show MainScreen
                if (authState.user == null) {
                    LoginScreen(
                        authRepository = authRepository,
                        authViewModel = authViewModel,
                        onSignInSuccess = {
                            // Navigation handled by state change
                        }
                    )
                } else {
                    MainScreen(
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}