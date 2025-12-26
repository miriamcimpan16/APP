package com.example.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historyy)

        val textView = findViewById<TextView>(R.id.historyText)
        textView.text = "Poze salvate: ${ImageStorage.images.size}"
    }
}
