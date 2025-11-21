package com.example.rechypher_ai_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.rechypher_ai_app.ui.screens.HomeScreen
import com.example.rechypher_ai_app.ui.screens.MainScreen
import com.example.rechypher_ai_app.ui.theme.ReChypherAiAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReChypherAiAppTheme {
                MainScreen()
            }
        }
    }
}