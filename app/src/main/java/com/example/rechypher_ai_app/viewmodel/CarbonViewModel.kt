package com.example.rechypher_ai_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechypher_ai_app.utils.CarbonCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing carbon impact calculations
 * Provides centralized state management for carbon data across the app
 */
class CarbonViewModel : ViewModel() {
    
    // Current waste label being analyzed
    private val _currentLabel = MutableStateFlow<String?>(null)
    val currentLabel: StateFlow<String?> = _currentLabel.asStateFlow()
    
    // Carbon saved (kg CO2e)
    private val _carbonSaved = MutableStateFlow(0.0)
    val carbonSaved: StateFlow<Double> = _carbonSaved.asStateFlow()
    
    // Carbon emitted (kg CO2e)
    private val _carbonEmitted = MutableStateFlow(0.0)
    val carbonEmitted: StateFlow<Double> = _carbonEmitted.asStateFlow()
    
    // Net carbon impact (saved - emitted)
    private val _netImpact = MutableStateFlow(0.0)
    val netImpact: StateFlow<Double> = _netImpact.asStateFlow()
    
    // Total accumulated carbon saved across all scans
    private val _totalCarbonSaved = MutableStateFlow(0.0)
    val totalCarbonSaved: StateFlow<Double> = _totalCarbonSaved.asStateFlow()
    
    // Total accumulated carbon emitted across all scans
    private val _totalCarbonEmitted = MutableStateFlow(0.0)
    val totalCarbonEmitted: StateFlow<Double> = _totalCarbonEmitted.asStateFlow()
    
    // Number of items scanned
    private val _itemsScanned = MutableStateFlow(0)
    val itemsScanned: StateFlow<Int> = _itemsScanned.asStateFlow()
    
    /**
     * Calculate carbon impact for a given waste label
     */
    fun calculateCarbonImpact(label: String) {
        viewModelScope.launch {
            _currentLabel.value = label
            
            val (saved, emitted) = CarbonCalculator.calculateCarbon(label)
            _carbonSaved.value = saved
            _carbonEmitted.value = emitted
            _netImpact.value = saved - emitted
        }
    }
    
    /**
     * Add current calculation to running totals
     */
    fun addToTotals() {
        viewModelScope.launch {
            _totalCarbonSaved.value += _carbonSaved.value
            _totalCarbonEmitted.value += _carbonEmitted.value
            _itemsScanned.value += 1
        }
    }
    
    /**
     * Calculate and add to totals in one operation
     */
    fun calculateAndAddToTotals(label: String) {
        calculateCarbonImpact(label)
        addToTotals()
    }
    
    /**
     * Reset current calculation
     */
    fun resetCurrent() {
        viewModelScope.launch {
            _currentLabel.value = null
            _carbonSaved.value = 0.0
            _carbonEmitted.value = 0.0
            _netImpact.value = 0.0
        }
    }
    
    /**
     * Reset all totals
     */
    fun resetTotals() {
        viewModelScope.launch {
            _totalCarbonSaved.value = 0.0
            _totalCarbonEmitted.value = 0.0
            _itemsScanned.value = 0
        }
    }
    
    /**
     * Reset everything
     */
    fun resetAll() {
        resetCurrent()
        resetTotals()
    }
    
    /**
     * Get formatted carbon impact string for current item
     */
    fun getFormattedImpact(): String {
        return CarbonCalculator.getFormattedCarbonImpact(_currentLabel.value ?: "")
    }
    
    /**
     * Get total net impact
     */
    fun getTotalNetImpact(): Double {
        return _totalCarbonSaved.value - _totalCarbonEmitted.value
    }
}
