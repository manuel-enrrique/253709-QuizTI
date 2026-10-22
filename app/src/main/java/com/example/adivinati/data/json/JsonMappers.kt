package com.example.adivinati.data.json

import com.example.adivinati.data.model.GameResult
import com.example.adivinati.data.model.Riddle
import org.json.JSONArray
import org.json.JSONObject

/**
 * Conversores entre los modelos de Kotlin y JSON.
 *
 * Se usa `org.json` (incluido en el SDK de Android) para no depender de
 * librerías ni plugins de serialización adicionales: la app es 100% offline
 * y sin dependencias de red.
 */
object JsonMappers {

    // ---------------------------------------------------------------- Riddles

    /** Convierte un array JSON de adivinanzas en una lista de [Riddle]. */
    fun parseRiddles(json: String): List<Riddle> {
        val array = JSONArray(json)
        return buildList(array.length()) {
            for (i in 0 until array.length()) {
                add(array.getJSONObject(i).toRiddle())
            }
        }
    }

    /** Convierte una lista de [Riddle] en un array JSON. */
    fun encodeRiddles(riddles: List<Riddle>): String {
        val array = JSONArray()
        riddles.forEach { array.put(it.toJson()) }
        return array.toString()
    }

    private fun JSONObject.toRiddle(): Riddle = Riddle(
        id = optInt(KEY_ID),
        text = optString(KEY_TEXT),
        hint = optString(KEY_HINT),
        options = optJSONArray(KEY_OPTIONS)?.toStringList().orEmpty(),
        correctIndex = optInt(KEY_CORRECT_INDEX),
        difficulty = optString(KEY_DIFFICULTY),
        isUserCreated = optBoolean(KEY_IS_USER_CREATED),
    )

    private fun Riddle.toJson(): JSONObject = JSONObject().apply {
        put(KEY_ID, id)
        put(KEY_TEXT, text)
        put(KEY_HINT, hint)
        put(KEY_OPTIONS, JSONArray(options))
        put(KEY_CORRECT_INDEX, correctIndex)
        put(KEY_DIFFICULTY, difficulty)
        put(KEY_IS_USER_CREATED, isUserCreated)
    }

    // ------------------------------------------------------------ GameResults

    /** Convierte un array JSON de resultados en una lista de [GameResult]. */
    fun parseGameResults(json: String): List<GameResult> {
        val array = JSONArray(json)
        return buildList(array.length()) {
            for (i in 0 until array.length()) {
                add(array.getJSONObject(i).toGameResult())
            }
        }
    }

    /** Convierte una lista de [GameResult] en un array JSON. */
    fun encodeGameResults(results: List<GameResult>): String {
        val array = JSONArray()
        results.forEach { array.put(it.toJson()) }
        return array.toString()
    }

    private fun JSONObject.toGameResult(): GameResult = GameResult(
        id = optLong(KEY_ID),
        difficulty = optString(KEY_DIFFICULTY),
        score = optInt(KEY_SCORE),
        correctAnswers = optInt(KEY_CORRECT_ANSWERS),
        date = optString(KEY_DATE),
    )

    private fun GameResult.toJson(): JSONObject = JSONObject().apply {
        put(KEY_ID, id)
        put(KEY_DIFFICULTY, difficulty)
        put(KEY_SCORE, score)
        put(KEY_CORRECT_ANSWERS, correctAnswers)
        put(KEY_DATE, date)
    }

    // ---------------------------------------------------------------- Utilidad

    private fun JSONArray.toStringList(): List<String> = buildList(length()) {
        for (i in 0 until length()) {
            add(optString(i))
        }
    }

    // Claves del JSON (compartidas por semillas y por lo persistido en DataStore)
    private const val KEY_ID = "id"
    private const val KEY_TEXT = "text"
    private const val KEY_HINT = "hint"
    private const val KEY_OPTIONS = "options"
    private const val KEY_CORRECT_INDEX = "correctIndex"
    private const val KEY_DIFFICULTY = "difficulty"
    private const val KEY_IS_USER_CREATED = "isUserCreated"
    private const val KEY_SCORE = "score"
    private const val KEY_CORRECT_ANSWERS = "correctAnswers"
    private const val KEY_DATE = "date"
}
