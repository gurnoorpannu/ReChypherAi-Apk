package com.example.rechypher_ai_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.rechypher_ai_app.utils.CarbonCalculator
import com.example.rechypher_ai_app.utils.WasteCategorizer
import java.text.SimpleDateFormat
import java.util.*

data class HistoryItem(
    val wasteLabel: String,
    val title: String,
    val date: String,
    val weight: String,
    val carbonSaved: Double,
    val carbonEmitted: Double
)

@Composable
fun HomeScreen() {
    // Sample history items with carbon calculations
    val historyItems = remember {
        listOf(
            createHistoryItem("plastic", "Plastic bottle recycled"),
            createHistoryItem("paper", "Paper waste recycled"),
            createHistoryItem("cardboard", "Cardboard box recycled"),
            createHistoryItem("metal", "Aluminum can recycled"),
            createHistoryItem("biological", "Food waste composted"),
            createHistoryItem("battery", "Battery properly disposed")
        )
    }
    
    // Calculate total carbon saved this month
    val totalCarbonSaved = remember(historyItems) {
        historyItems.sumOf { it.carbonSaved - it.carbonEmitted }
    }
    
    // Calculate waste type percentages
    val wasteStats = remember(historyItems) {
        calculateWasteStats(historyItems)
    }

    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            // Header Card with real carbon data
            HeaderCard(
                totalCarbonSaved = totalCarbonSaved,
                wasteStats = wasteStats
            )

            // History Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "History",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F)
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historyItems) { item ->
                        HistoryItemCard(item)
                    }
                }
            }
        }
    }
}

private fun createHistoryItem(wasteLabel: String, title: String): HistoryItem {
    val (carbonSaved, carbonEmitted) = CarbonCalculator.calculateCarbon(wasteLabel)
    val carbonData = CarbonCalculator.carbonTable[wasteLabel]
    val weight = carbonData?.avgWeight ?: 0.0
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = dateFormat.format(Date())
    
    return HistoryItem(
        wasteLabel = wasteLabel,
        title = title,
        date = date,
        weight = String.format("%.2f kg", weight),
        carbonSaved = carbonSaved,
        carbonEmitted = carbonEmitted
    )
}

data class WasteStats(
    val plasticPercent: Int,
    val paperPercent: Int,
    val glassPercent: Int
)

private fun calculateWasteStats(items: List<HistoryItem>): WasteStats {
    val total = items.size.toFloat()
    if (total == 0f) return WasteStats(0, 0, 0)
    
    val plasticCount = items.count { it.wasteLabel == "plastic" }
    val paperCount = items.count { it.wasteLabel in listOf("paper", "cardboard") }
    val glassCount = items.count { it.wasteLabel in listOf("brown-glass", "green-glass", "white-glass") }
    
    return WasteStats(
        plasticPercent = ((plasticCount / total) * 100).toInt(),
        paperPercent = ((paperCount / total) * 100).toInt(),
        glassPercent = ((glassCount / total) * 100).toInt()
    )
}

@Composable
fun HeaderCard(
    totalCarbonSaved: Double,
    wasteStats: WasteStats
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFEF3C7),
                        Color(0xFFD9F99D)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF92400E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JW",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Column {
                        Text(
                            text = "Jenny Wilson",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF1F1F1F)
                        )
                        Text(
                            text = "Take care of your nature",
                            fontSize = 12.sp,
                            color = Color(0xFF4B5563)
                        )
                    }
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF374151)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Section with real carbon data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = String.format("%.2f Kg", totalCarbonSaved),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )
                    Text(
                        text = "CO2e saved from\nrecycling this month",
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Legend with real percentages
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LegendItem("${wasteStats.plasticPercent}%", "Plastic", Color(0xFF22C55E))
                        LegendItem("${wasteStats.glassPercent}%", "Glass", Color(0xFF3B82F6))
                        LegendItem("${wasteStats.paperPercent}%", "Paper", Color(0xFFFA8C33))
                    }
                }

                // Circular Progress with real data
                CircularProgress(wasteStats)
            }
        }
    }
}

@Composable
fun LegendItem(percentage: String, label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(
            text = "$percentage $label",
            fontSize = 10.sp,
            color = Color(0xFF374151)
        )
    }
}

@Composable
fun CircularProgress(wasteStats: WasteStats) {
    Canvas(modifier = Modifier.size(120.dp)) {
        val strokeWidth = 10f

        // Background circles
        drawArc(
            color = Color(0xFFE5E7EB),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size = Size(size.width - strokeWidth, size.height - strokeWidth)
        )

        drawArc(
            color = Color(0xFFE5E7EB),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2 + 24, strokeWidth / 2 + 24),
            size = Size(size.width - strokeWidth - 48, size.height - strokeWidth - 48)
        )

        drawArc(
            color = Color(0xFFE5E7EB),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2 + 48, strokeWidth / 2 + 48),
            size = Size(size.width - strokeWidth - 96, size.height - strokeWidth - 96)
        )

        // Progress arcs with real data
        drawArc(
            color = Color(0xFF22C55E),
            startAngle = -90f,
            sweepAngle = (wasteStats.plasticPercent * 3.6f),
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size = Size(size.width - strokeWidth, size.height - strokeWidth)
        )

        drawArc(
            color = Color(0xFF3B82F6),
            startAngle = -90f,
            sweepAngle = (wasteStats.glassPercent * 3.6f),
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2 + 24, strokeWidth / 2 + 24),
            size = Size(size.width - strokeWidth - 48, size.height - strokeWidth - 48)
        )

        drawArc(
            color = Color(0xFFFA8C33),
            startAngle = -90f,
            sweepAngle = (wasteStats.paperPercent * 3.6f),
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2 + 48, strokeWidth / 2 + 48),
            size = Size(size.width - strokeWidth - 96, size.height - strokeWidth - 96)
        )
    }
}

@Composable
fun HistoryItemCard(item: HistoryItem) {
    val binType = WasteCategorizer.getBinType(item.wasteLabel)
    val netImpact = item.carbonSaved - item.carbonEmitted
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(binType.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = binType.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F1F1F)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = item.date,
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                        Text(
                            text = item.weight,
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "♻️ ${String.format("%.3f", netImpact)} kg CO2e saved",
                        fontSize = 11.sp,
                        color = Color(0xFF22C55E),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF86EFAC)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Camera",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color(0xFF1F2937),
        modifier = Modifier.height(70.dp)
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF9CA3AF),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF9CA3AF),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF9CA3AF),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Close, contentDescription = "Link") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color(0xFF9CA3AF),
                indicatorColor = Color.Transparent
            )
        )
    }
}