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
import org.apache.commons.text.similarity.LevenshteinDistance
import java.io.File
import java.util.BitSet

class ThirdActivity : AppCompatActivity() {
    private val randomNum = 10000009
    private lateinit var scrollView: RecyclerView
    lateinit var deleteButton: Button
    private lateinit var tests: MutableList<MyData>
    lateinit var testList: MutableList<MyData>
    var correct = BitSet(160)
    lateinit var file : File
    val gson = Gson()
    var t = false
    private fun listOldTests() {
        file = File(this.filesDir, "data.json")
        if (file.exists()) {
            val fileContent = file.readText()
            t=true
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
                    if (questionNumK in 40..59 && testList[i].answers[questionNumK] != testList[i].questions[questionNumK].answer) {
                        var answer = testList[i].answers[questionNumK].split(", ")
                        var intendedAnswer = testList[i].questions[questionNumK].answer.split(", ")
                        if(answer.size==1)answer= listOf(answer[0],"")
                        if(intendedAnswer.size==1)intendedAnswer= listOf(intendedAnswer[0],"")
                        if (answer[0] == intendedAnswer[1] || answer[1] == intendedAnswer[0]) answer =
                            answer.reversed()
                        if ((answer[0] == intendedAnswer[0] || (LevenshteinDistance().apply(
                                intendedAnswer[0],
                                answer[0]
                            ) <= 2 && answer[0].length > 3)) && (answer[1] == intendedAnswer[1] || (LevenshteinDistance().apply(
                                intendedAnswer[1],
                                answer[1]
                            ) <= 2 && answer[1].length > 3))
                        ) {
                            correct.set(questionNumK)
                            correct.set(questionNumK + 80)
                        } else if (answer[0] == intendedAnswer[0] || (LevenshteinDistance().apply(
                                intendedAnswer[0],
                                answer[0]
                            ) <= 2 && answer[0].length > 3)
                        ) {
                            correct.set(questionNumK)
                            correct.clear(questionNumK + 80)
                        } else if (answer[1] == intendedAnswer[1] || (LevenshteinDistance().apply(
                                intendedAnswer[1],
                                answer[1]
                            ) <= 2 && answer[1].length > 3)
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
                file.writeText(gson.toJson(testList))
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
        if (t) {if(buttonAdapter.isSelectionMode) buttonAdapter.exitSelectionMode()
        file.writeText(gson.toJson(testList))}
        finish()
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

    }
}