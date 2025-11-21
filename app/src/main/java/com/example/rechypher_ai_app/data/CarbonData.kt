package com.example.rechypher_ai_app.data

/**
 * Data class representing carbon impact factors for waste items
 * 
 * @param avgWeight Average weight of the item in kilograms
 * @param recycleFactor Carbon saved per kg when recycled
 * @param landfillFactor Carbon emitted per kg when sent to landfill
 * @param compostFactor Carbon saved per kg when composted (for organic waste)
 * @param hazardFactor Carbon saved per kg when properly disposed (for hazardous waste)
 */
data class CarbonData(
    val avgWeight: Double,
    val recycleFactor: Double = 0.0,
    val landfillFactor: Double = 0.0,
    val compostFactor: Double = 0.0,
    val hazardFactor: Double = 0.0
)
