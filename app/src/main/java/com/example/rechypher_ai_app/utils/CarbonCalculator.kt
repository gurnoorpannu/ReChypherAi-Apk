package com.example.rechypher_ai_app.utils

import com.example.rechypher_ai_app.data.CarbonData

/**
 * Carbon Calculator for Recypher App
 * Calculates carbon saved and emitted based on waste type and disposal method
 */
object CarbonCalculator {
    
    /**
     * Carbon data table mapping waste labels to their carbon impact factors
     * All weights in kg, all factors in kg CO2e per kg of waste
     */
    val carbonTable = mapOf(
        // Hazardous Waste
        "battery" to CarbonData(
            avgWeight = 0.05,      // 50g average
            hazardFactor = 0.5     // Proper disposal saves 0.5 kg CO2e per kg
        ),
        
        // Organic Waste
        "biological" to CarbonData(
            avgWeight = 0.3,       // 300g average
            compostFactor = 0.2,   // Composting saves 0.2 kg CO2e per kg
            landfillFactor = 0.5   // Landfill emits 0.5 kg CO2e per kg (methane)
        ),
        
        // Glass - Recyclable
        "brown-glass" to CarbonData(
            avgWeight = 0.4,       // 400g average
            recycleFactor = 0.31,  // Recycling saves 0.31 kg CO2e per kg
            landfillFactor = 0.02  // Minimal emissions in landfill
        ),
        "green-glass" to CarbonData(
            avgWeight = 0.4,
            recycleFactor = 0.31,
            landfillFactor = 0.02
        ),
        "white-glass" to CarbonData(
            avgWeight = 0.4,
            recycleFactor = 0.31,
            landfillFactor = 0.02
        ),
        
        // Paper Products - Recyclable
        "cardboard" to CarbonData(
            avgWeight = 0.2,       // 200g average
            recycleFactor = 3.3,   // Recycling saves 3.3 kg CO2e per kg
            landfillFactor = 1.5   // Decomposition emits 1.5 kg CO2e per kg
        ),
        "paper" to CarbonData(
            avgWeight = 0.1,       // 100g average
            recycleFactor = 2.5,   // Recycling saves 2.5 kg CO2e per kg
            landfillFactor = 1.3   // Decomposition emits 1.3 kg CO2e per kg
        ),
        
        // Textiles - Recyclable
        "clothes" to CarbonData(
            avgWeight = 0.5,       // 500g average
            recycleFactor = 3.6,   // Recycling/reuse saves 3.6 kg CO2e per kg
            landfillFactor = 0.6   // Landfill emits 0.6 kg CO2e per kg
        ),
        "shoes" to CarbonData(
            avgWeight = 0.6,       // 600g average
            recycleFactor = 2.5,   // Recycling/reuse saves 2.5 kg CO2e per kg
            landfillFactor = 0.5   // Landfill emits 0.5 kg CO2e per kg
        ),
        
        // Metals - Recyclable
        "metal" to CarbonData(
            avgWeight = 0.15,      // 150g average (aluminum can)
            recycleFactor = 9.0,   // Recycling saves 9.0 kg CO2e per kg
            landfillFactor = 0.1   // Minimal emissions in landfill
        ),
        
        // Plastics - Recyclable
        "plastic" to CarbonData(
            avgWeight = 0.05,      // 50g average (bottle)
            recycleFactor = 1.5,   // Recycling saves 1.5 kg CO2e per kg
            landfillFactor = 0.04  // Minimal emissions in landfill
        ),
        
        // General Waste
        "trash" to CarbonData(
            avgWeight = 0.5,       // 500g average
            landfillFactor = 0.7   // Landfill emits 0.7 kg CO2e per kg
        )
    )
    
    /**
     * Calculate carbon impact for a given waste label
     * 
     * @param label The waste item label (e.g., "battery", "plastic", "biological")
     * @return Pair<Double, Double> where first is carbonSaved (kg CO2e) and second is carbonEmitted (kg CO2e)
     */
    fun calculateCarbon(label: String): Pair<Double, Double> {
        val normalizedLabel = label.lowercase().trim()
        val carbonData = carbonTable[normalizedLabel]
        
        // If label not found, return zero impact
        if (carbonData == null) {
            return Pair(0.0, 0.0)
        }
        
        return when (normalizedLabel) {
            // Hazardous waste (battery)
            "battery" -> {
                val carbonSaved = carbonData.avgWeight * carbonData.hazardFactor
                val carbonEmitted = 0.0
                Pair(carbonSaved, carbonEmitted)
            }
            
            // Organic waste (biological)
            "biological" -> {
                val carbonSaved = carbonData.avgWeight * carbonData.compostFactor
                val carbonEmitted = carbonData.avgWeight * carbonData.landfillFactor
                Pair(carbonSaved, carbonEmitted)
            }
            
            // General waste (trash)
            "trash" -> {
                val carbonSaved = 0.0
                val carbonEmitted = carbonData.avgWeight * carbonData.landfillFactor
                Pair(carbonSaved, carbonEmitted)
            }
            
            // Recyclable items (default case)
            else -> {
                val carbonSaved = carbonData.avgWeight * carbonData.recycleFactor
                val carbonEmitted = carbonData.avgWeight * carbonData.landfillFactor
                Pair(carbonSaved, carbonEmitted)
            }
        }
    }
    
    /**
     * Get formatted carbon impact string
     * 
     * @param label The waste item label
     * @return String with formatted carbon impact information
     */
    fun getFormattedCarbonImpact(label: String): String {
        val (saved, emitted) = calculateCarbon(label)
        return buildString {
            append("♻️ Carbon Saved: ${String.format("%.3f", saved)} kg CO2e\n")
            append("🏭 Carbon Emitted: ${String.format("%.3f", emitted)} kg CO2e")
        }
    }
    
    /**
     * Get net carbon impact (saved - emitted)
     * Positive value means net benefit, negative means net cost
     * 
     * @param label The waste item label
     * @return Double representing net carbon impact in kg CO2e
     */
    fun getNetCarbonImpact(label: String): Double {
        val (saved, emitted) = calculateCarbon(label)
        return saved - emitted
    }
}
