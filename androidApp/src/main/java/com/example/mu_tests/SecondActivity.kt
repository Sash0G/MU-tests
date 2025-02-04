package com.example.mu_tests

import android.graphics.Color
import android.graphics.Color.YELLOW
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.text.Editable
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.transition.Fade
import android.util.TypedValue
import android.view.View
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
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.BitSet
import androidx.constraintlayout.widget.ConstraintSet
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import org.apache.commons.text.similarity.LevenshteinDistance
import java.io.File
import java.util.Vector
import kotlin.random.Random

class SecondActivity : AppCompatActivity() {
    private lateinit var options: List<Button>
    private lateinit var question: TextView
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var finishButton: Button
    private lateinit var startOverButton: Button
    private lateinit var firstEditText: EditText
    private lateinit var secondEditText: EditText
    private lateinit var textResult: TextView
    private lateinit var questionLayout: ConstraintLayout
    private val correct = BitSet(160)
    private val used = BitSet(160)
    private var questionNum = 0
    private var isClicked1 = false
    private var isClicked2 = false
    private var isClicked3 = false
    private var isClicked4 = false
    private var chosenAnswer = Array(80) { "" }
    private val map = mapOf("а" to 0, "б" to 1, "в" to 2, "г" to 3, "да" to 0, "не" to 1)
    private val randomNum = 1000000007

    @Parcelize
    data class DataFormat(
        val question: String,
        val option1: String,
        val option2: String,
        val option3: String,
        val option4: String,
        val answer: String,
        var part: String = "NULL"
    ) : Parcelable

