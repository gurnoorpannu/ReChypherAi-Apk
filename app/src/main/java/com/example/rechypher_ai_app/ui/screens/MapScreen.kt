package com.example.rechypher_ai_app.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rechypher_ai_app.R
import com.example.rechypher_ai_app.data.RecycleCenter
import com.example.rechypher_ai_app.ui.theme.DarkGreen
import com.example.rechypher_ai_app.ui.theme.PrimaryGreen
import com.example.rechypher_ai_app.ui.theme.White
import com.example.rechypher_ai_app.utils.LocationHelper
import com.example.rechypher_ai_app.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navigateToNearest: Boolean = false,
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val scope = rememberCoroutineScope()
    
    // Collect UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    
    // Log UI state changes
    LaunchedEffect(uiState) {
        Log.d("MapScreen", "UI State updated: ${uiState.centers.size} centers, loading=${uiState.isLoading}, error=${uiState.error}")
    }
    
    // Default location - Patiala, Punjab, India
    val defaultLocation = LatLng(30.3522, 76.3737)
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember { mutableStateOf(locationHelper.hasLocationPermission()) }
    
    // Dialog state for selected disposal center
    var selectedCenter by remember { mutableStateOf<RecycleCenter?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }
    
    // Convert RecycleCenter to LatLng
    fun RecycleCenter.toLatLng(): LatLng {
        // MongoDB stores coordinates as [longitude, latitude]
        return LatLng(location.coordinates[1], location.coordinates[0])
    }
    
    // Function to find nearest disposal center
    fun findNearestCenter(currentLocation: LatLng): RecycleCenter? {
        return uiState.centers.minByOrNull { center ->
            val centerLatLng = center.toLatLng()
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                currentLocation.latitude, currentLocation.longitude,
                centerLatLng.latitude, centerLatLng.longitude,
                results
            )
            results[0]
        }
    }
    
    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (hasLocationPermission) {
            locationHelper.getCurrentLocation { location ->
                Log.d("MapScreen", "Location obtained: lat=${location.latitude}, lng=${location.longitude}")
                userLocation = location
                // Load centers from API
                Log.d("MapScreen", "Calling viewModel.loadNearestCenters...")
                viewModel.loadNearestCenters(location.latitude, location.longitude)
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(location, 15f)
                    )
                }
            }
        }
    }
    
    // Get location on first launch
    LaunchedEffect(Unit) {
        Log.d("MapScreen", "LaunchedEffect triggered, hasLocationPermission=$hasLocationPermission")
        if (hasLocationPermission) {
            locationHelper.getCurrentLocation { location ->
                Log.d("MapScreen", "Location obtained in LaunchedEffect: lat=${location.latitude}, lng=${location.longitude}")
                userLocation = location
                // Load centers from API
                Log.d("MapScreen", "Calling viewModel.loadNearestCenters from LaunchedEffect...")
                viewModel.loadNearestCenters(location.latitude, location.longitude)
                scope.launch {
                    if (navigateToNearest) {
                        // Wait for centers to load, then navigate to nearest
                        val nearestCenter = findNearestCenter(location)
                        if (nearestCenter != null) {
                            selectedCenter = nearestCenter
                            showDialog = true
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(nearestCenter.toLatLng(), 16f)
                            )
                        }
                    } else {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(location, 15f)
                        )
                    }
                }
            }
        } else {
            // Request permission on first launch
            Log.d("MapScreen", "No location permission, requesting...")
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            // Display markers from API data
            Log.d("MapScreen", "Rendering ${uiState.centers.size} markers on map")
            uiState.centers.forEach { center ->
                // Use coordinates[1] for Latitude and coordinates[0] for Longitude
                val latitude = center.location.coordinates[1]
                val longitude = center.location.coordinates[0]
                val position = LatLng(latitude, longitude)
                
                Log.d("MapScreen", "Adding marker for ${center.name} at lat=$latitude, lng=$longitude")
                
                Marker(
                    state = MarkerState(position = position),
                    title = center.name,
                    snippet = center.acceptedMaterials.joinToString(", "),
                    onClick = {
                        selectedCenter = center
                        showDialog = true
                        true
                    }
                )
            }
            
            // Move camera to first center if centers list is not empty
            LaunchedEffect(uiState.centers) {
                if (uiState.centers.isNotEmpty() && !uiState.isLoading) {
                    val firstCenter = uiState.centers.first()
                    val firstCenterLocation = LatLng(
                        firstCenter.location.coordinates[1], // Latitude
                        firstCenter.location.coordinates[0]  // Longitude
                    )
                    Log.d("MapScreen", "Moving camera to first center: ${firstCenter.name} at $firstCenterLocation")
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(firstCenterLocation, 13f)
                    )
                }
            }
        }
        
        // Loading indicator
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp),
                color = PrimaryGreen
            )
        }
        
        // Error message with better UI
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth(0.9f),
                colors = CardDefaults.cardColors(
                    containerColor = when (uiState.errorType) {
                        com.example.rechypher_ai_app.viewmodel.ErrorType.NETWORK_ERROR -> MaterialTheme.colorScheme.errorContainer
                        com.example.rechypher_ai_app.viewmodel.ErrorType.NO_CENTERS_FOUND -> MaterialTheme.colorScheme.tertiaryContainer
                        else -> MaterialTheme.colorScheme.errorContainer
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = when (uiState.errorType) {
                                com.example.rechypher_ai_app.viewmodel.ErrorType.NETWORK_ERROR -> "🌐 Connection Issue"
                                com.example.rechypher_ai_app.viewmodel.ErrorType.SERVER_ERROR -> "🔧 Server Error"
                                com.example.rechypher_ai_app.viewmodel.ErrorType.NO_CENTERS_FOUND -> "📍 No Centers Found"
                                com.example.rechypher_ai_app.viewmodel.ErrorType.TIMEOUT_ERROR -> "⏱️ Timeout"
                                else -> "⚠️ Error"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (uiState.errorType) {
                                com.example.rechypher_ai_app.viewmodel.ErrorType.NETWORK_ERROR -> MaterialTheme.colorScheme.onErrorContainer
                                com.example.rechypher_ai_app.viewmodel.ErrorType.NO_CENTERS_FOUND -> MaterialTheme.colorScheme.onTertiaryContainer
                                else -> MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = when (uiState.errorType) {
                                com.example.rechypher_ai_app.viewmodel.ErrorType.NETWORK_ERROR -> MaterialTheme.colorScheme.onErrorContainer
                                com.example.rechypher_ai_app.viewmodel.ErrorType.NO_CENTERS_FOUND -> MaterialTheme.colorScheme.onTertiaryContainer
                                else -> MaterialTheme.colorScheme.onErrorContainer
                            }
                        )
                    }
                    
                    if (uiState.errorType != com.example.rechypher_ai_app.viewmodel.ErrorType.NO_CENTERS_FOUND) {
                        TextButton(
                            onClick = {
                                userLocation?.let { loc ->
                                    viewModel.loadNearestCenters(loc.latitude, loc.longitude)
                                }
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
        
        // Bottom dialog for disposal center details with expand vertically animation
        AnimatedVisibility(
            visible = showDialog && selectedCenter != null,
            enter = expandVertically(
                animationSpec = tween(durationMillis = 300),
                expandFrom = Alignment.Bottom
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = 300),
                shrinkTowards = Alignment.Bottom
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = selectedCenter?.name ?: "",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen,
                            modifier = Modifier.padding(end = 32.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = selectedCenter?.address ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Accepted materials
                        Text(
                            text = "Accepted Materials:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkGreen
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = selectedCenter?.acceptedMaterials?.joinToString(", ") ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        Button(
                            onClick = {
                                selectedCenter?.let { center ->
                                    val location = center.toLatLng()
                                    val uri = Uri.parse(
                                        "google.navigation:q=${location.latitude},${location.longitude}"
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    context.startActivity(intent)
                                    showDialog = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen
                            )
                        ) {
                            Text(
                                text = "Get Directions",
                                color = White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                    
                    // Close icon button
                    IconButton(
                        onClick = { showDialog = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DarkGreen
                        )
                    }
                }
            }
        }
        
        // Custom floating action buttons - move up when dialog is shown
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = if (showDialog) 200.dp else 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // User location button
            FloatingActionButton(
                onClick = { 
                    if (hasLocationPermission) {
                        locationHelper.getCurrentLocation { location ->
                            userLocation = location
                            viewModel.loadNearestCenters(location.latitude, location.longitude)
                            scope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(location, 15f)
                                )
                            }
                        }
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                containerColor = White,
                contentColor = PrimaryGreen
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user_location),
                    contentDescription = "My Location",
                    modifier = Modifier.size(40.dp)
                )
            }
            
            // Nearest trash center button
            FloatingActionButton(
                onClick = { 
                    val currentLoc = userLocation ?: defaultLocation
                    val nearestCenter = findNearestCenter(currentLoc)
                    if (nearestCenter != null) {
                        selectedCenter = nearestCenter
                        showDialog = true
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(nearestCenter.toLatLng(), 16f)
                            )
                        }
                    }
                },
                containerColor = White,
                contentColor = PrimaryGreen
            ) {
                Image(
                    painter = painterResource(id = R.drawable.trash_location),
                    contentDescription = "Nearest Trash Center",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
