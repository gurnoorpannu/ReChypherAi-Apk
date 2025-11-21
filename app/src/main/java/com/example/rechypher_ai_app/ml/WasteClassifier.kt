package com.example.rechypher_ai_app.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

class WasteClassifier(private val context: Context) {
    private var interpreter: Interpreter? = null
    private val labels = mutableListOf<String>()
    
    init {
        try {
            Log.wtf("WasteClassifier", "========== LOADING MODEL ==========")
            
            // Load labels from labels.txt
            context.assets.open("labels.txt").bufferedReader().useLines { lines ->
                lines.forEach { label ->
                    if (label.isNotBlank()) {
                        labels.add(label.trim())
                    }
                }
            }
            Log.wtf("WasteClassifier", "========== LOADED ${labels.size} LABELS: $labels ==========")
            
            // Load model
            val model = FileUtil.loadMappedFile(context, "model.tflite")
            interpreter = Interpreter(model)
            Log.wtf("WasteClassifier", "========== MODEL LOADED SUCCESSFULLY ==========")
        } catch (e: Exception) {
            Log.wtf("WasteClassifier", "========== MODEL LOAD FAILED: ${e.message} ==========", e)
            e.printStackTrace()
        }
    }
    
    fun classifyImage(bitmap: Bitmap): Pair<String, Float> {
        if (interpreter == null) {
            return Pair("Model not loaded", 0f)
        }
        
        try {
            // Get input tensor shape from the model
            val inputShape = interpreter!!.getInputTensor(0).shape()
            val inputHeight = inputShape[1]
            val inputWidth = inputShape[2]
            
            Log.d("WasteClassifier", "Model input shape: ${inputShape.contentToString()}")
            Log.d("WasteClassifier", "Expected input: ${inputWidth}x${inputHeight}")
            
            // Resize bitmap to model input size
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputWidth, inputHeight, true)
            
            // Convert bitmap to ByteBuffer with proper normalization
            val inputBuffer = ByteBuffer.allocateDirect(4 * inputWidth * inputHeight * 3)
            inputBuffer.order(java.nio.ByteOrder.nativeOrder())
            
            val pixels = IntArray(inputWidth * inputHeight)
            resizedBitmap.getPixels(pixels, 0, inputWidth, 0, 0, inputWidth, inputHeight)
            
            for (pixel in pixels) {
                // Normalize pixel values to [0, 1] or [-1, 1] depending on model
                val r = ((pixel shr 16) and 0xFF) / 255.0f
                val g = ((pixel shr 8) and 0xFF) / 255.0f
                val b = (pixel and 0xFF) / 255.0f
                
                inputBuffer.putFloat(r)
                inputBuffer.putFloat(g)
                inputBuffer.putFloat(b)
            }
            
            inputBuffer.rewind()
            
            // Get output tensor shape
            val outputShape = interpreter!!.getOutputTensor(0).shape()
            val outputSize = outputShape[1]
            
            Log.d("WasteClassifier", "Model output shape: ${outputShape.contentToString()}")
            
            // Prepare output buffer
            val outputBuffer = Array(1) { FloatArray(outputSize) }
            
            // Run inference
            interpreter?.run(inputBuffer, outputBuffer)
            
            // Get results
            val probabilities = outputBuffer[0]
            val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: 0
            val confidence = probabilities[maxIndex]
            
            val labelIndex = if (maxIndex < labels.size) maxIndex else 0
            return Pair(labels[labelIndex], confidence)
        } catch (e: Exception) {
            Log.e("WasteClassifier", "Classification error", e)
            e.printStackTrace()
            return Pair("Error: ${e.message}", 0f)
        }
    }
    
    fun close() {
        interpreter?.close()
    }
}
