package com.example.quizti.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.quizti.data.model.QuizResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class QuizResultDataSource(private val dataStore: DataStore<Preferences>) {
    private val resultsKey = stringPreferencesKey("quiz_results")

    val quizResults: Flow<List<QuizResult>> = dataStore.data.map { prefs ->
        val json = prefs[resultsKey] ?: "[]"
        parseResults(json)
    }

    suspend fun saveResult(result: QuizResult) {
        dataStore.edit { prefs ->
            val currentJson = prefs[resultsKey] ?: "[]"
            val currentList = parseResults(currentJson).toMutableList()
            currentList.add(0, result)
            prefs[resultsKey] = toJson(currentList)
        }
    }

    private fun parseResults(json: String): List<QuizResult> {
        val result = mutableListOf<QuizResult>()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(
                QuizResult(
                    id = obj.getLong("id"),
                    category = obj.getString("category"),
                    score = obj.getInt("score"),
                    total = obj.getInt("total"),
                    date = obj.getString("date")
                )
            )
        }
        return result
    }

    private fun toJson(list: List<QuizResult>): String {
        val array = JSONArray()
        list.forEach { r ->
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("category", r.category)
            obj.put("score", r.score)
            obj.put("total", r.total)
            obj.put("date", r.date)
            array.put(obj)
        }
        return array.toString()
    }
}
