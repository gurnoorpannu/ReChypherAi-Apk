package com.example.rechypher_ai_app.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.Dialog
import com.example.rechypher_ai_app.R
import com.example.rechypher_ai_app.ui.theme.DarkGreen
import com.example.rechypher_ai_app.ui.theme.PrimaryGreen
import com.example.rechypher_ai_app.ui.theme.White
import com.example.rechypher_ai_app.utils.LocationHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navigateToNearest: Boolean = false
) {
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    val scope = rememberCoroutineScope()
    
    // Demo disposal centers - TODO: Fetch from backend
    val demoDisposalCenters = remember {
        listOf(
            LatLng(28.6139, 77.2090) to "Green Waste Center",
            LatLng(28.6289, 77.2190) to "Eco Disposal Hub",
            LatLng(28.5989, 77.1990) to "Recycle Point",
            LatLng(28.6450, 77.2200) to "Clean Earth Center",
            LatLng(28.5850, 77.2350) to "Waste Management Station"
        )
    }
    
    // Default location
    val defaultLocation = LatLng(28.6139, 77.2090) // Delhi, India
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember { mutableStateOf(locationHelper.hasLocationPermission()) }
    
    // Dialog state for selected disposal center
    var selectedCenter by remember { mutableStateOf<Pair<LatLng, String>?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }
    
    // Function to find nearest disposal center
    fun findNearestCenter(currentLocation: LatLng): Pair<LatLng, String> {
        return demoDisposalCenters.minByOrNull { (location, _) ->
            val results = FloatArray(1)
            android.location.Location.distanceBetween(
                currentLocation.latitude, currentLocation.longitude,
                location.latitude, location.longitude,
                results
            )
            results[0]
        } ?: demoDisposalCenters.first()
    }
    
    // Location permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        if (hasLocationPermission) {
            locationHelper.getCurrentLocation { location ->
                userLocation = location
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
        if (hasLocationPermission) {
            locationHelper.getCurrentLocation { location ->
                userLocation = location
                scope.launch {
                    if (navigateToNearest) {
                        // Navigate to nearest center automatically
                        val nearestCenter = findNearestCenter(location)
                        selectedCenter = nearestCenter
                        showDialog = true
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(nearestCenter.first, 16f)
                        )
                    } else {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(location, 15f)
                        )
                    }
                }
            }
        } else {
            // Request permission on first launch
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
                demoDisposalCenters.forEach { (position, title) ->
                    Marker(
                        state = MarkerState(position = position),
                        title = title,
                        snippet = "Tap for details",
                        onClick = {
                            selectedCenter = position to title
                            showDialog = true
                            true
                        }
                    )
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
                                text = selectedCenter!!.second,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen,
                                modifier = Modifier.padding(end = 32.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Waste Disposal Center",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Button(
                                onClick = {
                                    val location = selectedCenter!!.first
                                    val uri = Uri.parse(
                                        "google.navigation:q=${location.latitude},${location.longitude}"
                                    )
                                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    context.startActivity(intent)
                                    showDialog = false
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
                        selectedCenter = nearestCenter
                        showDialog = true
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(nearestCenter.first, 16f)
                            )
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
