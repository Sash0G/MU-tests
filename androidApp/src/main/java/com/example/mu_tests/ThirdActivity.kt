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
        }
        else return
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