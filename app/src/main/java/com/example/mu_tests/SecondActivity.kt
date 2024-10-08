package com.example.mu_tests

import android.graphics.Color
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.BitSet
import androidx.constraintlayout.widget.ConstraintSet

class SecondActivity : AppCompatActivity() {

    private lateinit var options: List<Button>
    private lateinit var question: TextView
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var finishButton: Button
    private lateinit var startOverButton: Button
    private lateinit var firstTextView: TextView
    private lateinit var firstEditText: EditText
    private lateinit var secondTextView: TextView
    private lateinit var secondEditText: EditText
    private lateinit var thirdTextView: TextView
    private lateinit var textResult: TextView
    private lateinit var blankQuestionsPage: ConstraintLayout
    private lateinit var questionLayout: ConstraintLayout
    private val correct = BitSet(70)
    private var questionNum = 0
    private var isClicked1 = false
    private var isClicked2 = false
    private var isClicked3 = false
    private var isClicked4 = false
    private var chosenAnswer = Array(70) { "" }
    private val map = mapOf("а" to 0, "б" to 1, "в" to 2, "г" to 3, "да" to 0, "не" to 1)

    data class DataFormat(
        val question: String,
        val option1: String,
        val option2: String,
        val option3: String,
        val option4: String,
        val answer: String
    )


    private lateinit var dataList: List<DataFormat>
    private lateinit var set1Data: List<DataFormat>
    private lateinit var set2Data: List<DataFormat>
    private lateinit var set3Data: List<DataFormat>
    private lateinit var set4Data: List<DataFormat>

    private fun getNextQuestionOptionsBlanks(questionText: String) {
        val parts = questionText.split('_')
        val textPaint = TextView(this).paint
        val availableWidth = questionLayout.width.toFloat()-20
        val textViewIds = mutableListOf<Int>()
        var remainingText = parts[0]
        println(remainingText)
        var br = 0
        while (remainingText.isNotEmpty()) {
            println("This is the: " + remainingText)
            var ind = textPaint.breakText(remainingText, true, availableWidth, null)
            if (remainingText.lastIndexOf(' ', ind) != -1) ind = remainingText.lastIndexOf(' ', ind)
            val textToShow = remainingText.substring(0, ind)
            remainingText = if (ind + 1 < remainingText.length) remainingText.substring(ind + 1)
            else ""
            val textView = TextView(this).apply {
                text = textToShow
                id = View.generateViewId()
                textSize = 16f
                setTextColor(Color.BLACK)
            }
            questionLayout.addView(textView)
            textViewIds.add(textView.id)
        }
        for (i in textViewIds.indices) {
            val constraintSet = ConstraintSet().apply {
                clone(questionLayout)
                connect(
                    textViewIds[i],
                    ConstraintSet.START,
                    questionLayout.id,
                    ConstraintSet.START,
                )
                connect(textViewIds[i], ConstraintSet.END, questionLayout.id, ConstraintSet.END)
                setHorizontalBias(textViewIds[i], 0.6f)

                if (i != 0) connect(
                    textViewIds[i],
                    ConstraintSet.TOP,
                    textViewIds[i - 1],
                    ConstraintSet.BOTTOM
                )
                else connect(
                    textViewIds[i],
                    ConstraintSet.TOP,
                    questionLayout.id,
                    ConstraintSet.TOP
                )
                connect(textViewIds[i], ConstraintSet.BOTTOM, questionLayout.id, ConstraintSet.BOTTOM)
                if (i == 0) setVerticalBias(textViewIds[i], 0.3f)
                else setVerticalBias(textViewIds[i], 0.05f)
                applyTo(questionLayout)
            }
        }
        val constraintSet = ConstraintSet().apply {
            clone(questionLayout)
            connect(
                firstEditText.id,
                ConstraintSet.START,
                textViewIds[textViewIds.size-1],
                ConstraintSet.START,
            )
            connect(firstEditText.id, ConstraintSet.END, questionLayout.id, ConstraintSet.END)
            setHorizontalBias(firstEditText.id, 0.0f)
            if(textViewIds.size)
            connect(
                firstEditText.id,
                ConstraintSet.TOP,
                textViewIds[textViewIds.size-2],
                ConstraintSet.BOTTOM
            )
            connect(firstEditText.id, ConstraintSet.BOTTOM, questionLayout.id, ConstraintSet.BOTTOM)
            applyTo(questionLayout)
        }
        firstEditText.visibility=EditText.VISIBLE
        /*

        textViewIds.clear()
        remainingText = parts[1]
        println(remainingText)
        br = 0
        while (remainingText.isNotEmpty() && br++ < 5) {
            println("This is the: " + remainingText)
            var ind = textPaint.breakText(remainingText, true, availableWidth, null)
            if (remainingText.lastIndexOf(' ', ind) != -1) ind = remainingText.lastIndexOf(' ', ind)
            val textToShow = remainingText.substring(0, ind)
            remainingText = if (ind + 1 < remainingText.length) remainingText.substring(ind + 1)
            else ""
            val textView = TextView(this).apply {
                text = textToShow
                id = View.generateViewId()
                textSize = 16f
                setTextColor(Color.BLACK)
            }
            questionLayout.addView(textView)
            textViewIds.add(textView.id)
        }
        for (i in textViewIds.indices) {
            val constraintSet = ConstraintSet().apply {
                clone(questionLayout)
                connect(
                    textViewIds[i],
                    ConstraintSet.START,
                    questionLayout.id,
                    ConstraintSet.START,
                )
                connect(textViewIds[i], ConstraintSet.END, questionLayout.id, ConstraintSet.END)
                setHorizontalBias(textViewIds[i], 0.6f)

                if (i != 0) connect(
                    textViewIds[i],
                    ConstraintSet.TOP,
                    lastId,
                    ConstraintSet.BOTTOM
                )
                else connect(
                    textViewIds[i],
                    ConstraintSet.TOP,
                    questionLayout.id,
                    ConstraintSet.TOP
                )
                connect(textViewIds[i], ConstraintSet.BOTTOM, questionLayout.id, ConstraintSet.BOTTOM)
                if (i == 0) setVerticalBias(textViewIds[i], 0.3f)
                else setVerticalBias(textViewIds[i], 0.05f)
                applyTo(questionLayout)
            }
        }*/
    }

