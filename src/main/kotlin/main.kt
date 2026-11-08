package com

import io.ktor.server.engine.*
import io.ktor.server.cio.*

fun main() {

    initDatabase()

    // Lee el puerto asignado por el servidor en la nube o usa 8080 para pruebas locales
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(CIO, port = port, host = "0.0.0.0") {
        configureHttp()
        configureSerialization()
        configureRouting()
    }.start(wait = true)

}