package com.example.mu_tests

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mu_tests.SecondActivity.MyData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class ThirdActivity : AppCompatActivity() {
    private lateinit var layout: LinearLayout
    private lateinit var scrollView: ScrollView
    private lateinit var tests : MutableList<MyData>
    private fun listOldTests() {
        val file = File(this.filesDir, "data.json")
        val testList: MutableList<MyData>
        val gson = Gson()
        if (file.exists())
        {
            val fileContent = file.readText()
            testList =  gson.fromJson(fileContent, object : TypeToken<MutableList<MyData>>() {}.type)
        }
        else return
        scrollView.isHorizontalFadingEdgeEnabled = true
        scrollView.setFadingEdgeLength(50)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val buttonWidthPercent = 0.9f
        val buttonHeightPercent = 0.08f

        val buttonWidth = (screenWidth * buttonWidthPercent).toInt()
        val buttonHeight = (screenHeight * buttonHeightPercent).toInt()
        for (test in testList) {
            layout.setFadingEdgeLength(100)
            val button = Button(this)
            val layoutParams = LinearLayout.LayoutParams(
                buttonWidth, buttonHeight
            )
            layoutParams.setMargins(0, 20, 0, 20)
            button.layoutParams = layoutParams
            button.text = "Result: ${test.result}/80"
            button.textSize = 16f
            button.textAlignment = Button.TEXT_ALIGNMENT_CENTER
            button.setTextColor(resources.getColor(R.color.black))
            button.setOnClickListener {
                val intent = Intent(this, SecondActivity::class.java)
                intent.putExtra("test", test)
                startActivity(intent)
            }
            button.setBackgroundResource(R.drawable.rectangle_button)
            layout.addView(button)
        }
    }
    private fun iniatilise() {
        layout = findViewById(R.id.linearLayout)
        scrollView = findViewById(R.id.scrollView)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_third)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        iniatilise()
        listOldTests()
    }
}