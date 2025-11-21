package com.example.rechypher_ai_app.utils

import androidx.compose.ui.graphics.Color

enum class BinType(val displayName: String, val color: Color) {
    RECYCLABLE("Recyclable", Color(0xFF4CAF50)),      // Green
    HAZARDOUS("Hazardous", Color(0xFFF44336)),        // Red
    ORGANIC("Organic", Color(0xFF8BC34A)),            // Light Green
    GENERAL_WASTE("General Waste", Color(0xFF757575)) // Gray
}

object WasteCategorizer {
    private val categoryMap = mapOf(
        // Recyclable
        "brown-glass" to BinType.RECYCLABLE,
        "green-glass" to BinType.RECYCLABLE,
        "white-glass" to BinType.RECYCLABLE,
        "paper" to BinType.RECYCLABLE,
        "cardboard" to BinType.RECYCLABLE,
        "plastic" to BinType.RECYCLABLE,
        "metal" to BinType.RECYCLABLE,
        "clothes" to BinType.RECYCLABLE,
        "shoes" to BinType.RECYCLABLE,
        
        // Hazardous
        "battery" to BinType.HAZARDOUS,
        
        // Organic
        "biological" to BinType.ORGANIC,
        
        // General Waste
        "trash" to BinType.GENERAL_WASTE
    )
    
    fun getBinType(wasteLabel: String): BinType {
        return categoryMap[wasteLabel.lowercase().trim()] ?: BinType.GENERAL_WASTE
    }
    
    fun getBinTypeWithEmoji(wasteLabel: String): String {
        return when (getBinType(wasteLabel)) {
            BinType.RECYCLABLE -> "🟢 ${BinType.RECYCLABLE.displayName}"
            BinType.HAZARDOUS -> "🔴 ${BinType.HAZARDOUS.displayName}"
            BinType.ORGANIC -> "🌱 ${BinType.ORGANIC.displayName}"
            BinType.GENERAL_WASTE -> "⚫ ${BinType.GENERAL_WASTE.displayName}"
        }
    }
}