    private fun checkAnswer() {
        println(chosenAnswer[questionNum - 1])
        if (questionNum > 30) println(dataList[questionNum - 1].answer)
        if (chosenAnswer[questionNum - 1] == dataList[questionNum - 1].answer) correct.set(
            questionNum - 1
        )
        else correct.clear(questionNum - 1)
    }

    private fun getNextQuestionOptions(i: Int) {
        val nextQuestion = dataList[i]
        question.text = nextQuestion.question
        isClicked1 = false;isClicked2 = false;isClicked3 = false;isClicked4 = false
        options[0].setOnClickListener {
            changeState(options[0], isClicked1)
            isClicked1 = !isClicked1; isClicked2 = false; isClicked3 = false; isClicked4 = false
            if (isClicked1 && questionNum < 30) chosenAnswer[questionNum] = "а"
            else if (isClicked1 && questionNum >= 50) chosenAnswer[questionNum] = "да"
            else chosenAnswer[questionNum] = ""
        }
        options[1].setOnClickListener {
            changeState(options[1], isClicked2)
            isClicked2 = !isClicked2; isClicked1 = false; isClicked3 = false; isClicked4 = false
            if (isClicked2 && questionNum < 30) chosenAnswer[questionNum] = "б"
            else if (isClicked2 && questionNum >= 50) chosenAnswer[questionNum] = "не"
            else chosenAnswer[questionNum] = ""
        }
        if (questionNum < 30) {
            options[0].text = nextQuestion.option1
            options[1].text = nextQuestion.option2
            options[2].text = nextQuestion.option3
            options[3].text = nextQuestion.option4
            isClicked1 = false; isClicked2 = false; isClicked3 = false; isClicked4 = false

            if (questionNum < 30) options[2].setOnClickListener {
                changeState(options[2], isClicked3)
                isClicked3 = !isClicked3; isClicked1 = false; isClicked2 = false; isClicked4 = false
                if (isClicked3) chosenAnswer[questionNum] = "в"
                else chosenAnswer[questionNum] = ""
            }
            if (questionNum < 30) options[3].setOnClickListener {
                changeState(options[3], isClicked4)
                isClicked4 = !isClicked4; isClicked1 = false; isClicked2 = false; isClicked3 = false
                if (isClicked4) chosenAnswer[questionNum] = "г"
                else chosenAnswer[questionNum] = ""
            }
        } else if (questionNum < 70) {
            options[0].text = "да"
            options[1].text = "не"
        }
    }

