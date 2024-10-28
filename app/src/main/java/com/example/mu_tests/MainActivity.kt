package com.example.mu_tests

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button1)
        val button2 = findViewById<Button>(R.id.button2)
        var layoutParams = button.layoutParams
        layoutParams.height =(0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        layoutParams.width =(0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        button.layoutParams = layoutParams
        layoutParams = button2.layoutParams
        layoutParams.height =(0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        layoutParams.width =(0.4 * resources.displayMetrics.widthPixels).toInt() // Set the width in pixels
        button2.layoutParams = layoutParams

        button2.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
        button.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }



        }
    }