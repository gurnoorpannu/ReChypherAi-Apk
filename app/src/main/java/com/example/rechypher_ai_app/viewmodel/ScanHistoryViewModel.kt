package com.example.rechypher_ai_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechypher_ai_app.data.ScanHistoryRepository
import com.example.rechypher_ai_app.utils.CarbonCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class ScanHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val wasteLabel: String,
    val title: String,
    val date: String,
    val weight: String,
    val carbonSaved: Double,
    val carbonEmitted: Double,
    val timestamp: Long = System.currentTimeMillis()
)

class ScanHistoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = ScanHistoryRepository(application.applicationContext)
    
    private val _scanHistory = MutableStateFlow<List<ScanHistoryItem>>(emptyList())
    val scanHistory: StateFlow<List<ScanHistoryItem>> = _scanHistory.asStateFlow()
    
    init {
        // Load saved history on initialization
        viewModelScope.launch {
            repository.scanHistory.collect { history ->
                _scanHistory.value = history
            }
        }
    }
    
    /**
     * Add a new scan to the history
     */
    fun addScan(wasteLabel: String, title: String? = null) {
        viewModelScope.launch {
            val (carbonSaved, carbonEmitted) = CarbonCalculator.calculateCarbon(wasteLabel)
            val carbonData = CarbonCalculator.carbonTable[wasteLabel]
            val weight = carbonData?.avgWeight ?: 0.0
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = dateFormat.format(Date())
            
            val scanTitle = title ?: generateTitle(wasteLabel)
            
            val newItem = ScanHistoryItem(
                wasteLabel = wasteLabel,
                title = scanTitle,
                date = date,
                weight = String.format("%.2f kg", weight),
                carbonSaved = carbonSaved,
                carbonEmitted = carbonEmitted
            )
            
            // Save to repository (which will update the flow)
            repository.addScan(newItem)
        }
    }
    
    /**
     * Generate a title based on waste label
     */
    private fun generateTitle(wasteLabel: String): String {
        return when (wasteLabel) {
            "plastic" -> "Plastic bottle recycled"
            "paper" -> "Paper waste recycled"
            "cardboard" -> "Cardboard box recycled"
            "metal" -> "Metal can recycled"
            "biological" -> "Food waste composted"
            "battery" -> "Battery properly disposed"
            "brown-glass", "green-glass", "white-glass" -> "Glass recycled"
            "clothes" -> "Clothes recycled"
            "shoes" -> "Shoes recycled"
            "trash" -> "Trash disposed"
            else -> "${wasteLabel.capitalize()} recycled"
        }
    }
    
    /**
     * Remove a scan from history
     */
    fun removeScan(scanId: String) {
        viewModelScope.launch {
            repository.removeScan(scanId)
        }
    }
    
    /**
     * Clear all history
     */
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
    
    /**
     * Get total carbon saved from all scans
     */
    fun getTotalCarbonSaved(): Double {
        return _scanHistory.value.sumOf { it.carbonSaved - it.carbonEmitted }
    }
    
    /**
     * Calculate waste type percentages - Top 3 categories + Others
     */
    fun getWasteStats(): WasteStats {
        val items = _scanHistory.value
        val total = items.size
        if (total == 0) return WasteStats(emptyList())
        
        // Count each waste type
        val wasteCounts = items.groupingBy { it.wasteLabel }.eachCount()
        
        // Sort by count descending
        val sortedCounts = wasteCounts.entries.sortedByDescending { it.value }
        
        // Take top 3
        val topThree = sortedCounts.take(3).map { (label, count) ->
            WasteCategory(
                label = formatLabel(label),
                percent = ((count.toFloat() / total) * 100).toInt(),
                color = getCategoryColor(label)
            )
        }
        
        // Calculate "Others" - sum of all items beyond top 3
        val topThreeActualCount = sortedCounts.take(3).sumOf { it.value }
        val othersCount = total - topThreeActualCount
        val othersPercent = if (othersCount > 0) {
            ((othersCount.toFloat() / total) * 100).toInt()
        } else {
            0
        }
        
        val categories = topThree.toMutableList()
        if (othersPercent > 0) {
            categories.add(
                WasteCategory(
                    label = "Others",
                    percent = othersPercent,
                    color = android.graphics.Color.parseColor("#9CA3AF") // Gray color
                )
            )
        }
        
        return WasteStats(categories)
    }
    
    private fun formatLabel(label: String): String {
        return when (label) {
            "plastic" -> "Plastic"
            "paper", "cardboard" -> "Paper"
            "brown-glass", "green-glass", "white-glass" -> "Glass"
            "metal" -> "Metal"
            "biological" -> "Organic"
            "battery" -> "Battery"
            "clothes" -> "Clothes"
            "shoes" -> "Shoes"
            "trash" -> "Trash"
            else -> label.capitalize()
        }
    }
    
    private fun getCategoryColor(label: String): Int {
        return when (label) {
            "plastic" -> android.graphics.Color.parseColor("#22C55E") // Green
            "paper", "cardboard" -> android.graphics.Color.parseColor("#FA8C33") // Orange
            "brown-glass", "green-glass", "white-glass" -> android.graphics.Color.parseColor("#3B82F6") // Blue
            "metal" -> android.graphics.Color.parseColor("#EAB308") // Yellow
            "biological" -> android.graphics.Color.parseColor("#10B981") // Emerald
            "battery" -> android.graphics.Color.parseColor("#EF4444") // Red
            "clothes" -> android.graphics.Color.parseColor("#8B5CF6") // Purple
            "shoes" -> android.graphics.Color.parseColor("#EC4899") // Pink
            else -> android.graphics.Color.parseColor("#6B7280") // Gray
        }
    }
}

data class WasteCategory(
    val label: String,
    val percent: Int,
    val color: Int
)

data class WasteStats(
    val categories: List<WasteCategory>
)
