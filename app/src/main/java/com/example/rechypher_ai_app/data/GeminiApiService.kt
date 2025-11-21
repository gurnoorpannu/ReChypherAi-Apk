package com.example.rechypher_ai_app.data

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// Grok API uses OpenAI-compatible format
data class GrokRequest(
    val messages: List<GrokMessage>,
    val model: String = "grok-beta",
    val stream: Boolean = false,
    val temperature: Double = 0.7
)

data class GrokMessage(
    val role: String,
    val content: String
)

data class GrokResponse(
    val choices: List<GrokChoice>?
)

data class GrokChoice(
    val message: GrokMessage?,
    @SerializedName("finish_reason") val finishReason: String?
)

interface GeminiApiService {
    @POST("v1/chat/completions")
    suspend fun generateContent(
        @Header("Authorization") authorization: String,
        @Body request: GrokRequest
    ): Response<GrokResponse>
    
    companion object {
        private const val BASE_URL = "https://api.x.ai/"
        
        fun create(): GeminiApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GeminiApiService::class.java)
        }
    }
}
