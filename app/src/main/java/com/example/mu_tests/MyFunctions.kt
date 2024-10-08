package com.example.mu_tests

import android.content.Context
import com.example.mu_tests.SecondActivity.BlankQuestions
import com.example.mu_tests.SecondActivity.FourOptions
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.InputStreamReader

fun readGroup1(context: Context, fileName: String): List<FourOptions> {

    val csvParser = CSVParserBuilder().withSeparator(';').build()
    val data = mutableListOf<FourOptions>()
    val inputStream = context.assets.open(fileName)
    val reader =
        CSVReaderBuilder(InputStreamReader(inputStream)).withCSVParser(csvParser).build()

    var nextLine: Array<String>?
    while (reader.readNext().also { nextLine = it } != null) {
        if (nextLine?.size == 6) {
            val row = FourOptions(
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
    reader.close()
    return data
}

fun readGroup2(context: Context, fileName: String): List<FourOptions> {

    val csvParser = CSVParserBuilder().withSeparator(';').build()
    val data = mutableListOf<FourOptions>()
    val inputStream = context.assets.open(fileName)
    val reader =
        CSVReaderBuilder(InputStreamReader(inputStream)).withCSVParser(csvParser).build()

    var nextLine: Array<String>?
    while (reader.readNext().also { nextLine = it } != null) {
        if (nextLine?.size == 2) {
            val row = FourOptions(
                question = nextLine!![0],
                answer = nextLine!![1],
                option1 = "",
                option2 = "",
                option3 = "",
                option4 = ""
            )
            data.add(row)
        }
    }
    reader.close()
    return data
}