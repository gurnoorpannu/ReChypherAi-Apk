package com.example.rechypher_ai_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rechypher_ai_app.ui.theme.PrimaryGreen
import com.example.rechypher_ai_app.ui.theme.White
import com.example.rechypher_ai_app.viewmodel.CarbonViewModel

/**
 * Carbon Statistics Screen
 * Shows accumulated carbon impact across all scanned items
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarbonStatsScreen(
    onBackClick: () -> Unit = {},
    viewModel: CarbonViewModel = viewModel()
) {
    val totalSaved by viewModel.totalCarbonSaved.collectAsState()
    val totalEmitted by viewModel.totalCarbonEmitted.collectAsState()
    val itemsScanned by viewModel.itemsScanned.collectAsState()
    val totalNetImpact = totalSaved - totalEmitted
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Carbon Statistics",
                        color = White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryGreen,
                    titleContentColor = White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🌍",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your Environmental Impact",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$itemsScanned items scanned",
                        fontSize = 14.sp,
                        color = White.copy(alpha = 0.9f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Total Net Impact Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (totalNetImpact > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (totalNetImpact > 0) "Total Net Benefit" else "Total Net Cost",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format("%.3f", kotlin.math.abs(totalNetImpact)),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (totalNetImpact > 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                    Text(
                        text = "kg CO2e",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                         
       }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Breakdown Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Carbon Saved Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "♻️",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = String.format("%.3f", totalSaved),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "kg CO2e",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Saved",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                // Carbon Emitted Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🏭",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = String.format("%.3f", totalEmitted),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF44336)
                        )
                        Text(
                            text = "kg CO2e",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Emitted",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Equivalence Cards
            if (totalNetImpact > 0) {
                EquivalenceCard(
                    title = "That's equivalent to:",
                    items = getEquivalences(totalNetImpact)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reset Button
            if (itemsScanned > 0) {
                OutlinedButton(
                    onClick = { viewModel.resetAll() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFC62828)
                    )
                ) {
                    Text("Reset Statistics")
                }
            }
        }
    }
}

@Composable
private fun EquivalenceCard(
    title: String,
    items: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(12.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "• ",
                        fontSize = 14.sp,
                        color = Color(0xFF424242)
                    )
                    Text(
                        text = item,
                        fontSize = 14.sp,
                        color = Color(0xFF424242)
                    )
                }
            }
        }
    }
}

private fun getEquivalences(kgCO2e: Double): List<String> {
    val equivalences = mutableListOf<String>()
    
    // Driving distance (average car: 0.12 kg CO2e per km)
    val kmDriven = kgCO2e / 0.12
    if (kmDriven >= 1) {
        equivalences.add(String.format("%.1f km of driving", kmDriven))
    }
    
    // Trees planted (one tree absorbs ~21 kg CO2e per year)
    val treeDays = (kgCO2e / 21) * 365
    if (treeDays >= 1) {
        equivalences.add(String.format("%.0f days of a tree's CO2 absorption", treeDays))
    }
    
    // Smartphone charges (0.008 kg CO2e per charge)
    val phoneCharges = kgCO2e / 0.008
    if (phoneCharges >= 1) {
        equivalences.add(String.format("%.0f smartphone charges", phoneCharges))
    }
    
    // LED bulb hours (0.01 kg CO2e per hour)
    val bulbHours = kgCO2e / 0.01
    if (bulbHours >= 1) {
        equivalences.add(String.format("%.0f hours of LED bulb usage", bulbHours))
    }
    
    return equivalences.ifEmpty { listOf("Keep scanning to see more impact!") }
}
