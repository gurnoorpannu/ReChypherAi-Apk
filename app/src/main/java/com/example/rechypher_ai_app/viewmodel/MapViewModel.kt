package com.example.rechypher_ai_app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechypher_ai_app.data.RecycleCenter
import com.example.rechypher_ai_app.data.RecypherApiService
import com.example.rechypher_ai_app.data.GeminiApiService
import com.example.rechypher_ai_app.data.GeminiApiException
import com.example.rechypher_ai_app.data.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

data class MapUiState(
    val centers: List<RecycleCenter> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val errorType: ErrorType? = null,
    val showTimeoutDialog: Boolean = false
)

enum class ErrorType {
    NETWORK_ERROR,
    SERVER_ERROR,
    NO_CENTERS_FOUND,
    TIMEOUT_ERROR,
    UNKNOWN_ERROR
}

class MapViewModel : ViewModel() {
    
    private val apiService = RecypherApiService.create()
    private val geminiService = GeminiApiService.getInstance()
    
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
    
    companion object {
        private const val TAG = "MapViewModel"
    }
    
    fun loadNearestCenters(latitude: Double, longitude: Double, limit: Int = 10) {
        Log.d(TAG, "loadNearestCenters called with lat=$latitude, long=$longitude, limit=$limit")
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, errorType = null)
            Log.d(TAG, "Loading state set to true")
            
            try {
                Log.d(TAG, "Making API call to getNearestCenters...")
                val response = apiService.getNearestCenters(latitude, longitude, limit)
                
                Log.d(TAG, "API call successful! Response count: ${response.count}, centers received: ${response.centers.size}")
                
                // Check if count is 0 - helps debug empty database or small radius issues
                if (response.count == 0) {
                    Log.w(TAG, "WARNING: API returned count=0. Possible causes:")
                    Log.w(TAG, "  - Database is empty (no centers in the collection)")
                    Log.w(TAG, "  - Search radius is too small for location (lat=$latitude, long=$longitude)")
                    Log.w(TAG, "  - No centers exist within the specified limit ($limit)")
                    
                    _uiState.value = _uiState.value.copy(
                        centers = emptyList(),
                        isLoading = false,
                        error = "No recycling centers found nearby. Showing demo locations.",
                        errorType = ErrorType.NO_CENTERS_FOUND
                    )
                    loadDemoCenters()
                    return@launch
                }
                
                // Extract centers list from response
                val centersList = response.centers
                
                Log.d(TAG, "Processing ${centersList.size} centers from response")
                centersList.forEachIndexed { index, center ->
                    Log.d(TAG, "Center $index: ${center.name} at ${center.location.coordinates}")
                    Log.d(TAG, "  Address: ${center.address}")
                    Log.d(TAG, "  Accepted materials: ${center.acceptedMaterials.joinToString(", ")}")
                }
                
                // Update UI state with the extracted centers list
                _uiState.value = _uiState.value.copy(
                    centers = centersList,
                    isLoading = false,
                    error = null,
                    errorType = null
                )
                Log.d(TAG, "UI state updated successfully with ${centersList.size} centers")
                
            } catch (e: Exception) {
                Log.e(TAG, "API call failed with exception: ${e.javaClass.simpleName}")
                Log.e(TAG, "Error message: ${e.message}")
                Log.e(TAG, "Stack trace:", e)
                
                val (errorMessage, errorType) = categorizeError(e)
                
                // Show dialog for timeout and network errors, auto-fallback for others
                val shouldShowDialog = errorType == ErrorType.TIMEOUT_ERROR || 
                                      errorType == ErrorType.NETWORK_ERROR ||
                                      errorType == ErrorType.SERVER_ERROR
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMessage,
                    errorType = errorType,
                    showTimeoutDialog = shouldShowDialog
                )
                
