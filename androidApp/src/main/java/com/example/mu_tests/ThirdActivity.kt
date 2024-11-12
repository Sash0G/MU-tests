package com.example.mu_tests

import ButtonAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mu_tests.SecondActivity.MyData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class ThirdActivity : AppCompatActivity() {
    private val randomNum = 10000009
    private lateinit var scrollView: RecyclerView
    lateinit var deleteButton: Button
    private lateinit var tests : MutableList<MyData>
    lateinit var testList: MutableList<MyData>
    private fun listOldTests() {
        val file = File(this.filesDir, "data.json")

        val gson = Gson()
        if (file.exists())
        {
            val fileContent = file.readText()
            testList =  gson.fromJson(fileContent, object : TypeToken<MutableList<MyData>>() {}.type)
        }
        else return
//        scrollView.isHorizontalFadingEdgeEnabled = true
//        scrollView.setFadingEdgeLength(50)
//        val displayMetrics = resources.displayMetrics
//        val screenWidth = displayMetrics.widthPixels
//        val screenHeight = displayMetrics.heightPixels
//        val buttonWidthPercent = 0.9f
//        val buttonHeightPercent = 0.08f
//
//        val buttonWidth = (screenWidth * buttonWidthPercent).toInt()
//        val buttonHeight = (screenHeight * buttonHeightPercent).toInt()
//        var br = 1
//        for (test in testList) {
//            val button = Button(this)
//            val layoutParams = LinearLayout.LayoutParams(
//                buttonWidth, buttonHeight
//            )
//            layoutParams.setMargins(0, 20, 0, 20)
//            button.layoutParams = layoutParams
//            button.id = br*randomNum
//            br++
//            button.text = "Result: ${test.result}/80"
//            button.textSize = 16f
//            button.textAlignment = Button.TEXT_ALIGNMENT_CENTER
//            button.setTextColor(resources.getColor(R.color.black))
//            button.setOnClickListener {
//                val intent = Intent(this, SecondActivity::class.java)
//                intent.putExtra("test", test)
//                startActivity(intent)
//            }
//            button.setBackgroundResource(R.drawable.rectangle_button)
//            scrollView.addView(button)
//        }

    }
    private fun iniatilise() {
        deleteButton = findViewById(R.id.deleteButton)
        var layoutParams = deleteButton.layoutParams
        layoutParams.width =layoutParams.height
        deleteButton.layoutParams = layoutParams
    }
    private lateinit var buttonAdapter: ButtonAdapter
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
        val recyclerView: RecyclerView = findViewById(R.id.scrollView)
        buttonAdapter = ButtonAdapter(this,this, generateButtonList()) { isInSelectionMode ->
            if (isInSelectionMode) deleteButton.visibility = View.VISIBLE
            else deleteButton.visibility = View.INVISIBLE
        }
        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = buttonAdapter
        }
        deleteButton.setOnClickListener {
            buttonAdapter.deleteSelectedItems()
        }
        println(testList.size)
        println(generateButtonList())

    }
    private fun generateButtonList(): List<Int> {
        return List(testList.size) { it }
    }
    @Deprecated("")
    override fun onBackPressed() {
        if(buttonAdapter.isSelectionMode) buttonAdapter.exitSelectionMode()
        else super.onBackPressed()
    }
}