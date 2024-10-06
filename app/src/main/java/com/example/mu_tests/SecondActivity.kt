package com.example.mu_tests

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVParserBuilder
import java.io.InputStreamReader
import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.*
import java.util.BitSet

class SecondActivity : AppCompatActivity() {

    private lateinit var options: List<Button>
    private lateinit var question: TextView
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var finishButton: Button
    private val correct = BitSet(30)
    private var questionNum = 0
    private var isClicked1 = false
    private var isClicked2 = false
    private var isClicked3 = false
    private var isClicked4 = false
    private var chosenAnswer = Array(30) { 0 }

    data class CsvRow(
        val question: String,
        val option1: String,
        val option2: String,
        val option3: String,
        val option4: String,
        val answer: String
    )

    private lateinit var dataList: List<CsvRow>
    private lateinit var set1Data: List<CsvRow>
    private lateinit var set2Data: List<CsvRow>
    private fun readCSV(context: Context, fileName: String): List<CsvRow> {

        // Create a CSV parser with a semicolon as the delimiter
        val csvParser = CSVParserBuilder().withSeparator(';').build()
        val data = mutableListOf<CsvRow>()
        // Access the CSV file from assets or raw resources
        val inputStream = context.assets.open(fileName) // if it's in assets
        val reader =
            CSVReaderBuilder(InputStreamReader(inputStream)).withCSVParser(csvParser).build()

        // Read the CSV line by line
        var nextLine: Array<String>?
        while (reader.readNext().also { nextLine = it } != null) {
            // Ensure there are exactly 5 columns in the row
            if (nextLine?.size == 6) {
                val row = CsvRow(
                    question = nextLine!![0],
                    option1 = nextLine!![1],
                    option2 = nextLine!![2],
                    option3 = nextLine!![3],
                    option4 = nextLine!![4],
                    answer = nextLine!![5]
                )
                data.add(row)
            }
        }
        // Close the reader and return the list
        reader.close()
        return data
        // Now you can shuffle and use your data
    }

    private fun checkAnswer(
        isClicked1: Boolean,
        isClicked2: Boolean,
        isClicked3: Boolean,
        isClicked4: Boolean,
        answer: String
    ) {
        if (answer == "а" && isClicked1) correct.set(questionNum)
        else if (answer == "б" && isClicked2) correct.set(questionNum)
        else if (answer == "в" && isClicked3) correct.set(questionNum)
        else if (answer == "г" && isClicked4) correct.set(questionNum)
        else correct.clear(questionNum)
    }

    private fun getNextQuestion(i: Int) {
        val nextQuestion = dataList[i]
        question.text = nextQuestion.question
        options[0].text = nextQuestion.option1
        options[1].text = nextQuestion.option2
        options[2].text = nextQuestion.option3
        options[3].text = nextQuestion.option4
        isClicked1 = false
        isClicked2 = false
        isClicked3 = false
        isClicked4 = false
        options[0].setOnClickListener {
            changeState(options[0], isClicked1)
            isClicked1 = !isClicked1; isClicked2 = false; isClicked3 = false; isClicked4 = false
            chosenAnswer[questionNum] = 1
        }
        options[1].setOnClickListener {
            changeState(options[1], isClicked2)
            isClicked2 = !isClicked2; isClicked1 = false; isClicked3 = false; isClicked4 = false
            chosenAnswer[questionNum] = 2
        }
        options[2].setOnClickListener {
            changeState(options[2], isClicked3)
            isClicked3 = !isClicked3; isClicked1 = false; isClicked2 = false; isClicked4 = false
            chosenAnswer[questionNum] = 3
        }
        options[3].setOnClickListener {
            changeState(options[3], isClicked4)
            isClicked4 = !isClicked4; isClicked1 = false; isClicked2 = false; isClicked3 = false
            chosenAnswer[questionNum] = 4
        }
    }

    private fun changeState(clickedButton: Button, clicked: Boolean) {
        for (button in options) {
            button.setBackgroundResource(R.drawable.rectangle_button)
        }
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
        options = listOf(
            findViewById(R.id.FirstChoice),
            findViewById(R.id.SecondChoice),
            findViewById(R.id.ThirdChoice),
            findViewById(R.id.FourthChoice)
        )

        question = findViewById(R.id.questionMain)
        nextButton = findViewById(R.id.next)
        prevButton = findViewById(R.id.prev)
        finishButton = findViewById(R.id.finish)
        set1Data = readCSV(this, "set1.csv")
        set2Data = readCSV(this, "set2.csv")
        set1Data = set1Data.shuffled()
        set2Data = set2Data.shuffled()
        dataList = set1Data.subList(0, 20) + set2Data.subList(0, 10)
        questionNum = 0
        getNextQuestion(questionNum)
        nextButton.setOnClickListener {
            checkAnswer(
                isClicked1,
                isClicked2,
                isClicked3,
                isClicked4,
                dataList[questionNum].answer
            )
            questionNum++;
            if (chosenAnswer[questionNum] != 0) changeState(
                options[chosenAnswer[questionNum] - 1],
                false
            )
            else changeState(options[0], true)
            if (questionNum == 29) {
                nextButton.visibility = Button.INVISIBLE
                finishButton.visibility = Button.VISIBLE
            }
            prevButton.visibility = Button.VISIBLE
            getNextQuestion(questionNum)

        }
        prevButton.setOnClickListener {
            questionNum--;
            if (chosenAnswer[questionNum] != 0) changeState(
                options[chosenAnswer[questionNum] - 1],
                false
            )
            else changeState(options[0], true)
            if (questionNum == 0) prevButton.visibility = Button.INVISIBLE
            nextButton.visibility = Button.VISIBLE
            finishButton.visibility = Button.INVISIBLE
            getNextQuestion(questionNum)
        }
        finishButton.setOnClickListener {
            checkAnswer(
                isClicked1,
                isClicked2,
                isClicked3,
                isClicked4,
                dataList[questionNum].answer
            )
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alert")
            builder.setMessage("You have " + correct.cardinality() + " correct answers out of 30")
            builder.setPositiveButton("Start over") { _, _ ->
                // Handle the OK button click here
                set1Data = set1Data.shuffled()
                set2Data = set2Data.shuffled()
                dataList = set1Data.subList(0, 20) + set2Data.subList(0, 10
                )
                questionNum = 0
                correct.clear()
                getNextQuestion(questionNum)
                nextButton.visibility = Button.VISIBLE
                prevButton.visibility = Button.INVISIBLE
                finishButton.visibility = Button.INVISIBLE
            }

            val alertDialog = builder.create()
            alertDialog.show()
        }
    }
}