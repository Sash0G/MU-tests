package com.example.mu_tests

import android.graphics.Color
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.BitSet
import androidx.constraintlayout.widget.ConstraintSet
import java.util.Objects
import java.util.Vector

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
    private lateinit var questionLayout: ConstraintLayout
    private val correct = BitSet(80)
    private val used = BitSet(80)
    private var questionNum = 0
    private var isClicked1 = false
    private var isClicked2 = false
    private var isClicked3 = false
    private var isClicked4 = false
    private var chosenAnswer = Array(80) { "" }
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
    private var textViewIds = mutableListOf<Int>()
    private var textViewWidth = 0
    private var textViewWidth1 = 0
    private var textViewWidth2 = 0
    private var textViewIds3 = mutableListOf<Int>()
    private var textViewIds2 = mutableListOf<Int>()
    private val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    private val heightMeasureSpec =
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    var startOrEnd: Int = ConstraintSet.END
    var topOrBottom: Int = ConstraintSet.TOP
    var curr = 0
    var topId = Vector<Int>()
    var leftId = 0
    var availableWidth = 0.0f
    var t = false
    private var firstEditTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            firstEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
            firstEditText.inputType = InputType.TYPE_CLASS_TEXT
//            firstEditText.requestFocus()
            getNextQuestionOptionsBlanks(dataList[questionNum].question)
            chosenAnswer[questionNum] =
                firstEditText.text.toString() + ", " + secondEditText.text.toString()
        }

        override fun afterTextChanged(s: Editable?) {
        }

    }
    private var secondEditTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            secondEditText.imeOptions = EditorInfo.IME_ACTION_GO
            secondEditText.inputType = InputType.TYPE_CLASS_TEXT
