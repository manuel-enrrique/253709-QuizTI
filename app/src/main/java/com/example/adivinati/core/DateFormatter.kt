package com.example.adivinati.core

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Formateo de fechas. `java.time` está disponible de forma nativa desde
 * API 26, y el `minSdk` del proyecto es 26, por lo que no hace falta desugaring.
 */
object DateFormatter {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    /** Devuelve la fecha y hora actuales ya formateadas para mostrar. */
    fun now(zoneId: ZoneId = ZoneId.systemDefault()): String =
        formatter.format(Instant.now().atZone(zoneId))
}