                // Only auto-fallback for non-critical errors
                if (!shouldShowDialog) {
                    Log.d(TAG, "Auto-falling back to demo data for non-critical error")
                    loadDemoCenters()
                }
            }
        }
    }
    
    private fun categorizeError(exception: Exception): Pair<String, ErrorType> {
        return when {
            exception is UnknownHostException ||
            exception is ConnectException -> {
                Pair(
                    "No internet connection. Showing demo locations.",
                    ErrorType.NETWORK_ERROR
                )
            }
            exception is SocketTimeoutException -> {
                Pair(
                    "Request timed out. Server might be slow. Showing demo locations.",
                    ErrorType.TIMEOUT_ERROR
                )
            }
            exception.message?.contains("HTTP", ignoreCase = true) == true -> {
                Pair(
                    "Server error. Please try again later. Showing demo locations.",
                    ErrorType.SERVER_ERROR
                )
            }
            else -> {
                Pair(
                    "Unable to load centers. Showing demo locations.",
                    ErrorType.UNKNOWN_ERROR
                )
            }
        }
    }
    
    fun dismissTimeoutDialog() {
        _uiState.value = _uiState.value.copy(showTimeoutDialog = false)
    }
    
    fun useDemoCenters() {
        Log.d(TAG, "User chose to use demo centers")
        dismissTimeoutDialog()
        loadDemoCenters()
    }
    
    fun retryLoadCenters(latitude: Double, longitude: Double, limit: Int = 10) {
        Log.d(TAG, "User chose to retry loading centers")
        dismissTimeoutDialog()
        loadNearestCenters(latitude, longitude, limit)
    }
    
    /**
     * Classifies waste using Gemini API directly
     * Returns the classification result or null on error
     */
    suspend fun classifyWaste(prompt: String): String? {
        Log.d(TAG, "classifyWaste called with prompt: $prompt")
        return try {
            // Use Gemini API directly instead of backend
            val systemPrompt = """You are a waste classification expert. Analyze the user's input and classify it into one of these categories:
            - Recyclable (plastic, paper, metal, glass)
            - Organic/Food Waste (food scraps, yard waste)
            - Hazardous (batteries, chemicals, electronics)
            - General Waste (non-recyclable items)
            
            Respond with just the category name and a brief 1-2 sentence explanation.""".trimIndent()
            
            val response = geminiService.generateContent(prompt, systemPrompt)
            Log.d(TAG, "Waste classification successful: $response")
            response
        } catch (e: GeminiApiException) {
            Log.e(TAG, "Waste classification failed: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Waste classification failed: ${e.message}", e)
            null
        }
    }
    
    private fun loadDemoCenters() {
        Log.d(TAG, "Loading demo centers as fallback")
        
        // Fallback demo data in case API is unavailable
        val demoCenters = listOf(
            RecycleCenter(
                name = "Patiala Waste Disposal Center",
                address = "Patiala, Punjab, India",
                acceptedMaterials = listOf("Plastic", "Paper", "Metal", "Glass"),
                location = LocationData(
                    coordinates = listOf(76.3737, 30.3522)
                )
            ),
            RecycleCenter(
                name = "Green Recycling Hub Patiala",
                address = "Patiala, Punjab, India",
                acceptedMaterials = listOf("E-waste", "Plastic", "Paper"),
                location = LocationData(
                    coordinates = listOf(76.3850, 30.3400)
                )
            ),
            RecycleCenter(
                name = "Eco Waste Management",
                address = "Patiala, Punjab, India",
                acceptedMaterials = listOf("Organic", "Plastic", "Paper"),
                location = LocationData(
                    coordinates = listOf(76.3600, 30.3650)
                )
            ),
            RecycleCenter(
                name = "Clean Punjab Initiative",
                address = "Patiala, Punjab, India",
                acceptedMaterials = listOf("Metal", "Glass", "Plastic"),
                location = LocationData(
                    coordinates = listOf(76.3900, 30.3300)
                )
            ),
            RecycleCenter(
                name = "Sustainable Waste Center",
                address = "Patiala, Punjab, India",
                acceptedMaterials = listOf("Hazardous", "E-waste", "Battery"),
                location = LocationData(
                    coordinates = listOf(76.3500, 30.3700)
                )
            )
        )
        
        Log.d(TAG, "Demo centers loaded: ${demoCenters.size} centers")
        _uiState.value = _uiState.value.copy(centers = demoCenters)
    }
}