//            secondEditText.requestFocus()
            getNextQuestionOptionsBlanks(dataList[questionNum].question)
            chosenAnswer[questionNum] =
                firstEditText.text.toString() + ", " + secondEditText.text.toString()
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }
    private fun turnOnOff(flag:Boolean){
        if(flag)
        {
            firstEditText.addTextChangedListener(firstEditTextWatcher)
            secondEditText.addTextChangedListener(secondEditTextWatcher)
        }
        else
        {
            firstEditText.removeTextChangedListener(firstEditTextWatcher)
            secondEditText.removeTextChangedListener(secondEditTextWatcher)
        }
    }

    private fun clearPrev() {
        for (id in textViewIds) {
            questionLayout.removeView(findViewById(id))

        }
        for (id in textViewIds2) {
            questionLayout.removeView(findViewById(id))
        }
        for (id in textViewIds3) {
            questionLayout.removeView(findViewById(id))
        }

        textViewIds.clear()
        textViewIds2.clear()
        textViewIds3.clear()
        textViewWidth = 0
        textViewWidth1 = 0
        textViewWidth2 = 0
        availableWidth = 0.0f
        startOrEnd = ConstraintSet.START
        topOrBottom = ConstraintSet.TOP
        curr = 0
        topId = Vector<Int>()
        leftId = questionLayout.id
    }

    private fun splitText(textS: String): MutableList<Int> {
        val textPaint = TextView(this).paint
        availableWidth = questionLayout.width.toFloat() * 0.85f
        val firstAvailableWidth = availableWidth - textViewWidth1 - textViewWidth2

        var br = 0
        var remainingText = textS
        val textViewId = mutableListOf<Int>()
        while (remainingText.isNotEmpty()) {
            var ind = if (br != 0) textPaint.breakText(remainingText, true, availableWidth, null)
            else textPaint.breakText(remainingText, true, firstAvailableWidth, null)
            br++
            println("$ind !! $remainingText")
            if (ind + 1 < remainingText.length && remainingText[ind + 1] != ' ') if (remainingText.lastIndexOf(
                    ' ',
                    ind
                ) != -1
            ) ind = remainingText.lastIndexOf(' ', ind)
            println(ind)
            var textToShow = remainingText.substring(0, ind)
            remainingText = if (ind + 1 < remainingText.length) remainingText.substring(ind)
            else ""
            println("$textToShow !! $remainingText")
            if (textToShow == " ") textToShow = ""
            else if (textToShow.isNotEmpty() && textToShow[0] == ' ') textToShow =
                textToShow.substring(1)
            val textView = TextView(this).apply {
                text = textToShow
                id = View.generateViewId()
                textSize = 16f
                setTextColor(Color.BLACK)
            }
            textView.measure(widthMeasureSpec, heightMeasureSpec)
            textViewWidth = textView.measuredWidth
            println("TextView width: $textViewWidth")
            questionLayout.addView(textView)
            textViewId.add(textView.id)
        }
        return textViewId
    }

    private fun setConstraintEditText(editText: EditText) {
        println("!!" + textViewWidth1)
        println(textViewWidth2)
        val constraintSet3 = ConstraintSet().apply {
            clone(questionLayout)
            connect(editText.id, ConstraintSet.END, questionLayout.id, ConstraintSet.END)
            connect(
                editText.id,
                ConstraintSet.BOTTOM,
                questionLayout.id,
                ConstraintSet.BOTTOM
            )
            if ((textViewWidth1 + textViewWidth2 + editText.width) * 0.9 > availableWidth) {
                leftId = questionLayout.id
                startOrEnd = ConstraintSet.START
                textViewWidth1 = 0
                textViewWidth2 = 0
            } else {
                startOrEnd = ConstraintSet.END
                if (curr > 0) curr--
                if (curr == 0) topOrBottom = ConstraintSet.TOP
            }
            connect(
                editText.id,
                ConstraintSet.START,
                leftId,
                startOrEnd,
            )
            connect(
                editText.id, ConstraintSet.TOP, topId[curr], topOrBottom
            )
            if (curr == 0) setVerticalBias(editText.id, 0.1f)
            else setVerticalBias(editText.id, 0.0f)
            setHorizontalBias(editText.id, 0.01f)
            applyTo(questionLayout)
        }
        editText.visibility = TextView.VISIBLE
    }

    private fun setConstraintTextView(Ids: MutableList<Int>) {
        println(Ids.size)
        for (i in Ids.indices) {
            println(i)
            println(findViewById<TextView>(Ids[i]).text)
            val constraintSet = ConstraintSet().apply {
                clone(questionLayout)
                connect(
                    Ids[i],
                    ConstraintSet.END,
                    questionLayout.id,
                    ConstraintSet.END,
                )
                connect(
                    Ids[i],
                    ConstraintSet.BOTTOM,
                    questionLayout.id,
                    ConstraintSet.BOTTOM
                )
                connect(
                    Ids[i],
                    ConstraintSet.TOP,
                    topId[curr],
                    topOrBottom
                )
                connect(
                    Ids[i],
                    ConstraintSet.START,
                    leftId,
                    startOrEnd
                )
                if (curr == 0) setVerticalBias(Ids[i], 0.1f)
                else setVerticalBias(Ids[i], 0.0f)
                setHorizontalBias(Ids[i], 0.0f)
                curr++
                topOrBottom = ConstraintSet.BOTTOM
                startOrEnd = ConstraintSet.START
                if (curr == topId.size) topId.add(Ids[i])
                leftId = if (Ids.size == i + 1) {
                    Ids[i]
                } else questionLayout.id
                applyTo(questionLayout)
            }
        }
    }

    private fun getNextQuestionOptionsBlanks(questionText: String, first: Boolean = false) {
        clearPrev()
        println(questionText)
        if(first)firstEditText.requestFocus()
        val parts =
            (" $questionText ").split(
                '_'
            )
        topId.add(questionLayout.id)
        leftId = questionLayout.id
        textViewIds = splitText(parts[0])
        textViewWidth1 = textViewWidth
        setConstraintTextView(textViewIds)
        setConstraintEditText(firstEditText)
        textViewWidth1 += firstEditText.width
        var whichRow = curr
        leftId = firstEditText.id
        textViewIds2 = splitText(parts[1])
        textViewWidth2 = textViewWidth
        startOrEnd = ConstraintSet.END
        setConstraintTextView(textViewIds2)
        if (whichRow < curr - 1) textViewWidth1 = 0
        setConstraintEditText(secondEditText)
        textViewWidth2 += secondEditText.width
        leftId = secondEditText.id
        var br = 0
        textViewIds3 = splitText(parts[2].dropLast(1))
        startOrEnd = ConstraintSet.END
        setConstraintTextView(textViewIds3)
    }

    private fun checkAnswer(questionNumK: Int = questionNum) {
        println(chosenAnswer[questionNumK])

        if (questionNumK > 39) println(dataList[questionNumK].answer)
        if (chosenAnswer[questionNumK] == dataList[questionNumK].answer) correct.set(
            questionNumK
        )
        else correct.clear(questionNumK)
        if (chosenAnswer[questionNumK] == "" || (questionNumK in 39..59 && chosenAnswer[questionNumK].split(
                ", "
            ).size < 2)
        ) {
            used.clear(questionNumK)
            findViewById<Button>((questionNumK + 1) * 1337).setBackgroundResource(R.drawable.rectangle_button)
        } else {
            used.set(questionNumK)
            findViewById<Button>((questionNumK + 1) * 1337).setBackgroundResource(R.drawable.rectangle_button_clicked)
        }
    }

    private fun getNextQuestionOptions(i: Int) {
        val nextQuestion = dataList[i]
        question.text = nextQuestion.question
        isClicked1 = false;isClicked2 = false;isClicked3 = false;isClicked4 = false
        options[0].setOnClickListener {
            changeState(options[0], isClicked1)
            isClicked1 = !isClicked1; isClicked2 = false; isClicked3 = false; isClicked4 = false
            if (isClicked1 && questionNum < 40) chosenAnswer[questionNum] = "а"
            else if (isClicked1 && questionNum >= 60) chosenAnswer[questionNum] = "да"
            else chosenAnswer[questionNum] = ""
        }
        options[1].setOnClickListener {
            changeState(options[1], isClicked2)
            isClicked2 = !isClicked2; isClicked1 = false; isClicked3 = false; isClicked4 = false
            if (isClicked2 && questionNum < 40) chosenAnswer[questionNum] = "б"
            else if (isClicked2 && questionNum >= 60) chosenAnswer[questionNum] = "не"
            else chosenAnswer[questionNum] = ""
        }
        if (questionNum < 40) {
            options[0].text = nextQuestion.option1
            options[1].text = nextQuestion.option2
            options[2].text = nextQuestion.option3
            options[3].text = nextQuestion.option4
            isClicked1 = false; isClicked2 = false; isClicked3 = false; isClicked4 = false

            if (questionNum < 40) options[2].setOnClickListener {
                changeState(options[2], isClicked3)
                isClicked3 = !isClicked3; isClicked1 = false; isClicked2 = false; isClicked4 = false
                if (isClicked3) chosenAnswer[questionNum] = "в"
                else chosenAnswer[questionNum] = ""
            }
            if (questionNum < 40) options[3].setOnClickListener {
                changeState(options[3], isClicked4)
                isClicked4 = !isClicked4; isClicked1 = false; isClicked2 = false; isClicked3 = false
                if (isClicked4) chosenAnswer[questionNum] = "г"
                else chosenAnswer[questionNum] = ""
            }
        } else if (questionNum < 80) {
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
        if (questionNum < 40) {
            question.text = dataList[questionNum].question
            options[0].text = dataList[questionNum].option1
            options[1].text = dataList[questionNum].option2
            options[2].text = dataList[questionNum].option3
            options[3].text = dataList[questionNum].option4
        } else if (questionNum < 60) {

        } else if (questionNum < 80) {
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
        dataList = set1Data.subList(0, 20) + set2Data.subList(0, 20) + set3Data.subList(
            0, 20
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
        questionLayout = findViewById(R.id.questionBackground2)
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
        turnOnOff(true)
    }

    private fun blankQuestions() {
        question.visibility = TextView.INVISIBLE

        questionLayout.visibility = LinearLayout.VISIBLE
        for (button in options) button.visibility = Button.INVISIBLE
    }

    private fun fourOptions() {
        question.visibility = TextView.VISIBLE
        questionLayout.visibility = LinearLayout.INVISIBLE
        for (button in options) button.visibility = Button.VISIBLE
        if (questionNum == 0) {
            changeState(options[0], true)
            prevButton.visibility = Button.INVISIBLE
        }
        finishButton.visibility = Button.INVISIBLE
        nextButton.visibility = Button.VISIBLE
        for (button in options) button.setOnClickListener(null)
    }

    private fun twoOptions() {
        question.visibility = TextView.VISIBLE
        questionLayout.visibility = LinearLayout.INVISIBLE
        options[0].visibility = Button.VISIBLE
        options[1].visibility = Button.VISIBLE
        options[2].visibility = Button.INVISIBLE
        options[3].visibility = Button.INVISIBLE
    }

    private fun showAnswerOptions() {
        for (option in options) option.setBackgroundResource(R.drawable.rectangle_button)
        if (chosenAnswer[questionNum] != "") options[map[chosenAnswer[questionNum]]!!].setBackgroundResource(
            R.drawable.rectangle_button_wrong
        )
        options[map[dataList[questionNum].answer]!!].setBackgroundResource(R.drawable.rectangle_button_correct)

    }

    private fun showAnswerBlanks() {
        question.visibility = TextView.VISIBLE
        questionLayout.visibility = LinearLayout.INVISIBLE
        var textC = " " + dataList[questionNum].question + " "
        val parts = textC.split('_')
        println(questionNum)
        println(chosenAnswer[questionNum] + "!!!")
        if (chosenAnswer[questionNum] == "" || chosenAnswer[questionNum] == ", ") textC =
            parts[0].substring(1) + "(" + dataList[questionNum].answer.split(", ")[0] + ")" + parts[1] + "(" + dataList[questionNum].answer.split(
                ", "
            )[1] + ")" + parts[2].dropLast(1)
        else if (chosenAnswer[questionNum].split(", ")[0] == dataList[questionNum].answer.split(", ")[0] && chosenAnswer[questionNum].split(
                ", "
            )[1] == dataList[questionNum].answer.split(", ")[1]
        ) textC =
            parts[0].substring(1) + chosenAnswer[questionNum].split(", ")[0] + parts[1] + chosenAnswer[questionNum].split(
                ", "
            )[1] + parts[2].dropLast(1)
        else if (chosenAnswer[questionNum].split(", ")[0] == dataList[questionNum].answer.split(", ")[0]) textC =
            parts[0].substring(1) + chosenAnswer[questionNum].split(", ")[0] + parts[1] + chosenAnswer[questionNum].split(
                ", "
            )[1] + "(" + dataList[questionNum].answer.split(", ")[1] + ")" + parts[2].dropLast(1)
        else textC =
            parts[0].substring(1) + chosenAnswer[questionNum].split(", ")[0] + "(" + dataList[questionNum].answer.split(
                ", "
            )[0] + ")" + parts[1] + chosenAnswer[questionNum].split(", ")[1] + parts[2].dropLast(1)
        var spannableString = SpannableString(textC)
        for (i in 0..1) {
            var word = ""
            if (chosenAnswer[questionNum] != "" && chosenAnswer[questionNum] != ", " && chosenAnswer[questionNum].split(
                    ", "
                ).size > i
            ) word = chosenAnswer[questionNum].split(", ")[i]
            if (word == "") continue
            val startIndex = textC.indexOf(word)
            val endIndex = startIndex + word.length
            if (chosenAnswer[questionNum].split(", ")[i] == dataList[questionNum].answer.split(", ")[i]) spannableString.setSpan(
                ForegroundColorSpan(GREEN),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            else {
                spannableString.setSpan(
                    ForegroundColorSpan(RED),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        question.text = spannableString
    }

    private fun showResult() {
        setNavBar(1)
        findViewById<HorizontalScrollView>(R.id.scrollView).smoothScrollTo(0, 0)
        textResult.text = "Result: " + correct.cardinality() + "/80"
        textResult.visibility = TextView.VISIBLE
        fourOptions()
        questionNum = 0
        showQuestion()
        showAnswerOptions()
        nextButton.setOnClickListener {
            if (questionNum == 0) prevButton.visibility = Button.VISIBLE
            if (questionNum == 39) blankQuestions()
            if (questionNum == 59) twoOptions()
            if (questionNum == 78) {
                nextButton.visibility = Button.INVISIBLE
                startOverButton.visibility = Button.VISIBLE
            }
            questionNum++
            showQuestion()
            if (questionNum < 40) showAnswerOptions()
            else if (questionNum < 60) showAnswerBlanks()
            else if (questionNum < 80) showAnswerOptions()

        }
        prevButton.setOnClickListener {
            questionNum--;
            if (questionNum == 0) prevButton.visibility = Button.VISIBLE
            if (questionNum == 39) fourOptions()
            if (questionNum == 69) blankQuestions()
            if (questionNum == 78) {
                nextButton.visibility = Button.INVISIBLE
                startOverButton.visibility = Button.VISIBLE
            }
            showQuestion()
            if (questionNum < 40) showAnswerOptions()
            else if (questionNum < 60) showAnswerBlanks()
            else if (questionNum < 80) showAnswerOptions()
        }
        startOverButton.setOnClickListener {
            startOverButton.visibility = Button.INVISIBLE
            nextButton.visibility = Button.VISIBLE
            prevButton.visibility = Button.INVISIBLE

            newTest()
        }
    }

    private fun showQuestionOptions(flag: Boolean = false, questionNumK: Int = questionNum) {
        if (chosenAnswer[questionNum] != "") changeState(
            options[map[chosenAnswer[questionNum]]!!], false
        )
        else changeState(options[0], true)
        getNextQuestionOptions(questionNum)
    }

    private fun showQuestionBlanks(questionNumK: Int = questionNum) {
        turnOnOff(false)
        if (chosenAnswer[questionNum] != "" || chosenAnswer[questionNum] != ", ") firstEditText.setText(
            chosenAnswer[questionNum].split(", ")[0]
        )
        else firstEditText.setText("")
        turnOnOff(true)
        if (chosenAnswer[questionNum].split(", ").size > 1) secondEditText.setText(
            chosenAnswer[questionNum].split(
                ", "
            )[1]
        )
        else secondEditText.setText("")
        getNextQuestionOptionsBlanks(dataList[questionNum].question,true)

    }

    private fun newTest() {
        setNavBar(0)
        textResult.visibility = TextView.INVISIBLE
        for (option in options) option.setBackgroundResource(R.drawable.rectangle_button)
        correct.clear()
        chosenAnswer = Array(80) { "" }
        newDataSet()
        fourOptions()
        questionNum = 0
        getNextQuestionOptions(questionNum)
        nextButton.setOnClickListener {
            val location = IntArray(2)
            findViewById<Button>((questionNum + 1) * 1337).getLocationOnScreen(location)
            val scrollViewLocation = IntArray(2)
            findViewById<HorizontalScrollView>(R.id.scrollView).getLocationOnScreen(
                scrollViewLocation
            )
            val relativeX = location[0] - scrollViewLocation[0]
            findViewById<HorizontalScrollView>(R.id.scrollView).smoothScrollTo(relativeX, 0)
            if (questionNum == 0) prevButton.visibility = Button.VISIBLE
            if (questionNum == 39) blankQuestions()
            if (questionNum == 59) twoOptions()
            if (questionNum == 78) {
                nextButton.visibility = Button.INVISIBLE
                finishButton.visibility = Button.VISIBLE
            }
            checkAnswer()
            questionNum++;

            if (questionNum < 40) showQuestionOptions()
            else if (questionNum < 60) showQuestionBlanks()
            else if (questionNum < 80) showQuestionOptions(true)

        }
        prevButton.setOnClickListener {
            checkAnswer()
            questionNum--;
            if (questionNum == 0) prevButton.visibility = Button.INVISIBLE
            if (questionNum == 39) fourOptions()
            if (questionNum == 59) blankQuestions()
            if (questionNum == 78) {
                nextButton.visibility = Button.VISIBLE
                finishButton.visibility = Button.INVISIBLE
            }
            if (questionNum < 40) showQuestionOptions()
            else if (questionNum < 60) showQuestionBlanks()
            else if (questionNum < 80) showQuestionOptions()
        }
        finishButton.setOnClickListener {
            questionNum++;
            checkAnswer()
            showResult()
        }
    }

    private fun setNavBar(flag: Int) {

        for (i in 1..80) {
            val button = findViewById<Button>(i * 1337)
            if (flag == 0) {
                findViewById<Button>(i * 1337).setBackgroundResource(R.drawable.rectangle_button)
                button.setOnClickListener {
                    val oldnum = questionNum
                    if (questionNum != 0) checkAnswer(questionNum)
                    questionNum = i - 1;
                    if (questionNum == 0) prevButton.visibility = Button.INVISIBLE
                    else prevButton.visibility = Button.VISIBLE
                    if (questionNum <= 39) fourOptions()
                    else if (questionNum <= 59) blankQuestions()
                    else if (questionNum <= 79) twoOptions()
                    if (questionNum <= 78) {
                        nextButton.visibility = Button.VISIBLE
                        finishButton.visibility = Button.INVISIBLE
                    } else {
                        nextButton.visibility = Button.INVISIBLE
                        finishButton.visibility = Button.VISIBLE
                    }
                    if (questionNum < 40) showQuestionOptions(false, oldnum)
                    else if (questionNum < 60) showQuestionBlanks(oldnum)
                    else if (questionNum < 80) showQuestionOptions(false, oldnum)
                }
            } else {
                if (chosenAnswer[i - 1] != dataList[i - 1].answer) findViewById<Button>(i * 1337).setBackgroundResource(
                    R.drawable.rectangle_button_wrong
                )
                else findViewById<Button>(i * 1337).setBackgroundResource(R.drawable.rectangle_button_correct)
                button.setOnClickListener {

                    questionNum = i - 1;
                    if (questionNum == 0) prevButton.visibility = Button.INVISIBLE
                    else prevButton.visibility = Button.VISIBLE
                    if (questionNum <= 39) fourOptions()
                    else if (questionNum <= 59) blankQuestions()
                    else if (questionNum <= 79) twoOptions()
                    if (questionNum <= 78) {
                        nextButton.visibility = Button.VISIBLE
                        startOverButton.visibility = Button.INVISIBLE
                    } else {
                        nextButton.visibility = Button.INVISIBLE
                        startOverButton.visibility = Button.VISIBLE
                    }
                    if (questionNum < 40) showAnswerOptions()
                    else if (questionNum < 60) showAnswerBlanks()
                    else if (questionNum < 80) showAnswerOptions()
                    showQuestion()
                }
            }
        }
    }

    private fun addButtons() {
        val horizontalScrollView = findViewById<HorizontalScrollView>(R.id.scrollView)
        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)

// Enable fading edge for horizontal scroll
        horizontalScrollView.isHorizontalFadingEdgeEnabled = true
        horizontalScrollView.setFadingEdgeLength(50)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val buttonWidthPercent = 0.15f
        val buttonHeightPercent = 0.05f

        val buttonWidth = (screenWidth * buttonWidthPercent).toInt()
        val buttonHeight = (screenHeight * buttonHeightPercent).toInt()
        for (i in 1..80) {
            horizontalScrollView.setFadingEdgeLength(100)
            val button = Button(this)
            val layoutParams = LinearLayout.LayoutParams(
                buttonWidth, buttonHeight
            )
            layoutParams.setMargins(10, 0, 10, 0)
            button.layoutParams = layoutParams
            button.text = "$i"
            button.id = i * 1337 // Give each button a unique ID
            button.setBackgroundResource(R.drawable.rectangle_button)
            linearLayout.addView(button)
        }
        setNavBar(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.questionLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initialize()
        addButtons()
        newTest()
    }
}