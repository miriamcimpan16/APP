package com.example.app

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val imageView = findViewById<ImageView>(R.id.resultImage)
        val resultText = findViewById<TextView>(R.id.resultText)

        // primim calea imaginii
        val imagePath = intent.getStringExtra("image_path")

        if (imagePath.isNullOrEmpty()) {
            resultText.text = "No image received"
            return
        }

        val bitmap = BitmapFactory.decodeFile(imagePath)
        imageView.setImageBitmap(bitmap)

        // Analiză ML
        val analyzer = PlantAnalyzer(this)
        val (label, confidence, classIndex) = analyzer.analyze(bitmap)

        val confidencePercent = confidence * 100f

        resultText.text = """
            Detected: $label
            Confidence: ${String.format(Locale.US, "%.2f", confidencePercent)}%
            Class index: $classIndex
        """.trimIndent()
    }
}
