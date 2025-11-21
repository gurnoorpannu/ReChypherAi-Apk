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
     * Calculate waste type percentages
     */
    fun getWasteStats(): WasteStats {
        val items = _scanHistory.value
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
}

data class WasteStats(
    val plasticPercent: Int,
    val paperPercent: Int,
    val glassPercent: Int
)
