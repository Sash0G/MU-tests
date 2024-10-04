package com.example.mu_tests

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val myButton: Button = findViewById(R.id.myButton)
        var isClicked = false
        myButton.setOnClickListener {

            if (isClicked) {
                // Change back to the original state
                myButton.text = "Click Me"
                myButton.setBackgroundColor(Color.BLUE)
            } else {
                // Change to clicked state
                myButton.text = "Clicked!"
                myButton.setBackgroundColor(Color.RED)
            }

            // Toggle the flag
            isClicked = !isClicked
        }
    }
}