package com.example.rechypher_ai_app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechypher_ai_app.data.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {
    private val apiService = RecypherApiService.create()
    
    val messages = mutableStateListOf<ChatMessage>()
    var isLoading = false
        private set
    
    private val wasteKeywords = listOf(
        "waste", "trash", "garbage", "recycle", "recycling", "compost", "composting",
        "disposal", "dispose", "bin", "landfill", "plastic", "paper", "glass", "metal",
        "organic", "biodegradable", "hazardous", "e-waste", "electronic waste",
        "segregation", "segregate", "sorting", "sort", "reuse", "reduce", "pollution",
        "environment", "eco", "green", "sustainable", "sustainability"
    )
    
    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return
        
        // Add user message
        messages.add(ChatMessage(userMessage, true))
        
        // Check if query is waste-related
        if (!isWasteRelated(userMessage)) {
            messages.add(
                ChatMessage(
                    "I'm a waste management assistant. I can only help with questions related to waste, recycling, trash disposal, and environmental sustainability. Please ask me something about waste management!",
                    false
                )
            )
            return
        }
        
        isLoading = true
        
        viewModelScope.launch {
            try {
                // Call the Render backend API for waste classification
                val request = ClassifyWasteRequest(prompt = userMessage)
                val response = apiService.classifyWaste(request)
                
                // Display the result field from the backend response
                messages.add(ChatMessage(response.result, false))
                
            } catch (e: Exception) {
                e.printStackTrace()
                
                val errorMessage = when {
                    e is java.net.UnknownHostException || e is java.net.ConnectException -> {
                        "🌐 No internet connection. Please check your network and try again."
                    }
                    e is java.net.SocketTimeoutException -> {
                        "⏱️ Request timed out. The server is taking too long to respond. Please try again."
                    }
                    e.message?.contains("HTTP 500", ignoreCase = true) == true -> {
                        "🔧 Server error. Our AI service is temporarily unavailable. Please try again in a moment."
                    }
                    e.message?.contains("HTTP 429", ignoreCase = true) == true -> {
                        "⚠️ Too many requests. Please wait a moment before trying again."
                    }
                    e.message?.contains("HTTP 404", ignoreCase = true) == true -> {
                        "❌ Service not found. Please contact support if this persists."
                    }
                    else -> {
                        "❌ Sorry, something went wrong: ${e.message ?: "Unknown error"}. Please try again."
                    }
                }
                
                messages.add(ChatMessage(errorMessage, false))
            } finally {
                isLoading = false
            }
        }
    }
    
    private fun isWasteRelated(query: String): Boolean {
        val lowerQuery = query.lowercase()
        return wasteKeywords.any { keyword -> lowerQuery.contains(keyword) }
    }
    
    fun clearChat() {
        messages.clear()
    }
}
