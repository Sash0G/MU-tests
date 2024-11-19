package com.example.mu_tests

import ButtonAdapter
import android.content.Intent
import android.os.Bundle
import android.transition.Fade
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
import java.util.BitSet

class ThirdActivity : AppCompatActivity() {
    private val randomNum = 10000009
    private lateinit var scrollView: RecyclerView
    lateinit var deleteButton: Button
    private lateinit var tests: MutableList<MyData>
    lateinit var testList: MutableList<MyData>
    var correct = BitSet(160)
    private fun listOldTests() {
        val file = File(this.filesDir, "data.json")

        val gson = Gson()
        if (file.exists()) {
            val fileContent = file.readText()

            testList = gson.fromJson(fileContent, object : TypeToken<MutableList<MyData>>() {}.type)
            for (i in testList.indices) {
                for (questionNumK in 0..79) {
                    if (testList[i].answers[questionNumK] != testList[i].questions[questionNumK].answer) {
                        correct.clear(questionNumK)
                        correct.clear(questionNumK + 80)
                    } else if (questionNumK < 20 || questionNumK > 59) correct.set(questionNumK)
                    else {
                        correct.set(questionNumK)
                        correct.set(questionNumK + 80)
                    }
                    if (testList[i].answers[questionNumK] != testList[i].questions[questionNumK].answer && questionNumK in 40..59) {
                        if (testList[i].answers[questionNumK].split(", ")[0] == testList[i].questions[questionNumK].answer.split(
                                ", "
                            )[0]
                        ) {
                            correct.set(questionNumK)
                            correct.clear(questionNumK + 80)
                        } else if (testList[i].answers[questionNumK].split(", ").size>1&&testList[i].questions[questionNumK].answer.split(", ").size>1&&testList[i].answers[questionNumK].split(", ")[1] == testList[i].questions[questionNumK].answer.split(
                                ", "
                            )[1]
                        ) {
                            correct.clear(questionNumK)
                            correct.set(questionNumK + 80)
                        }
                    }
                    if(questionNumK in 40..59)
                    {
                        if(testList[i].answers[questionNumK]=="" || testList[i].questions[questionNumK].answer=="," || testList[i].questions[questionNumK].answer=="-, -")testList[i].answers[questionNumK]="_, _"
                        if(testList[i].questions[questionNumK].answer.split(", ")[0]=="-")testList[i].answers[questionNumK]="_"+", "+testList[i].answers[questionNumK]
                        if(testList[i].answers[questionNumK].split(", ").size>1&&testList[i].answers[questionNumK].split(", ")[1]=="-")testList[i].answers[questionNumK]+=", _"
                    }
                }
                testList[i].result = correct.cardinality()
            }

            val recyclerView: RecyclerView = findViewById(R.id.scrollView)
            buttonAdapter =
                ButtonAdapter(this, this, List(testList.size) { it }) { isInSelectionMode ->
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
        } else return
    }

    private fun iniatilise() {
        deleteButton = findViewById(R.id.deleteButton)
        var layoutParams = deleteButton.layoutParams
        layoutParams.width = layoutParams.height
        deleteButton.layoutParams = layoutParams
    }

    private lateinit var buttonAdapter: ButtonAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        val fade = Fade(Fade.IN)
        window.enterTransition = fade
        window.exitTransition = fade
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

    @Deprecated("")
    override fun onBackPressed() {
        if (buttonAdapter.isSelectionMode) buttonAdapter.exitSelectionMode()
        else {
            finish()
            super.onBackPressed()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}