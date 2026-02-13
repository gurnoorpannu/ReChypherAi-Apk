package com.example.rechypher_ai_app.data

import com.example.rechypher_ai_app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Custom exceptions for Gemini API errors
 */
sealed class GeminiApiException(message: String) : Exception(message) {
    class ApiKeyMissing : GeminiApiException("Gemini API key is not configured. Please add your API key to local.properties")
    class NetworkError(message: String) : GeminiApiException(message)
    class TimeoutError : GeminiApiException("Request timed out. Please try again.")
    class RateLimitExceeded : GeminiApiException("Too many requests. Please wait a moment.")
    class InvalidRequest(message: String) : GeminiApiException(message)
    class ServerError(message: String) : GeminiApiException(message)
    class UnknownError(message: String) : GeminiApiException(message)
}

/**
 * Service for direct communication with Gemini API
 * Replaces backend proxy with direct REST API calls
 */
class GeminiApiService private constructor() {

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
        private const val MODEL = "gemini-2.5-flash"
        private const val TIMEOUT_MS = 30000L // 30 seconds

        @Volatile
        private var instance: GeminiApiService? = null

        fun getInstance(): GeminiApiService {
            return instance ?: synchronized(this) {
                instance ?: GeminiApiService().also { instance = it }
            }
        }
    }

    /**
     * Generates content using Gemini API
     * @param prompt The user's prompt/question
     * @param systemPrompt Optional system prompt to set context
     * @return The generated text response
     * @throws GeminiApiException on API errors
     */
    suspend fun generateContent(
        prompt: String,
        systemPrompt: String = getDefaultSystemPrompt()
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY_HERE") {
            throw GeminiApiException.ApiKeyMissing()
        }

        withTimeout(TIMEOUT_MS) {
            val url = URL("$BASE_URL/$MODEL:generateContent?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection

            try {
                // Configure connection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 15000
                connection.readTimeout = 20000

                // Build request body
                val requestBody = buildRequestBody(prompt, systemPrompt)

                // Send request
                OutputStreamWriter(connection.outputStream).use { writer ->
                    writer.write(requestBody.toString())
                    writer.flush()
                }

                // Read response
                val responseCode = connection.responseCode
                val response = if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                        reader.readText()
                    }
                } else {
                    BufferedReader(InputStreamReader(connection.errorStream)).use { reader ->
                        reader.readText()
                    }
                }

                // Parse response
                parseResponse(response, responseCode)

            } catch (e: SocketTimeoutException) {
                throw GeminiApiException.TimeoutError()
            } catch (e: UnknownHostException) {
                throw GeminiApiException.NetworkError("No internet connection. Please check your network.")
            } finally {
                connection.disconnect()
            }
        }
    }

    /**
     * Builds the JSON request body for Gemini API
     */
    private fun buildRequestBody(prompt: String, systemPrompt: String): JSONObject {
        val parts = JSONArray().apply {
            put(JSONObject().apply {
                put("text", "$systemPrompt\n\nUser question: $prompt")
            })
        }

        val contents = JSONArray().apply {
            put(JSONObject().apply {
                put("role", "user")
                put("parts", parts)
            })
        }

        return JSONObject().apply {
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 2048)
                put("topP", 0.95)
                put("topK", 40)
            })
            put("safetySettings", JSONArray().apply {
                put(JSONObject().apply {
                    put("category", "HARM_CATEGORY_HARASSMENT")
                    put("threshold", "BLOCK_MEDIUM_AND_ABOVE")
                })
                put(JSONObject().apply {
                    put("category", "HARM_CATEGORY_HATE_SPEECH")
                    put("threshold", "BLOCK_MEDIUM_AND_ABOVE")
                })
                put(JSONObject().apply {
                    put("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT")
                    put("threshold", "BLOCK_MEDIUM_AND_ABOVE")
                })
                put(JSONObject().apply {
                    put("category", "HARM_CATEGORY_DANGEROUS_CONTENT")
                    put("threshold", "BLOCK_MEDIUM_AND_ABOVE")
                })
            })
        }
    }

    /**
     * Parses the API response and returns the generated text
     */
    private fun parseResponse(response: String, responseCode: Int): String {
        val jsonResponse = JSONObject(response)

        // Check for error in response
        if (responseCode != HttpURLConnection.HTTP_OK) {
            val error = jsonResponse.optJSONObject("error")
            if (error != null) {
                val code = error.optInt("code", responseCode)
                val message = error.optString("message", "Unknown error")
                val status = error.optString("status", "ERROR")

                throw when (code) {
                    400 -> GeminiApiException.InvalidRequest(message)
                    429 -> GeminiApiException.RateLimitExceeded()
                    500, 502, 503, 504 -> GeminiApiException.ServerError("Server error: $message")
                    else -> GeminiApiException.UnknownError("Error $code: $message")
                }
            }
        }

        // Parse successful response
        val candidates = jsonResponse.optJSONArray("candidates")
            ?: throw GeminiApiException.UnknownError("No response generated")

        if (candidates.length() == 0) {
            throw GeminiApiException.UnknownError("Empty response from API")
        }

        val firstCandidate = candidates.getJSONObject(0)
        val content = firstCandidate.optJSONObject("content")
            ?: throw GeminiApiException.UnknownError("Invalid response format")

        val parts = content.optJSONArray("parts")
            ?: throw GeminiApiException.UnknownError("Invalid response format")

        if (parts.length() == 0) {
            throw GeminiApiException.UnknownError("Empty content in response")
        }

        val text = parts.getJSONObject(0).optString("text", "")
        if (text.isBlank()) {
            throw GeminiApiException.UnknownError("Empty text in response")
        }

        return text
    }

    /**
     * Returns the default system prompt for waste management context
     */
    private fun getDefaultSystemPrompt(): String {
        return """You are a helpful and knowledgeable waste management assistant. 
        Your expertise covers:
        - Proper waste segregation (recyclable, organic, hazardous, e-waste, etc.)
        - Recycling guidelines and best practices
        - Composting methods and tips
        - Safe disposal of hazardous materials
        - Reducing waste and living sustainably
        - Understanding environmental impact of waste
        
        Provide clear, actionable advice. Be friendly and encouraging while being accurate.
        If you're unsure about specific local regulations, mention that guidelines may vary by location.
        Keep responses concise but informative.""".trimIndent()
    }
}
