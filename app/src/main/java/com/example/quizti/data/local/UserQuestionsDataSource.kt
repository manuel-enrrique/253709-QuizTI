package com.example.quizti.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.quizti.data.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class UserQuestionsDataSource(private val dataStore: DataStore<Preferences>) {
    private val userQuestionsKey = stringPreferencesKey("user_questions")

    val userQuestions: Flow<List<Question>> = dataStore.data.map { prefs ->
        val json = prefs[userQuestionsKey] ?: "[]"
        parseQuestions(json)
    }

    suspend fun addQuestion(question: Question) {
        dataStore.edit { prefs ->
            val currentJson = prefs[userQuestionsKey] ?: "[]"
            val currentList = parseQuestions(currentJson).toMutableList()
            currentList.add(question)
            prefs[userQuestionsKey] = toJson(currentList)
        }
    }

    private fun parseQuestions(json: String): List<Question> {
        val result = mutableListOf<Question>()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val optionsArray = obj.getJSONArray("options")
            val options = mutableListOf<String>()
            for (j in 0 until optionsArray.length()) {
                options.add(optionsArray.getString(j))
            }
            result.add(
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
        return result
    }

    private fun toJson(list: List<Question>): String {
        val array = JSONArray()
        list.forEach { q ->
            val obj = JSONObject()
            obj.put("id", q.id)
            obj.put("text", q.text)
            val optionsArray = JSONArray()
            q.options.forEach { optionsArray.put(it) }
            obj.put("options", optionsArray)
            obj.put("correctIndex", q.correctIndex)
            obj.put("category", q.category)
            obj.put("isUserCreated", q.isUserCreated)
            array.put(obj)
        }
        return array.toString()
    }
}
