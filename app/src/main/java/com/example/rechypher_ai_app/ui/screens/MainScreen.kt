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
    var showChatbot by remember { mutableStateOf(false) }
    
    // Shared ViewModel for scan history
    val scanHistoryViewModel: ScanHistoryViewModel = viewModel()
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (showChatbot) {
            // Show chatbot in full screen
            ChatbotScreen(
                onClose = { showChatbot = false }
            )
        } else {
            // Main content with bottom padding for the nav bar
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                when (selectedScreen) {
                    0 -> HomeScreen(scanHistoryViewModel = scanHistoryViewModel)
                    1 -> MapScreen() // Placeholder for wallet/card screen
                    2 -> CameraScreen(
                        onBackClick = { selectedScreen = 0 },
                        scanHistoryViewModel = scanHistoryViewModel
                    )
                    3 -> MapScreen() // Placeholder for shopping cart screen
                    4 -> MapScreen() // Placeholder for profile screen
                }
            }
            
            // Chatbot FAB
            FloatingActionButton(
                onClick = { showChatbot = true },
                modifier = Modifier
                    .align(androidx.compose.ui.Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 96.dp),
                containerColor = Color(0xFF7BA589),
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Waste Assistant",
                    modifier = Modifier.size(24.dp)
                )
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
                    onItemSelected = { selectedScreen = it }
                )
            }
        }
    }
}
