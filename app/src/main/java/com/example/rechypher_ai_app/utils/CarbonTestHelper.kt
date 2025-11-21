package com.example.rechypher_ai_app.utils

import android.util.Log

/**
 * Helper object for testing carbon calculations
 * Use this to verify all calculations are working correctly
 */
object CarbonTestHelper {
    
    private const val TAG = "CarbonTest"
    
    /**
     * Test all waste labels and log results
     */
    fun testAllLabels() {
        Log.d(TAG, "========== CARBON CALCULATION TEST ==========")
        
        val allLabels = listOf(
            "battery", "biological",
            "brown-glass", "green-glass", "white-glass",
            "cardboard", "paper",
            "clothes", "shoes",
            "metal", "plastic", "trash"
        )
        
        allLabels.forEach { label ->
            testLabel(label)
        }
        
        Log.d(TAG, "========== TEST COMPLETE ==========")
    }
    
    /**
     * Test a single label
     */
    fun testLabel(label: String) {
        val (saved, emitted) = CarbonCalculator.calculateCarbon(label)
        val net = saved - emitted
        val category = WasteCategorizer.getBinType(label).displayName
        
        Log.d(TAG, """
            Label: $label
            Category: $category
            Carbon Saved: ${String.format("%.3f", saved)} kg CO2e
            Carbon Emitted: ${String.format("%.3f", emitted)} kg CO2e
            Net Impact: ${String.format("%.3f", net)} kg CO2e
            Status: ${if (net > 0) "✅ POSITIVE" else "⚠️ NEGATIVE"}
            ---
        """.trimIndent())
    }
    
    /**
     * Test calculation accuracy against expected values
     */
    fun verifyCalculations(): Boolean {
        Log.d(TAG, "========== VERIFICATION TEST ==========")
        
        val testCases = mapOf(
            "battery" to Triple(0.025, 0.0, 0.025),
            "plastic" to Triple(0.075, 0.002, 0.073),
            "metal" to Triple(1.35, 0.015, 1.335),
            "cardboard" to Triple(0.66, 0.3, 0.36),
            "trash" to Triple(0.0, 0.35, -0.35)
        )
        
        var allPassed = true
        
        testCases.forEach { (label, expected) ->
            val (expectedSaved, expectedEmitted, expectedNet) = expected
            val (actualSaved, actualEmitted) = CarbonCalculator.calculateCarbon(label)
            val actualNet = actualSaved - actualEmitted
            
            val savedMatch = kotlin.math.abs(actualSaved - expectedSaved) < 0.001
            val emittedMatch = kotlin.math.abs(actualEmitted - expectedEmitted) < 0.001
            val netMatch = kotlin.math.abs(actualNet - expectedNet) < 0.001
            
            val passed = savedMatch && emittedMatch && netMatch
            allPassed = allPassed && passed
            
            Log.d(TAG, """
                $label: ${if (passed) "✅ PASS" else "❌ FAIL"}
                Expected: saved=$expectedSaved, emitted=$expectedEmitted, net=$expectedNet
                Actual: saved=$actualSaved, emitted=$actualEmitted, net=$actualNet
            """.trimIndent())
        }
        
        Log.d(TAG, "========== VERIFICATION ${if (allPassed) "PASSED ✅" else "FAILED ❌"} ==========")
        return allPassed
    }
    
    /**
     * Get a summary of all carbon factors
     */
    fun printCarbonTable() {
        Log.d(TAG, "========== CARBON FACTORS TABLE ==========")
        Log.d(TAG, String.format("%-15s %-10s %-10s %-10s %-10s %-10s", 
            "Label", "Weight", "Recycle", "Landfill", "Compost", "Hazard"))
        Log.d(TAG, "-".repeat(70))
        
        CarbonCalculator.carbonTable.forEach { (label, data) ->
            Log.d(TAG, String.format("%-15s %-10.3f %-10.3f %-10.3f %-10.3f %-10.3f",
                label,
                data.avgWeight,
                data.recycleFactor,
                data.landfillFactor,
                data.compostFactor,
                data.hazardFactor
            ))
        }
        
        Log.d(TAG, "========== END TABLE ==========")
    }
    
    /**
     * Compare impact of different waste types
     */
    fun compareImpacts() {
        Log.d(TAG, "========== IMPACT COMPARISON ==========")
        
        val impacts = CarbonCalculator.carbonTable.keys.map { label ->
            val net = CarbonCalculator.getNetCarbonImpact(label)
            label to net
        }.sortedByDescending { it.second }
        
        Log.d(TAG, "Ranked by Net Positive Impact (Best to Worst):")
        impacts.forEachIndexed { index, (label, net) ->
            Log.d(TAG, "${index + 1}. $label: ${String.format("%.3f", net)} kg CO2e")
        }
        
        Log.d(TAG, "========== END COMPARISON ==========")
    }
    
    /**
     * Simulate scanning multiple items
     */
    fun simulateScanSession(): Triple<Double, Double, Int> {
        Log.d(TAG, "========== SIMULATED SCAN SESSION ==========")
        
        val scannedItems = listOf(
            "plastic", "plastic", "cardboard",
            "metal", "paper", "biological",
            "battery", "clothes"
        )
        
        var totalSaved = 0.0
        var totalEmitted = 0.0
        
        scannedItems.forEach { label ->
            val (saved, emitted) = CarbonCalculator.calculateCarbon(label)
            totalSaved += saved
            totalEmitted += emitted
            Log.d(TAG, "Scanned: $label (saved: ${String.format("%.3f", saved)}, emitted: ${String.format("%.3f", emitted)})")
        }
        
        val netImpact = totalSaved - totalEmitted
        
        Log.d(TAG, """
            
            SESSION SUMMARY:
            Items Scanned: ${scannedItems.size}
            Total Carbon Saved: ${String.format("%.3f", totalSaved)} kg CO2e
            Total Carbon Emitted: ${String.format("%.3f", totalEmitted)} kg CO2e
            Net Impact: ${String.format("%.3f", netImpact)} kg CO2e
            Equivalent to: ${String.format("%.1f", netImpact / 0.12)} km of driving saved
        """.trimIndent())
        
        Log.d(TAG, "========== END SESSION ==========")
        
        return Triple(totalSaved, totalEmitted, scannedItems.size)
    }
}
