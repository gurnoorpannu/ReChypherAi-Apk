package com.example.rechypher_ai_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rechypher_ai_app.utils.CarbonCalculator
import com.example.rechypher_ai_app.utils.WasteCategorizer

/**
 * Carbon Impact Screen
 * Displays carbon footprint calculation for identified waste items
 */
@Composable
fun CarbonImpactScreen(
    wasteLabel: String,
    modifier: Modifier = Modifier
) {
    val (carbonSaved, carbonEmitted) = CarbonCalculator.calculateCarbon(wasteLabel)
    val netImpact = carbonSaved - carbonEmitted
    val binType = WasteCategorizer.getBinType(wasteLabel)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Carbon Impact Analysis",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Waste Item Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = wasteLabel.uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = binType.color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = WasteCategorizer.getBinTypeWithEmoji(wasteLabel),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Carbon Saved Card
        CarbonMetricCard(
            title = "Carbon Saved",
            value = carbonSaved,
            icon = "♻️",
            color = Color(0xFF4CAF50),
            description = "By proper disposal/recycling"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Carbon Emitted Card
        CarbonMetricCard(
            title = "Carbon Emitted",
            value = carbonEmitted,
            icon = "🏭",
            color = Color(0xFFF44336),
            description = "If sent to landfill"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Net Impact Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (netImpact > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (netImpact > 0) "🌍 Net Positive Impact" else "⚠️ Net Negative Impact",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (netImpact > 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${String.format("%.3f", kotlin.math.abs(netImpact))} kg CO2e",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (netImpact > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (netImpact > 0) "saved by recycling" else "cost if not recycled",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "💡 Did you know?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = getWasteFactText(wasteLabel),
                    fontSize = 14.sp,
                    color = Color(0xFF424242),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun CarbonMetricCard(
    title: String,
    value: Double,
    icon: String,
    color: Color,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(
                text = icon,
                fontSize = 40.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format("%.3f", value)} kg CO2e",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun getWasteFactText(label: String): String {
    return when (label.lowercase()) {
        "battery" -> "Batteries contain toxic materials. Proper disposal prevents soil and water contamination while recovering valuable metals."
        "biological" -> "Composting organic waste reduces methane emissions from landfills and creates nutrient-rich soil."
        "plastic" -> "Recycling one ton of plastic saves approximately 1.5 tons of CO2 emissions compared to producing new plastic."
        "paper", "cardboard" -> "Recycling paper saves trees and uses 70% less energy than making paper from raw materials."
        "metal" -> "Recycling aluminum saves 95% of the energy needed to make new aluminum from raw materials."
        "clothes", "shoes" -> "Textile recycling reduces water pollution and saves resources. One recycled shirt saves 2,700 liters of water."
        "brown-glass", "green-glass", "white-glass" -> "Glass can be recycled endlessly without loss of quality, saving raw materials and energy."
        "trash" -> "General waste in landfills produces methane, a greenhouse gas 25x more potent than CO2."
        else -> "Proper waste segregation is key to reducing environmental impact and conserving resources."
    }
}