    private fun changeState(clickedButton: Button, clicked: Boolean) {
        for (button in options) {
            button.setBackgroundResource(R.drawable.rectangle_button)
        }
        if (!clicked) clickedButton.setBackgroundResource(R.drawable.rectangle_button_clicked)
    }

    private fun showQuestion() {
        if (questionNum < 30) {
            question.text = dataList[questionNum].question
            options[0].text = dataList[questionNum].option1
            options[1].text = dataList[questionNum].option2
            options[2].text = dataList[questionNum].option3
            options[3].text = dataList[questionNum].option4
        } else if (questionNum < 50) {
            getNextQuestionOptionsBlanks(dataList[questionNum].question)
        } else if (questionNum < 70) {
            question.text = dataList[questionNum].question
            options[0].text = "да"
            options[1].text = "не"
        }
    }

    private fun newDataSet() {
        set1Data = set1Data.shuffled()
        set2Data = set2Data.shuffled()
        set3Data = set3Data.shuffled()
        set4Data = set4Data.shuffled()
        dataList = set1Data.subList(0, 20) + set2Data.subList(0, 10) + set3Data.subList(
            0,
            20
        ) + set4Data.subList(0, 20)
    }

    private fun initialize() {
        question = findViewById(R.id.questionMain)
        nextButton = findViewById(R.id.next)
        prevButton = findViewById(R.id.prev)
        finishButton = findViewById(R.id.finish)
        startOverButton = findViewById(R.id.startOver)
//        firstTextView = findViewById(R.id.FirstTextView)
        firstEditText = findViewById(R.id.firstEditText)
//        secondTextView = findViewById(R.id.SecondTextView)
        secondEditText = findViewById(R.id.secondEditText)
//        thirdTextView = findViewById(R.id.ThirdTextView)
        textResult = findViewById(R.id.textView)
        questionLayout = findViewById(R.id.questionLayout)
        blankQuestionsPage = findViewById(R.id.questionPage)
        set1Data = readGroup1(this, "set1.csv")
        set2Data = readGroup1(this, "set2.csv")
        set3Data = readGroup2(this, "set3.csv")
        set4Data = readGroup2(this, "set4.csv")
        options = listOf(
            findViewById(R.id.FirstChoice),
            findViewById(R.id.SecondChoice),
            findViewById(R.id.ThirdChoice),
            findViewById(R.id.FourthChoice)
        )
    }

    private fun blankQuestions() {
        question.visibility = TextView.INVISIBLE
        blankQuestionsPage.visibility = LinearLayout.VISIBLE
        for (button in options)
            button.visibility = Button.INVISIBLE
    }

    private fun fourOptions() {
        question.visibility = TextView.VISIBLE
        blankQuestionsPage.visibility = LinearLayout.INVISIBLE
        for (button in options)
            button.visibility = Button.VISIBLE
        if (questionNum == 0) {
            changeState(options[0], true)
            prevButton.visibility = Button.INVISIBLE
        }
        finishButton.visibility = Button.INVISIBLE
        nextButton.visibility = Button.VISIBLE
        for (button in options)
            button.setOnClickListener(null)
    }

    private fun twoOptions() {
        question.visibility = TextView.VISIBLE
        blankQuestionsPage.visibility = LinearLayout.INVISIBLE
        options[0].visibility = Button.VISIBLE
        options[1].visibility = Button.VISIBLE
        options[2].visibility = Button.INVISIBLE
        options[3].visibility = Button.INVISIBLE
    }

    private fun showAnswerOptions() {
        for (option in options)
            option.setBackgroundResource(R.drawable.rectangle_button)
        if (chosenAnswer[questionNum] != "") options[map[chosenAnswer[questionNum]]!!].setBackgroundResource(
            R.drawable.rectangle_button_wrong
        )
        options[map[dataList[questionNum].answer]!!].setBackgroundResource(R.drawable.rectangle_button_correct)
    }

    private fun showAnswerBlanks() {
        firstEditText.setText(dataList[questionNum].answer.split(", ")[0])
        secondEditText.setText(dataList[questionNum].answer.split(", ")[1])
        firstEditText.setTextColor(RED)
        secondEditText.setTextColor(RED)
        if (chosenAnswer[questionNum].split(", ")[0] == dataList[questionNum].answer.split(", ")[0])
            firstEditText.setTextColor(GREEN)
        if (chosenAnswer[questionNum].split(", ")[1] == dataList[questionNum].answer.split(", ")[1])
            secondEditText.setTextColor(GREEN)
    }

