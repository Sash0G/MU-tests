package com.example.mu_tests

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {

    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var question: TextView
    private fun changeState(clickedButton: Button, clicked: Boolean) {
        button1.setBackgroundResource(R.drawable.rectangle_button)
        button2.setBackgroundResource(R.drawable.rectangle_button)
        button3.setBackgroundResource(R.drawable.rectangle_button)
        button4.setBackgroundResource(R.drawable.rectangle_button)
        if (!clicked) clickedButton.setBackgroundResource(R.drawable.rectangle_button_clicked)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        button1 = findViewById(R.id.FirstChoice)
        button2 = findViewById(R.id.SecondChoice)
        button3 = findViewById(R.id.ThirdChoice)
        button4 = findViewById(R.id.FourthChoice)
        question = findViewById(R.id.questionMain)
        question.text = "dsad"
        var isClicked1 = false;
        var isClicked2 = false;
        var isClicked3 = false;
        var isClicked4 = false
        button1.setOnClickListener {
            changeState(button1, isClicked1)
            isClicked1 = !isClicked1; isClicked2 = false; isClicked3 = false; isClicked4 = false
        }
        button2.setOnClickListener {
            changeState(button2, isClicked2)
            isClicked2 = !isClicked2; isClicked1 = false; isClicked3 = false; isClicked4 = false
        }
        button3.setOnClickListener {
            changeState(button3, isClicked3)
            isClicked3 = !isClicked3; isClicked1 = false; isClicked2 = false; isClicked4 = false
        }
        button4.setOnClickListener {
            changeState(button4, isClicked4)
            isClicked4 = !isClicked4; isClicked1 = false; isClicked2 = false; isClicked3 = false
        }
    }
}