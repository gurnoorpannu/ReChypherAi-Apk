package com.example.rechypher_ai_app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rechypher_ai_app.data.GeminiApiException
import com.example.rechypher_ai_app.data.GeminiApiService
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {
    private val geminiService = GeminiApiService.getInstance()
    
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
                // Call Gemini API directly
                val response = geminiService.generateContent(userMessage)
                messages.add(ChatMessage(response, false))
                
            } catch (e: GeminiApiException) {
                val errorMessage = when (e) {
                    is GeminiApiException.ApiKeyMissing -> {
                        "🔐 API key not configured. Please contact support."
                    }
                    is GeminiApiException.NetworkError -> {
                        "🌐 ${e.message}"
                    }
                    is GeminiApiException.TimeoutError -> {
                        "⏱️ Request timed out. The AI service is taking too long. Please try again."
                    }
                    is GeminiApiException.RateLimitExceeded -> {
                        "⚠️ Too many requests. Please wait a moment before trying again."
                    }
                    is GeminiApiException.InvalidRequest -> {
                        "❌ Invalid request: ${e.message}. Please try rephrasing your question."
                    }
                    is GeminiApiException.ServerError -> {
                        "🔧 ${e.message}. Our AI service is temporarily unavailable. Please try again in a moment."
                    }
                    is GeminiApiException.UnknownError -> {
                        "❌ Sorry, something went wrong: ${e.message}. Please try again."
                    }
                }
                
                messages.add(ChatMessage(errorMessage, false))
            } catch (e: Exception) {
                // Handle any unexpected exceptions
                e.printStackTrace()
                val errorMessage = when {
                    e is UnknownHostException || e is ConnectException -> {
                        "🌐 No internet connection. Please check your network and try again."
                    }
                    e is SocketTimeoutException -> {
                        "⏱️ Request timed out. Please try again."
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
