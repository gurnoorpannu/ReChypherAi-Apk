package com.example.rechypher_ai_app.ui.functions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rechypher_ai_app.utils.CarbonCalculator

/**
 * Reusable Carbon Impact Card Component
 * Can be embedded in any screen to show carbon calculations
 */
@Composable
fun CarbonImpactCard(
    wasteLabel: String,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val (carbonSaved, carbonEmitted) = CarbonCalculator.calculateCarbon(wasteLabel)
    val netImpact = carbonSaved - carbonEmitted
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (netImpact > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (compact) {
            CompactCarbonView(carbonSaved, carbonEmitted, netImpact)
        } else {
            DetailedCarbonView(carbonSaved, carbonEmitted, netImpact)
        }
    }
}

@Composable
private fun CompactCarbonView(
    carbonSaved: Double,
    carbonEmitted: Double,
    netImpact: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "♻️ ${String.format("%.3f", carbonSaved)} kg",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
            Text(
                text = "Saved",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (netImpact > 0) "🌍" else "⚠️",
                fontSize = 20.sp
            )
            Text(
                text = String.format("%.3f", kotlin.math.abs(netImpact)),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (netImpact > 0) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "🏭 ${String.format("%.3f", carbonEmitted)} kg",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF44336)
            )
            Text(
                text = "Emitted",
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun DetailedCarbonView(
    carbonSaved: Double,
    carbonEmitted: Double,
    netImpact: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Carbon Impact",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CarbonMetric(
                label = "Saved",
                value = carbonSaved,
                icon = "♻️",
                color = Color(0xFF4CAF50)
            )
            
            CarbonMetric(
                label = "Emitted",
                value = carbonEmitted,
                icon = "🏭",
                color = Color(0xFFF44336)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Divider(color = Color.LightGray, thickness = 1.dp)
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = if (netImpact > 0) "Net Benefit" else "Net Cost",
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = "${String.format("%.3f", kotlin.math.abs(netImpact))} kg CO2e",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (netImpact > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    }
}

@Composable
private fun CarbonMetric(
    label: String,
    value: Double,
    icon: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = String.format("%.3f", value),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = "kg CO2e",
            fontSize = 10.sp,
            color = Color.Gray
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
