package com

import io.ktor.server.engine.*
import io.ktor.server.cio.*

fun main() {

    initDatabase()

    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        configureHttp()
        configureSerialization()
        configureRouting()
    }.start(wait = true)

}