    private fun showResult() {
        textResult.text = "Result: " + correct.cardinality() + "/70"
        textResult.visibility = TextView.VISIBLE
        fourOptions()
        questionNum = 0
        showQuestion()
        showAnswerOptions()
        nextButton.setOnClickListener {
            if (questionNum == 0) prevButton.visibility = Button.VISIBLE
            if (questionNum == 29) blankQuestions()
            if (questionNum == 49) twoOptions()
            if (questionNum == 68) {
                nextButton.visibility = Button.INVISIBLE
                startOverButton.visibility = Button.VISIBLE
            }
            questionNum++
            showQuestion()
            if (questionNum < 30) showAnswerOptions()
            else if (questionNum < 50) showAnswerBlanks()
            else if (questionNum < 70) showAnswerOptions()

        }
        prevButton.setOnClickListener {
            questionNum--;
            if (questionNum == 0) prevButton.visibility = Button.VISIBLE
            if (questionNum == 29) fourOptions()
            if (questionNum == 49) blankQuestions()
            if (questionNum == 68) {
                nextButton.visibility = Button.INVISIBLE
                startOverButton.visibility = Button.VISIBLE
            }
            showQuestion()
            if (questionNum < 30) showAnswerOptions()
            else if (questionNum < 50) showAnswerBlanks()
            else if (questionNum < 70) showAnswerOptions()
        }
        startOverButton.setOnClickListener {
            startOverButton.visibility = Button.INVISIBLE
            nextButton.visibility = Button.VISIBLE
            prevButton.visibility = Button.INVISIBLE

            newTest()
        }
    }

    private fun showQuestionOptions(flag: Boolean = false) {
        if (questionNum == 50 && flag) chosenAnswer[questionNum - 1] =
            firstEditText.text.toString() + ", " + secondEditText.text.toString()
        if (chosenAnswer[questionNum] != "") changeState(
            options[map[chosenAnswer[questionNum]]!!],
            false
        )
        else changeState(options[0], true)
        getNextQuestionOptions(questionNum)
    }

    private fun showQuestionBlanks() {

        if (questionNum != 30) chosenAnswer[questionNum - 1] =
            firstEditText.text.toString() + ", " + secondEditText.text.toString()
        getNextQuestionOptionsBlanks(dataList[questionNum].question)
        if (chosenAnswer[questionNum] != "") firstEditText.setText(chosenAnswer[questionNum].split(", ")[0])
        if (chosenAnswer[questionNum].split(", ").size > 1) secondEditText.setText(
            chosenAnswer[questionNum].split(
                ", "
            )[1]
        )
    }

    private fun newTest() {
        textResult.visibility = TextView.INVISIBLE
        for (option in options)
            option.setBackgroundResource(R.drawable.rectangle_button)
        correct.clear()
        chosenAnswer = Array(70) { "" }
        newDataSet()
        fourOptions()
        questionNum = 0
        getNextQuestionOptions(questionNum)
        nextButton.setOnClickListener {
            if (questionNum == 0) prevButton.visibility = Button.VISIBLE
            if (questionNum == 29) blankQuestions()
            if (questionNum == 49) twoOptions()
            if (questionNum == 68) {
                nextButton.visibility = Button.INVISIBLE
                finishButton.visibility = Button.VISIBLE
            }
            questionNum++;
            checkAnswer()
            if (questionNum < 30) showQuestionOptions()
            else if (questionNum < 50) showQuestionBlanks()
            else if (questionNum < 70) showQuestionOptions(true)

        }
        prevButton.setOnClickListener {
            questionNum--;
            if (questionNum == 0) prevButton.visibility = Button.INVISIBLE
            if (questionNum == 29) fourOptions()
            if (questionNum == 49) blankQuestions()
            if (questionNum == 68) {
                nextButton.visibility = Button.VISIBLE
                finishButton.visibility = Button.INVISIBLE
            }
            if (questionNum < 30) showQuestionOptions()
            else if (questionNum < 50) showQuestionBlanks()
            else if (questionNum < 70) showQuestionOptions()
        }
        finishButton.setOnClickListener {
            questionNum++;
            checkAnswer()
            showResult()
        }
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
        initialize()
        newTest()
    }
}