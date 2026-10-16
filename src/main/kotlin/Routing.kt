package com

import com.models.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    // Carga de usuarios desde el archivo users.json en resources
    val usersJsonText = this::class.java.classLoader.getResourceAsStream("users.json")
        ?.bufferedReader()?.use { it.readText() } ?: "[]"

    val usersList: List<UserRecord> = Json.decodeFromString(usersJsonText)

    routing {
        get("/") {
            call.respondText("Servidor Pizzería Backend activo")
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val foundUser = usersList.find {
                it.username == request.username && it.password == request.password
            }

            if (foundUser != null) {
                call.respond(
                    LoginResponse(
                        success = true,
                        message = "Autenticación exitosa",
                        role = foundUser.role,
                        nombre = foundUser.nombre
                    )
                )
            } else {
                call.respond(
                    LoginResponse(
                        success = false,
                        message = "Credenciales incorrectas"
                    )
                )
            }
        }
    }
}