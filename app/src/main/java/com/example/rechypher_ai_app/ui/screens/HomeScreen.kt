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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rechypher_ai_app.utils.CarbonCalculator
import com.example.rechypher_ai_app.utils.WasteCategorizer
import com.example.rechypher_ai_app.viewmodel.ScanHistoryViewModel
import com.example.rechypher_ai_app.viewmodel.ScanHistoryItem
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    scanHistoryViewModel: ScanHistoryViewModel
) {
    // Observe scan history from ViewModel
    val scanHistory by scanHistoryViewModel.scanHistory.collectAsState()
    
    // Calculate total carbon saved
    val totalCarbonSaved = remember(scanHistory) {
        scanHistoryViewModel.getTotalCarbonSaved()
    }
    
    // Calculate waste type percentages
    val wasteStats = remember(scanHistory) {
        scanHistoryViewModel.getWasteStats()
    }

    Scaffold { paddingValues ->
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

                if (scanHistory.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF9CA3AF)
                            )
                            Text(
                                text = "No scans yet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "Start scanning waste to see your history",
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(scanHistory) { item ->
                            HistoryItemCard(
                                item = item,
                                onDelete = { scanHistoryViewModel.removeScan(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun HeaderCard(
    totalCarbonSaved: Double,
    wasteStats: com.example.rechypher_ai_app.viewmodel.WasteStats
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

                    // Legend with real percentages - Top 3 + Others
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categories = wasteStats.categories
                        if (categories.size >= 2) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                LegendItem(
                                    "${categories[0].percent}%",
                                    categories[0].label,
                                    Color(categories[0].color)
                                )
                                LegendItem(
                                    "${categories[1].percent}%",
                                    categories[1].label,
                                    Color(categories[1].color)
                                )
                            }
                        }
                        if (categories.size >= 3) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                LegendItem(
                                    "${categories[2].percent}%",
                                    categories[2].label,
                                    Color(categories[2].color)
                                )
                                if (categories.size >= 4) {
                                    LegendItem(
                                        "${categories[3].percent}%",
                                        categories[3].label,
                                        Color(categories[3].color)
                                    )
                                }
                            }
                        }
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
fun CircularProgress(wasteStats: com.example.rechypher_ai_app.viewmodel.WasteStats) {
    Canvas(modifier = Modifier.size(120.dp)) {
        val strokeWidth = 10f
        val categories = wasteStats.categories
        val ringOffsets = listOf(0f, 24f, 48f, 72f)

        // Draw background circles for each category
        categories.take(4).forEachIndexed { index, _ ->
            val offset = ringOffsets[index]
            drawArc(
                color = Color(0xFFE5E7EB),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2 + offset, strokeWidth / 2 + offset),
                size = Size(size.width - strokeWidth - (offset * 2), size.height - strokeWidth - (offset * 2))
            )
        }

        // Draw progress arcs with real data
        categories.take(4).forEachIndexed { index, category ->
            val offset = ringOffsets[index]
            drawArc(
                color = Color(category.color),
                startAngle = -90f,
                sweepAngle = (category.percent * 3.6f),
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2 + offset, strokeWidth / 2 + offset),
                size = Size(size.width - strokeWidth - (offset * 2), size.height - strokeWidth - (offset * 2))
            )
        }
    }
}

@Composable
fun HistoryItemCard(
    item: ScanHistoryItem,
    onDelete: () -> Unit = {}
) {
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

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

