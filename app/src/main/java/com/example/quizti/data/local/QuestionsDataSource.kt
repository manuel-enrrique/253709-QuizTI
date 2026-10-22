package com.example.quizti.data.local

import android.content.Context
import com.example.quizti.R
import com.example.quizti.data.model.Question
import org.json.JSONArray
import org.json.JSONObject

class QuestionsDataSource(private val context: Context) {
    fun loadQuestionsFromRaw(): List<Question> {
        val inputStream = context.resources.openRawResource(R.raw.questions)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        inputStream.close()
        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<Question>()
        for (i in 0 until jsonArray.length()) {
            val obj: JSONObject = jsonArray.getJSONObject(i)
            val optionsArray = obj.getJSONArray("options")
            val options = mutableListOf<String>()
            for (j in 0 until optionsArray.length()) {
                options.add(optionsArray.getString(j))
            }
            list.add(
                Question(
                    id = obj.getInt("id"),
                    text = obj.getString("text"),
                    options = options,
                    correctIndex = obj.getInt("correctIndex"),
                    category = obj.getString("category"),
                    isUserCreated = obj.getBoolean("isUserCreated")
                )
            )
        }
        return list
    }
}
