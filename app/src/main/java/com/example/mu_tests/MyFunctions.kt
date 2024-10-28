package com.example.mu_tests

import android.content.Context
import com.example.mu_tests.SecondActivity.DataFormat
import com.example.mu_tests.SecondActivity.MyData
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.InputStreamReader
import com.google.gson.*
import java.io.File
import java.lang.reflect.Type
import java.util.BitSet

fun readGroup1(context: Context, fileName: String): List<DataFormat> {

    val csvParser = CSVParserBuilder().withSeparator(';').build()
    val data = mutableListOf<DataFormat>()
    val inputStream = context.assets.open(fileName)
    val reader =
        CSVReaderBuilder(InputStreamReader(inputStream)).withCSVParser(csvParser).build()

    var nextLine: Array<String>?
    while (reader.readNext().also { nextLine = it } != null) {
        if (nextLine?.size == 6) {
            val row = DataFormat(
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

fun readGroup2(context: Context, fileName: String): List<DataFormat> {

    val csvParser = CSVParserBuilder().withSeparator(';').build()
    val data = mutableListOf<DataFormat>()
    val inputStream = context.assets.open(fileName)
    val reader =
        CSVReaderBuilder(InputStreamReader(inputStream)).withCSVParser(csvParser).build()

    var nextLine: Array<String>?
    while (reader.readNext().also { nextLine = it } != null) {
        if (nextLine?.size == 2) {
            val row = DataFormat(
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

fun saveDataToFile(context: Context, data: MyData) {
    val gson = Gson()
    val jsonData = gson.toJson(data)
    val file = File(context.filesDir, "data.json")
    file.writeText(jsonData)
}

fun loadDataFromFile(context: Context): MyData? {
    val file = File(context.filesDir, "data.json")
    if (file.exists()) {
        val jsonData = file.readText()
        val gson = Gson()
        return gson.fromJson(jsonData, MyData::class.java)
    }
    return null
}
fun editFile(context: Context, fileName: String, newContent: MyData) {
    val file = File(context.filesDir, fileName)
    val currentContent = if (file.exists()) file.readText() else ""
    val updatedContent = currentContent + newContent
    file.writeText(updatedContent)
}