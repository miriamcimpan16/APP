package com.example.app

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PlantAnalyzer(private val context: Context) {

    private val interpreter: Interpreter
    private val labels: List<String>

    init {
        interpreter = Interpreter(loadModelFile())
        labels = loadLabels()
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("plant_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    private fun loadLabels(): List<String> {
        return context.assets.open("labels.txt")
            .bufferedReader()
            .readLines()
    }

    /**
     * @return Triple(label, confidence, rawIndex)
     */
    fun analyze(bitmap: Bitmap): Triple<String, Float, Int> {

        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        val inputBuffer =
            ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4)
                .order(ByteOrder.nativeOrder())

        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = resized.getPixel(x, y)

                inputBuffer.putFloat(((pixel shr 16) and 0xFF) / 255f) // R
                inputBuffer.putFloat(((pixel shr 8) and 0xFF) / 255f)  // G
                inputBuffer.putFloat((pixel and 0xFF) / 255f)          // B
            }
        }

        val output = Array(1) { FloatArray(1001) }
        interpreter.run(inputBuffer, output)

        val probabilities = output[0]

        // ignorăm index 0 (background)
        var maxIndex = 1
        for (i in 1 until probabilities.size) {
            if (probabilities[i] > probabilities[maxIndex]) {
                maxIndex = i
            }
        }

        val confidence = probabilities[maxIndex]

        val label = labels.getOrElse(maxIndex - 1) {
            "Unknown object"
        }

        return Triple(label, confidence, maxIndex)
    }
}
