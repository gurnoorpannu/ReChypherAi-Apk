package com.example.rechypher_ai_app.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.rechypher_ai_app.R
import com.example.rechypher_ai_app.ml.WasteClassifier
import com.example.rechypher_ai_app.ui.functions.CarbonImpactCard
import com.example.rechypher_ai_app.ui.theme.DarkGreen
import com.example.rechypher_ai_app.ui.theme.PrimaryGreen
import com.example.rechypher_ai_app.ui.theme.White
import com.example.rechypher_ai_app.utils.WasteCategorizer
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onBackClick: () -> Unit = {},
    scanHistoryViewModel: com.example.rechypher_ai_app.viewmodel.ScanHistoryViewModel? = null,
    onNavigateToMap: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var hasCameraPermission by remember { mutableStateOf(false) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var classificationResult by remember { mutableStateOf<Pair<String, Float>?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    
    android.util.Log.wtf("CameraScreen", "========== CREATING WASTECLASSIFIER ==========")
    val wasteClassifier = remember { WasteClassifier(context) }
    android.util.Log.wtf("CameraScreen", "========== WASTECLASSIFIER CREATED ==========")

    
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }
    
    LaunchedEffect(Unit) {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
    
    DisposableEffect(Unit) {
        onDispose {
            wasteClassifier.close()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
            if (capturedImage == null) {
                // Camera Preview
                if (hasCameraPermission) {
                    CameraPreview(
                        onImageCaptured = { bitmap ->
                            capturedImage = bitmap
                            isProcessing = true
                            // Classify the image
                            val result = wasteClassifier.classifyImage(bitmap)
                            classificationResult = result
                            isProcessing = false
                        }
                    )
                } else {
                    // Permission denied message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Camera permission is required",
                            style = MaterialTheme.typography.titleLarge,
                            color = DarkGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                        ) {
                            Text("Grant Permission", color = White)
                        }
                    }
                }
            } else {
                // Show captured image and classification result
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        capturedImage?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Captured Image",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        
                        // Close button
                        IconButton(
                            onClick = {
                                capturedImage = null
                                classificationResult = null
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = White
                            )
                        }
                    }
                    
                    // Classification Result Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isProcessing) {
                                CircularProgressIndicator(color = PrimaryGreen)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Classifying...", style = MaterialTheme.typography.bodyLarge)
                            } else {
                                classificationResult?.let { (label, confidence) ->
                                    val binType = WasteCategorizer.getBinType(label)
                                    val binTypeWithEmoji = WasteCategorizer.getBinTypeWithEmoji(label)
                                    
                                    Text(
                                        text = "Waste Type",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Bin Type Display
                                    Text(
                                        text = "Bin Type",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = binTypeWithEmoji,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = binType.color
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Confidence: ${(confidence * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Carbon Impact Card
                                    CarbonImpactCard(
                                        wasteLabel = label,
                                        compact = true
                                    )
                                    
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    // Single button to add to history and navigate to map
                                    Button(
                                        onClick = {
                                            // Add to history when button is clicked
                                            scanHistoryViewModel?.addScan(label)
                                            // Navigate to map screen
                                            onNavigateToMap()
                                            capturedImage = null
                                            classificationResult = null
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                                    ) {
                                        Text(
                                            "Get Directions to Nearest Center",
                                            color = White,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

@Composable
fun CameraPreview(
    onImageCaptured: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    
                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Capture button with camera icon
        FloatingActionButton(
            onClick = {
                imageCapture?.let { capture ->
                    capture.takePicture(
                        cameraExecutor,
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val bitmap = image.toBitmap()
                                val rotatedBitmap = rotateBitmap(bitmap, image.imageInfo.rotationDegrees.toFloat())
                                onImageCaptured(rotatedBitmap)
                                image.close()
                            }
                            
                            override fun onError(exception: ImageCaptureException) {
                                exception.printStackTrace()
                            }
                        }
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
                .size(72.dp),
            containerColor = White,
            contentColor = PrimaryGreen
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .border(3.dp, PrimaryGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.camera),
                    contentDescription = "Capture",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

private fun ImageProxy.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
