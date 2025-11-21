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
    private val apiService = GeminiApiService.create()
    private val apiKey = "AIzaSyCEpS6GrFmRZq0QzaM3mYGjIQ1RiNkB5r8"
    
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
                val systemPrompt = "You are a helpful waste management assistant. " +
                        "Only answer questions related to waste, recycling, trash disposal, " +
                        "composting, and environmental sustainability. " +
                        "Keep your responses concise and helpful."
                
                val request = GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(
                                GeminiPart(text = "$systemPrompt\n\nUser question: $userMessage")
                            )
                        )
                    )
                )
                
                val response = apiService.generateContent(apiKey, request)
                
                if (response.isSuccessful) {
                    val aiResponse = response.body()?.candidates?.firstOrNull()
                        ?.content?.parts?.firstOrNull()?.text
                        ?: "Sorry, I couldn't generate a response."
                    
                    messages.add(ChatMessage(aiResponse, false))
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    messages.add(
                        ChatMessage(
                            "API Error (${response.code()}): $errorBody",
                            false
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                messages.add(
                    ChatMessage(
                        "Sorry, I encountered an error: ${e.message ?: "Unknown error"}. Please try again.",
                        false
                    )
                )
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
