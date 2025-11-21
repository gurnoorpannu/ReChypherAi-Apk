package com.example.rechypher_ai_app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.rechypher_ai_app.viewmodel.ScanHistoryItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scan_history")

class ScanHistoryRepository(private val context: Context) {
    
    private val gson = Gson()
    
    companion object {
        private val SCAN_HISTORY_KEY = stringPreferencesKey("scan_history")
    }
    
    /**
     * Get scan history as a Flow
     */
    val scanHistory: Flow<List<ScanHistoryItem>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[SCAN_HISTORY_KEY] ?: "[]"
            try {
                val type = object : TypeToken<List<ScanHistoryItem>>() {}.type
                gson.fromJson<List<ScanHistoryItem>>(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    
    /**
     * Save scan history
     */
    suspend fun saveScanHistory(history: List<ScanHistoryItem>) {
        context.dataStore.edit { preferences ->
            val json = gson.toJson(history)
            preferences[SCAN_HISTORY_KEY] = json
        }
    }
    
    /**
     * Add a single scan to history
     */
    suspend fun addScan(scan: ScanHistoryItem) {
        context.dataStore.edit { preferences ->
            val json = preferences[SCAN_HISTORY_KEY] ?: "[]"
            val type = object : TypeToken<List<ScanHistoryItem>>() {}.type
            val currentHistory = try {
                gson.fromJson<List<ScanHistoryItem>>(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            
            val updatedHistory = listOf(scan) + currentHistory
            preferences[SCAN_HISTORY_KEY] = gson.toJson(updatedHistory)
        }
    }
    
    /**
     * Remove a scan from history
     */
    suspend fun removeScan(scanId: String) {
        context.dataStore.edit { preferences ->
            val json = preferences[SCAN_HISTORY_KEY] ?: "[]"
            val type = object : TypeToken<List<ScanHistoryItem>>() {}.type
            val currentHistory = try {
                gson.fromJson<List<ScanHistoryItem>>(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            
            val updatedHistory = currentHistory.filter { it.id != scanId }
            preferences[SCAN_HISTORY_KEY] = gson.toJson(updatedHistory)
        }
    }
    
    /**
     * Clear all history
     */
    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences[SCAN_HISTORY_KEY] = "[]"
        }
    }
}