    @Parcelize
    data class MyData(
        val questions: List<DataFormat>,
        val answers: Array<String>,
        var result: Int
    ) : Parcelable

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
    val locations = mutableListOf<Array<Int>>()
    private var lastFirstWidth = 0;
    private var lastSecondWidth = 0
    private var backPressedTime: Long = 0
    private val backPressThreshold = 2000
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var themes: ArrayList<String>? = null
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - backPressedTime < backPressThreshold) {
            finish()
            super.onBackPressed()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        } else {
            backPressedTime = currentTime
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToMyTests() {
        val data = MyData(
            questions = dataList,
            answers = chosenAnswer,
            result = correct.cardinality()
        )
        val file = File(this.filesDir, "data.json")
        val testList: MutableList<MyData>
        val gson = Gson()
        if (file.exists()) {
            val fileContent = file.readText()
            testList = gson.fromJson(fileContent, object : TypeToken<MutableList<MyData>>() {}.type)
        } else testList = mutableListOf()
        testList.add(data)
        file.writeText(gson.toJson(testList))
    }

    private val editTextWatcher = { editText: EditText, imeOptions: Int ->
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                editText.imeOptions = imeOptions
//                editText.inputType = InputType.TYPE_CLASS_TEXT
                runnable?.let { handler.removeCallbacks(it) }
                runnable = Runnable {
                    if (firstEditText.width != lastFirstWidth || secondEditText.width != lastSecondWidth) {
                        getNextQuestionOptionsBlanks(dataList[questionNum].question, true)
                    }
                }
                handler.postDelayed(runnable!!, 0)
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }


    private fun turnOnOff(flag: Boolean) {
        if (flag) {
            firstEditText.addTextChangedListener(
                editTextWatcher(
                    firstEditText,
                    EditorInfo.IME_ACTION_NEXT
                )
            )
            secondEditText.addTextChangedListener(
                editTextWatcher(
                    secondEditText,
                    EditorInfo.IME_ACTION_GO
                )
            )
        } else {
            firstEditText.removeTextChangedListener(
                editTextWatcher(
                    firstEditText,
                    EditorInfo.IME_ACTION_NEXT
                )
            )
            secondEditText.removeTextChangedListener(
                editTextWatcher(
                    secondEditText,
                    EditorInfo.IME_ACTION_GO
                )
            )
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
            if (ind + 1 < remainingText.length && remainingText[ind] != ' ') if (remainingText.lastIndexOf(
                    ' ',
                    ind
                ) != -1
            ) ind = remainingText.lastIndexOf(' ', ind)
            println(remainingText.lastIndexOf(' ', ind))
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
            if (remainingText.isEmpty()) {
                textView.measure(widthMeasureSpec, heightMeasureSpec)
                textViewWidth = textView.measuredWidth
            }
            questionLayout.addView(textView)
            textViewId.add(textView.id)
        }
        return textViewId
    }

    private fun setConstraintEditText(editText: EditText) {
        println("!!" + textViewWidth1)
        println(textViewWidth2)
        ConstraintSet().apply {
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
        val constraintSet = ConstraintSet().apply {
            clone(questionLayout)
        }
        println(Ids.size)
        for (viewId in Ids) {
            val view = findViewById<TextView>(viewId)
            if (view.text.isNotEmpty() && leftId != questionLayout.id && view.text[0] != '.' && view.text[0] != ',' && view.text[0] != ')'
            ) {
                view.text =
                    " " + view.text.toString()
            }
            constraintSet.connect(viewId, ConstraintSet.END, questionLayout.id, ConstraintSet.END)
            constraintSet.connect(
                viewId,
                ConstraintSet.BOTTOM,
                questionLayout.id,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(viewId, ConstraintSet.START, leftId, startOrEnd)
            constraintSet.connect(viewId, ConstraintSet.TOP, topId[curr], topOrBottom)
            constraintSet.setVerticalBias(viewId, if (curr == 0) 0.1f else 0.0f)
            constraintSet.setHorizontalBias(viewId, 0.0f)
            if (curr == 0) constraintSet.setVerticalBias(viewId, 0.1f)
            else constraintSet.setVerticalBias(viewId, 0.0f)
            constraintSet.setHorizontalBias(viewId, 0.0f)
            curr++
            topOrBottom = ConstraintSet.BOTTOM
            startOrEnd = ConstraintSet.START
            if (curr == topId.size) topId.add(viewId)
            leftId = if (Ids.last() == viewId) {
                viewId
            } else questionLayout.id
        }

        constraintSet.applyTo(questionLayout)
    }

    private fun getNextQuestionOptionsBlanks(questionText: String, first: Boolean = false) {
        println(questionText)
        lastFirstWidth = firstEditText.width
        lastSecondWidth = secondEditText.width
        clearPrev()
        val parts =
            (" $questionText ").split(
                '_'
            )
        startOrEnd = ConstraintSet.START
        topOrBottom = ConstraintSet.TOP
        leftId = questionLayout.id
        topId.add(questionLayout.id)
        textViewIds = splitText(parts[0])
        setConstraintTextView(textViewIds)
        textViewWidth1 = textViewWidth
        setConstraintEditText(firstEditText)
        textViewWidth1 += firstEditText.width
        val whichRow = curr
        leftId = firstEditText.id
        try {
            // Опитай се да разделиш текста и извикай splitText
            textViewIds2 = splitText(parts[1])
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().apply {
                log("Error processing parts: ${parts[0]}, - $questionText")
                recordException(e)
            }
        }
        textViewWidth2 = textViewWidth
        startOrEnd = ConstraintSet.END
        setConstraintTextView(textViewIds2)
        if (whichRow < curr - 1) textViewWidth1 = 0
        setConstraintEditText(secondEditText)
        textViewWidth2 += secondEditText.width
        leftId = secondEditText.id
        textViewIds3 = splitText(parts[2].dropLast(1))
        startOrEnd = ConstraintSet.END
        setConstraintTextView(textViewIds3)
        for (i in 0 until questionLayout.childCount) {
            val view = questionLayout.getChildAt(i)
            println("View at index $i: ${view.javaClass.simpleName}, ${findViewById<TextView>(view.id).text} ID: ${view.id}")
        }
    }

    private fun checkAnswer(questionNumK: Int = questionNum - 1) {
        if (chosenAnswer[questionNumK] != dataList[questionNumK].answer) {
            correct.clear(questionNumK)
            correct.clear(questionNumK + 80)
        } else if (questionNumK < 20 || questionNumK > 59) correct.set(questionNumK)
        else {
            correct.set(questionNumK)
            correct.set(questionNumK + 80)
        }
        if (questionNumK in 40..59 && chosenAnswer[questionNumK] != dataList[questionNumK].answer) {
            var answer = chosenAnswer[questionNumK].split(", ")
            var intendedAnswer = dataList[questionNumK].answer.split(", ")
            if (answer.size == 1) answer = listOf(answer[0], "")
            if (intendedAnswer.size == 1) intendedAnswer = listOf(intendedAnswer[0], "")
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
        if (chosenAnswer[questionNumK] == "" || chosenAnswer[questionNumK] == "_, _") {
            used.clear(questionNumK)
            findViewById<Button>((questionNumK + 1) * randomNum).setBackgroundResource(R.drawable.rectangle_button)
        } else {
            used.set(questionNumK)
            findViewById<Button>((questionNumK + 1) * randomNum).setBackgroundResource(R.drawable.rectangle_button_clicked)
        }
    }

    private fun getNextQuestionOptions(i: Int) {
        val nextQuestion = dataList[i]
        question.text = nextQuestion.question
        question.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        question.setAutoSizeTextTypeUniformWithConfiguration(
            6,  // Min text size (in sp)
            18,  // Max text size (in sp)
            1,   // Granularity of the text size change
            TypedValue.COMPLEX_UNIT_SP
        )
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
            for (option in options) {
                option.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                option.setAutoSizeTextTypeUniformWithConfiguration(
                    6,  // Min text size (in sp)
                    14,  // Max text size (in sp)
                    1,   // Granularity of the text size change
                    TypedValue.COMPLEX_UNIT_SP
                )
            }

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
            question.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            question.setAutoSizeTextTypeUniformWithConfiguration(
                6,  // Min text size (in sp)
                18,  // Max text size (in sp)
                1,   // Granularity of the text size change
                TypedValue.COMPLEX_UNIT_SP
            )
            for (option in options) {
                option.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                option.setAutoSizeTextTypeUniformWithConfiguration(
                    6,  // Min text size (in sp)
                    14,  // Max text size (in sp)
                    1,   // Granularity of the text size change
                    TypedValue.COMPLEX_UNIT_SP
                )
            }
        } else if (questionNum in 60..79) {
            question.text = dataList[questionNum].question
            options[0].text = "да"
            options[1].text = "не"
        }
    }

    private fun newDataSet() {

        set1Data = set1Data.shuffled(Random(System.currentTimeMillis()))
        set2Data = set2Data.shuffled(Random(System.currentTimeMillis()))
        set3Data = set3Data.shuffled(Random(System.currentTimeMillis()))
        set4Data = set4Data.shuffled(Random(System.currentTimeMillis()))
        if (set1Data.filter { it.part in themes!! }.size < 20 || set2Data.filter { it.part in themes!! }.size < 20 || set3Data.filter { it.part in themes!! }.size < 20 || set4Data.filter { it.part in themes!! }.size < 20) {
            Toast.makeText(this, "Няма достатъчно задачи в избраните теми", Toast.LENGTH_LONG)
                .show()
            finish()
            super.onBackPressed()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            dataList = set1Data.filter { it.part in themes!! }
        } else {
            dataList = set1Data.filter { it.part in themes!! }
                .subList(0, 20) + set2Data.filter { it.part in themes!! }
                .subList(0, 20) + set3Data.filter { it.part in themes!! }.subList(
                0, 20
            ) + set4Data.filter { it.part in themes!! }.subList(0, 20)
        }
    }

    private fun initialise() {
        question = findViewById(R.id.questionMain)
        nextButton = findViewById(R.id.next)
        prevButton = findViewById(R.id.prev)
        finishButton = findViewById(R.id.finish)
        startOverButton = findViewById(R.id.startOver)
        firstEditText = findViewById(R.id.firstEditText)
        secondEditText = findViewById(R.id.secondEditText)
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
        firstEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
        firstEditText.inputType = InputType.TYPE_CLASS_TEXT
        secondEditText.imeOptions = EditorInfo.IME_ACTION_GO
        secondEditText.inputType = InputType.TYPE_CLASS_TEXT
    }

    private fun blankQuestions() {
        question.visibility = TextView.INVISIBLE
        questionLayout.visibility = LinearLayout.VISIBLE
        for (button in options) button.visibility = Button.INVISIBLE
        turnOnOff(true)
    }

    private fun fourOptions() {
        turnOnOff(false)
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
        turnOnOff(false)
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
        FirebaseCrashlytics.getInstance().log(dataList[questionNum].question)
        var answer = chosenAnswer[questionNum].split(", ")
        var intendedAnswer = dataList[questionNum].answer.split(", ")
        if (answer.size == 1) answer = listOf(answer[0], "")
        if (intendedAnswer.size == 1) intendedAnswer = listOf(intendedAnswer[0], "")
        question.visibility = TextView.VISIBLE
        questionLayout.visibility = LinearLayout.INVISIBLE
        if (answer[0] == intendedAnswer[1] || answer[1] == intendedAnswer[0]
        ) answer = answer.reversed()
        var textC = " " + dataList[questionNum].question + " "
        val parts = textC.split('_')
        val ind = mutableListOf<Int>()
        ind.add(parts[0].length - 1)
        textC = parts[0].drop(1) + answer[0] + parts[1]
        ind.add(textC.length)
        textC += answer[1] + parts[2].dropLast(1)
//        if (LevenshteinDistance().apply(
//                dataList[questionNum].answer.split(", ")[0],
//                chosenAnswer[questionNum].split(", ")[0]
//            ) <= 2
//        ) textC = parts[0].drop(1) + "✅" + dataList[questionNum].answer.split(", ")[0] + parts[1]
//        else textC = parts[0].drop(1) + "❌" + chosenAnswer[questionNum].split(", ")[0] + parts[1]
//        ind.add(textC.length + 1)
//        if (LevenshteinDistance().apply(
//                dataList[questionNum].answer.split(", ")[1],
//                chosenAnswer[questionNum].split(", ")[1]
//            ) <= 2
//        ) textC += "✅" + dataList[questionNum].answer.split(", ")[1] + parts[2].dropLast(1)
//        else textC += "❌" + chosenAnswer[questionNum].split(", ")[1] + parts[2].dropLast(1)
        textC += "\n\nВерни отговори:"
        if(answer[0] == intendedAnswer[0])textC += "\n1. ✔"
        else if ((LevenshteinDistance().apply(
                intendedAnswer[0],
                answer[0]
            ) <= 2 && answer[0].length > 3)
        ) textC += "\n1. ✔ " + intendedAnswer[0]
        else textC += "\n1. ✖ " + intendedAnswer[0]
        if(answer[1] == intendedAnswer[1])textC +="\n2. "
        else if ((LevenshteinDistance().apply(
                intendedAnswer[1],
                answer[1]
            ) <= 2 && answer[1].length > 3)
        ) textC += "\n2. " + intendedAnswer[1]
        else textC += "\n2. ✖ " + intendedAnswer[1]

//        if (chosenAnswer[questionNum] == "_, _") textC =
//            parts[0].drop(1) + "_ (" + dataList[questionNum].answer.split(", ")[0] + ")" + parts[1] + "_ (" + dataList[questionNum].answer.split(
//                ", "
//            )[1] + ")" + parts[2].dropLast(1)
//        else if (chosenAnswer[questionNum].split(", ")[0] == dataList[questionNum].answer.split(", ")[0] && chosenAnswer[questionNum].split(
//                ", "
//            )[1] == dataList[questionNum].answer.split(", ")[1]
//        ) textC =
//            parts[0].drop(1) + chosenAnswer[questionNum].split(", ")[0] + parts[1] + chosenAnswer[questionNum].split(
//                ", "
//            )[1] + parts[2].dropLast(1)
//        else if (chosenAnswer[questionNum].split(", ")[0] == dataList[questionNum].answer.split(", ")[0]) textC =
//            parts[0].substring(1) + chosenAnswer[questionNum].split(", ")[0] + parts[1] + chosenAnswer[questionNum].split(
//                ", "
//            )[1] + "(" + dataList[questionNum].answer.split(", ")[1] + ")" + parts[2].dropLast(1)
//        else if (chosenAnswer[questionNum].split(", ")[1] == dataList[questionNum].answer.split(", ")[1]) textC =
//            parts[0].substring(1) + chosenAnswer[questionNum].split(", ")[0] + "(" + dataList[questionNum].answer.split(
//                ", "
//            )[0] + ")" + parts[1] + chosenAnswer[questionNum].split(", ")[1] + parts[2].dropLast(1)
//        else  textC =
//            parts[0].drop(1) +  chosenAnswer[questionNum].split(", ")[0] + " (" + dataList[questionNum].answer.split(", ")[0] + ")" + parts[1] +  chosenAnswer[questionNum].split(", ")[1] + " (" + dataList[questionNum].answer.split(
//                ", "
//            )[1] + ")" + parts[2].dropLast(1)
        val spannableString = SpannableString(textC)
        for (i in 0..1) {
            val word = answer[i]
            val startIndex = ind[i]
            val endIndex = startIndex + word.length
            if (answer[i] == intendedAnswer[i]) spannableString.setSpan(
                ForegroundColorSpan(Color.parseColor("#43A614")),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            else if (LevenshteinDistance().apply(intendedAnswer[i], answer[i]) <= 2) {
                spannableString.setSpan(
                    ForegroundColorSpan(Color.parseColor("#C3B103")),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spannableString.setSpan(
                    ForegroundColorSpan(Color.parseColor("#f23343")),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        question.text = spannableString
    }

    private fun showResult(result: Int = correct.cardinality(), flag: Boolean = false) {
        setNavBar(1, flag)
        textResult.text = "Result: " + result + "/120"
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
                if (!flag) startOverButton.visibility = Button.VISIBLE
            }
            questionNum++
            findViewById<HorizontalScrollView>(R.id.scrollView).scrollTo(
                locations[questionNum][0],
                locations[questionNum][1]
            )
            showQuestion()
            if (questionNum < 40) showAnswerOptions()
            else if (questionNum < 60) showAnswerBlanks()
            else if (questionNum < 80) showAnswerOptions()

        }
        prevButton.setOnClickListener {
            questionNum--;
            findViewById<HorizontalScrollView>(R.id.scrollView).scrollTo(
                locations[questionNum][0],
                locations[questionNum][1]
            )
            if (questionNum == 0) prevButton.visibility = Button.INVISIBLE
            if (questionNum == 39) fourOptions()
            if (questionNum == 59) blankQuestions()
            if (questionNum == 78) {
                nextButton.visibility = Button.VISIBLE
                startOverButton.visibility = Button.INVISIBLE
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

    private fun showQuestionOptions(questionNumK: Int = questionNum - 1) {
        if (questionNumK in 40..59) chosenAnswer[questionNumK] =
            firstEditText.text.toString().trim()
                .ifEmpty { "-" } + ", " + secondEditText.text.toString().trim().ifEmpty { "-" }
        if (chosenAnswer[questionNum] != "") changeState(
            options[map[chosenAnswer[questionNum]]!!], false
        )
        else changeState(options[0], true)
        getNextQuestionOptions(questionNum)
    }

    private fun showQuestionBlanks(questionNumK: Int = questionNum - 1) {
        if (questionNumK in 40..59) chosenAnswer[questionNumK] =
            firstEditText.text.toString().trim()
                .ifEmpty { "_" } + ", " + secondEditText.text.toString().trim().ifEmpty { "_" }
        turnOnOff(false)
        if (chosenAnswer[questionNum].split(", ")[0] != "_") firstEditText.setText(
            chosenAnswer[questionNum].split(", ")[0]
        )
        else firstEditText.setText("")

        if (chosenAnswer[questionNum].split(", ")[1] != "_") secondEditText.setText(
            chosenAnswer[questionNum].split(
                ", "
            )[1]
        )
        else secondEditText.setText("")
        lastFirstWidth = 0
        lastSecondWidth = 0
        turnOnOff(true)
        FirebaseCrashlytics.getInstance().log(dataList[questionNum].question)
        getNextQuestionOptionsBlanks(dataList[questionNum].question, true)
    }

    private fun newTest() {
        setNavBar(0)
        textResult.visibility = TextView.INVISIBLE
        for (option in options) option.setBackgroundResource(R.drawable.rectangle_button)
        correct.clear()
        chosenAnswer = Array(80) { "" }
        for (i in 40..59) chosenAnswer[i] = "_, _"
        newDataSet()
        fourOptions()
        questionNum = 0
        getNextQuestionOptions(questionNum)
        nextButton.setOnClickListener {
            println(locations.size)
            if (questionNum == 0) prevButton.visibility = Button.VISIBLE
            if (questionNum == 39) blankQuestions()
            if (questionNum == 59) twoOptions()
            if (questionNum == 78) {
                nextButton.visibility = Button.INVISIBLE
                finishButton.visibility = Button.VISIBLE
            }
            questionNum++;
            findViewById<HorizontalScrollView>(R.id.scrollView).smoothScrollTo(
                locations[questionNum][0],
                locations[questionNum][1]
            )
            if (questionNum < 40) showQuestionOptions()
            else if (questionNum < 60) showQuestionBlanks()
            else if (questionNum < 80) showQuestionOptions()
            checkAnswer()

        }
        prevButton.setOnClickListener {
            questionNum--;
            findViewById<HorizontalScrollView>(R.id.scrollView).smoothScrollTo(
                locations[questionNum][0],
                locations[questionNum][1]
            )
            if (questionNum == 0) prevButton.visibility = Button.INVISIBLE
            if (questionNum == 39) fourOptions()
            if (questionNum == 59) blankQuestions()
            if (questionNum == 78) {
                nextButton.visibility = Button.VISIBLE
                finishButton.visibility = Button.INVISIBLE
            }
            if (questionNum < 40) showQuestionOptions(questionNum + 1)
            else if (questionNum < 60) showQuestionBlanks(questionNum + 1)
            else if (questionNum < 80) showQuestionOptions(questionNum + 1)
            checkAnswer(questionNum + 1)
        }
        finishButton.setOnClickListener {
            questionNum++;
            checkAnswer()
            if (used.cardinality() == 80) {
                saveToMyTests()
                showResult()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmation")
                builder.setMessage("You haven't answered all questions. Are you sure you want to finish the test?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    saveToMyTests()
                    showResult()
                }
                builder.setNegativeButton("No") { dialog, which ->

                }
                questionNum--
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun setNavBar(flag: Int, showOldTest: Boolean = false) {
        findViewById<HorizontalScrollView>(R.id.scrollView).smoothScrollTo(0, 0)
        for (i in 1..80) {
            val button = findViewById<Button>(i * randomNum)
            if (flag == 0) {
                findViewById<Button>(i * randomNum).setBackgroundResource(R.drawable.rectangle_button)
                button.setOnClickListener {
                    val oldNum = questionNum
                    questionNum = i - 1;
                    findViewById<HorizontalScrollView>(R.id.scrollView).smoothScrollTo(
                        locations[questionNum][0],
                        locations[questionNum][1]
                    )
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
                    if (questionNum < 40) showQuestionOptions(oldNum)
                    else if (questionNum < 60) showQuestionBlanks(oldNum)
                    else if (questionNum < 80) showQuestionOptions(oldNum)
                    checkAnswer(oldNum)
                }
            } else {
                if ((i in 40..59 && !correct.get(i - 1 + 80)&& !correct.get(i - 1)) || !correct.get(i - 1)) findViewById<Button>(
                    i * randomNum
                ).setBackgroundResource(
                    R.drawable.rectangle_button_wrong
                )
                else if (i in 40..59 && (!correct.get(i - 1 + 80) || !correct.get(i - 1)))
                    findViewById<Button>(i * randomNum).setBackgroundResource(R.drawable.rectangle_button_mid)
                else findViewById<Button>(i * randomNum).setBackgroundResource(R.drawable.rectangle_button_correct)
                button.setOnClickListener {

                    questionNum = i - 1;
                    findViewById<HorizontalScrollView>(R.id.scrollView).smoothScrollTo(
                        locations[questionNum][0],
                        locations[questionNum][1]
                    )
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
                        if (!showOldTest) startOverButton.visibility = Button.VISIBLE
                    }
                    if (questionNum < 40) showAnswerOptions()
                    else if (questionNum < 60) showAnswerBlanks()
                    else if (questionNum < 80) showAnswerOptions()
                    showQuestion()
                }
            }
            findViewById<HorizontalScrollView>(R.id.scrollView).post {
                val location = IntArray(2)
                button.getLocationOnScreen(location)
                println("${location[0]} +${location[1]}")
                locations.add(arrayOf(location[0], location[1]))
            }
        }
        println(locations.size)
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
            button.id = i * randomNum // Give each button a unique ID
            button.setBackgroundResource(R.drawable.rectangle_button)
            linearLayout.addView(button)
        }
        setNavBar(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val fade = Fade(Fade.IN)
        window.enterTransition = fade
        window.exitTransition = fade
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.questionLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initialise()
        addButtons()
        val data = intent.getParcelableExtra<MyData>("test")
        themes = intent.getStringArrayListExtra("parts")
        if (themes != null) newTest()
        else {
            dataList = data!!.questions
            chosenAnswer = data.answers
            for (i in 0..79) checkAnswer(i)
            println(correct)
            showResult(data.result, true)

        }

        print(data)
    }
}
