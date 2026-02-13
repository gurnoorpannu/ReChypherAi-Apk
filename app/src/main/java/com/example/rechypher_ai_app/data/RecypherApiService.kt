package com.example.rechypher_ai_app.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Data class matching MongoDB schema
data class RecycleCenter(
    val name: String,
    val address: String,
    val acceptedMaterials: List<String>,
    val location: LocationData
)

data class LocationData(
    val coordinates: List<Double> // [longitude, latitude] - index 0 is Longitude, index 1 is Latitude
)

// Response wrapper for nearest centers
data class CentersResponse(
    val count: Int,
    val centers: List<RecycleCenter>
)

// Request/Response for waste classification
data class ClassifyWasteRequest(
    val prompt: String
)

data class ClassifyWasteResponse(
    val result: String, // Backend sends AI text under 'result' key
    val category: String? = null,
    val confidence: Double? = null
)

// Retrofit API interface
interface RecypherApiService {
    
    @GET("api/centers/nearest")
    suspend fun getNearestCenters(
        @Query("lat") latitude: Double,
        @Query("long") longitude: Double,
        @Query("limit") limit: Int = 10
    ): CentersResponse
    
    @GET("api/centers/all")
    suspend fun getAllCenters(): CentersResponse
    
    @POST("api/ai/classify")  // Fixed endpoint path
    suspend fun classifyWaste(
        @Body request: ClassifyWasteRequest
    ): ClassifyWasteResponse
    
    companion object {
        private const val BASE_URL = "https://recypherai-backend.onrender.com/"
        private const val TAG = "RecypherApiService"
        
        fun create(): RecypherApiService {
            // Add logging interceptor
            val loggingInterceptor = okhttp3.logging.HttpLoggingInterceptor { message ->
                android.util.Log.d(TAG, message)
            }.apply {
                level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
            }
            
            val client = okhttp3.OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
            
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            
            android.util.Log.d(TAG, "Retrofit client created with base URL: $BASE_URL")
            
            return retrofit.create(RecypherApiService::class.java)
        }
    }
}
