package com.example.rechypher_ai_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rechypher_ai_app.ui.functions.BottomNavBar
import com.example.rechypher_ai_app.viewmodel.ScanHistoryViewModel

@Composable
fun MainScreen() {
    var selectedScreen by remember { mutableStateOf(0) }
    var navigateToNearestCenter by remember { mutableStateOf(false) }
    
    // Shared ViewModel for scan history
    val scanHistoryViewModel: ScanHistoryViewModel = viewModel()
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Main content with bottom padding for the nav bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            when (selectedScreen) {
                0 -> HomeScreen(scanHistoryViewModel = scanHistoryViewModel)
                1 -> MapScreen(navigateToNearest = navigateToNearestCenter)
                2 -> CameraScreen(
                    onBackClick = { selectedScreen = 0 },
                    scanHistoryViewModel = scanHistoryViewModel,
                    onNavigateToMap = { 
                        navigateToNearestCenter = true
                        selectedScreen = 1
                    }
                )
                3 -> ChatbotScreen(onClose = { selectedScreen = 0 })
            }
        }
        
        // White spacer to cover any grey line
        Box(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .fillMaxWidth()
                .height(82.dp)
                .background(Color.White)
        )
        
        // Bottom navigation bar
        Box(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            BottomNavBar(
                selectedItem = selectedScreen,
                onItemSelected = { 
                    // Reset navigateToNearestCenter when manually navigating
                    if (it != 1) {
                        navigateToNearestCenter = false
                    }
                    selectedScreen = it
                }
            )
        }
    }
}
