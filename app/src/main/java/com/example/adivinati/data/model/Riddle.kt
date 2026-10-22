package com.example.adivinati.data.model

/**
 * Modelo de dominio de una adivinanza.
 *
 * Es una data class inmutable: la capa de presentación sólo puede leerla,
 * nunca modificarla. Cualquier cambio genera una copia nueva.
 *
 * @param id            Identificador único. En las semillas viene del JSON,
 *                      en las creadas por el usuario lo asigna el repositorio.
 * @param text          Enunciado de la adivinanza.
 * @param hint          Pista que se puede revelar durante la partida.
 * @param options       Lista de opciones (siempre 4 en esta app).
 * @param correctIndex  Índice de la opción correcta dentro de [options].
 * @param difficulty    Clave de dificultad ("FACIL", "MEDIA" o "DIFICIL").
 * @param isUserCreated `true` si la creó el usuario, `false` si viene de la semilla.
 */
data class Riddle(
    val id: Int,
    val text: String,
    val hint: String,
    val options: List<String>,
    val correctIndex: Int,
    val difficulty: String,
    val isUserCreated: Boolean,
)

/**
 * Niveles de dificultad de la aplicación.
 *
 * [key] es el valor estable que se persiste en el JSON y en DataStore,
 * mientras que [label] es el texto que se muestra en pantalla.
 */
enum class Difficulty(val key: String, val label: String) {
    FACIL("FACIL", "Fácil"),
    MEDIA("MEDIA", "Media"),
    DIFICIL("DIFICIL", "Difícil");

    companion object {
        /** Convierte la clave persistida en un [Difficulty]; si no existe, devuelve [FACIL]. */
        fun fromKey(key: String?): Difficulty =
            entries.firstOrNull { it.key == key } ?: FACIL
    }
}
